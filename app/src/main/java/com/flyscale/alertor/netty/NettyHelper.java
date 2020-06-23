package com.flyscale.alertor.netty;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.up.UHeart;
import com.flyscale.alertor.helper.DateHelper;
import com.flyscale.alertor.helper.ThreadPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.unix.DatagramSocketAddress;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 9:24
 * @DESCRIPTION 单例
 *     //ca验证改为双向验证
 *     //注意对接文档中的修改ca证书协议，增加了根证书和客户端私钥
 *     //可以在202.100.171.173 4000测试业务，202.100.190.14 9988测试连接
 */
public class NettyHelper {
    private static final NettyHelper ourInstance = new NettyHelper();

    public static final int DISCONNECTION = 1;
    public static final int CONNECTED = 2;
    public static final String TAG = "NettyHelper";
//    final String HOST = "202.100.190.14";
//    final int PORT = 9988;
    boolean isRunning = true;
    int mConnectStatus = DISCONNECTION;

    SslContext mSslContext;
    //默认的trustManager
    public final TrustManager DEFAULT_TrustManager = getDefaultTrustManager();
    Bootstrap mBootstrap;
    EventLoopGroup mGroup;
    ChannelFuture mChannelFuture;
    public Channel mChannel;
    Timer mTimer;
    //连接次数
    int mConnectCount = 0;

    public static NettyHelper getInstance() {
        return ourInstance;
    }

    private NettyHelper(){}

    public int getConnectCount() {
        return mConnectCount;
    }


    /**
     * 非同步
     */
    public void connect(){
        //todo
        // 重连这里始终有bug
        if(isConnect()){
            mChannel.close();
        }

        mConnectCount++;
        if(mConnectCount >= 10){
            PersistConfig.saveNewIp("",-1);
        }
        Log.i(TAG, "connect: mConnectCount = " + mConnectCount);
        ChannelFuture future = mBootstrap.connect(PersistConfig.findConfig().getIp(),PersistConfig.findConfig().getPort());
        Log.i(TAG, "connect: ----" + PersistConfig.findConfig().getIp() + PersistConfig.findConfig().getPort());
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) throws Exception {
                if(future.isSuccess()){
                    mChannelFuture = future;
                    mChannel = mChannelFuture.channel();
                    mConnectStatus = CONNECTED;
                    sendHeartLoop();
                    mConnectCount = 0;
                    if(!TextUtils.isEmpty(PersistConfig.findConfig().getNewIp())){
                        PersistConfig.saveIp(PersistConfig.findConfig().getNewIp());
                        PersistConfig.savePort(PersistConfig.findConfig().getNewPort());
                        PersistConfig.saveNewIp("",-1);
                    }
                }else {
                    future.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            Log.i(TAG, "run: future.channel().eventLoop().schedule00");
                            if(isRunning){
                                connect();
                            }
                        }
                    },2l, TimeUnit.SECONDS);
                }
            }
        });
    }




    /**
     * 初始化的时候必须注册
     */
    public void register(){
        try {
            mSslContext = SslContextBuilder.forClient()
                    .keyManager(BaseApplication.sContext.getAssets().open("client.crt")
                            ,BaseApplication.sContext.getAssets().open("pkcs8_client.key"))
                    // 这里由于android的限制7.0以上无法信任用户添加的证书
                    // 所以信任所有证书
                    // 也许会有隐患，有时间可以优化一下
                    // https://blog.csdn.net/shadowyspirits/article/details/79756274
                    // https://developer.android.google.cn/training/articles/security-config.html
                    .trustManager(DEFAULT_TrustManager).build();
            mBootstrap = new Bootstrap();
            mGroup = new NioEventLoopGroup();
            mBootstrap.group(mGroup);
            mBootstrap.channel(NioSocketChannel.class);
            mBootstrap.option(ChannelOption.SO_KEEPALIVE,true);
            mBootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    ByteBuf delimiter = Unpooled.copiedBuffer("]".getBytes());
                    pipeline.addLast(mSslContext.newHandler(ch.alloc()));
                    pipeline.addLast(new DelimiterBasedFrameDecoder(1048576, false,delimiter));
                    pipeline.addLast(new StringDecoder());
                    pipeline.addLast(new StringEncoder());
                    pipeline.addLast(new NettyHandler());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 销毁定时器
     */
    private void cancelTimer(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    /**
     * 发送心跳
     */
    private void sendHeartLoop(){
        cancelTimer();
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                send(new UHeart());
            }
        },1000,5 * 1000);
    }



    public void unRegister(){
        cancelTimer();
        isRunning = false;
        mConnectStatus = DISCONNECTION;
        if(mChannelFuture != null){
            mChannel.closeFuture();
            mChannel.close();
            mChannelFuture = null;
            mChannel = null;
        }
        if(mGroup != null){
            mGroup.shutdownGracefully();
            mGroup = null;
            mBootstrap = null;
        }
    }

    /**
     * 是否链接
     * @return
     */
    public boolean isConnect(){
        return mConnectStatus == CONNECTED && mChannel != null && mChannel.isActive();
    }

    public int getConnectStatus() {
        return mConnectStatus;
    }

    public void setConnectStatus(int connectStatus) {
        mConnectStatus = connectStatus;
    }

    /**
     * 根据实体类 发送报文
     * @param baseData
     */
    public void send(BaseData baseData){
        send(baseData.formatToString());
    }

    /**
     * 直接发送报文
     * @param message
     */
    public void send(String message){
        if(isConnect()){
            mChannel.writeAndFlush(message);
        }else {
            Log.i(TAG, "send: 发送消息失败 请检查长连接是否已经断开");
        }
    }


    /**
     * 获取默认的TrustManager
     * 信任所有证书
     * @return
     */
    private TrustManager getDefaultTrustManager(){
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };
        return trustAllCerts[0];
    }
}
