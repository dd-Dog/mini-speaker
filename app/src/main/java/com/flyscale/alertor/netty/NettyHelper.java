package com.flyscale.alertor.netty;

import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.FotaAction;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.packet.TcpPacket;
import com.flyscale.alertor.data.packet.TcpPacketFactory;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistPacket;
import com.flyscale.alertor.data.up.UChangeClientCa;
import com.flyscale.alertor.data.up.UChangeIP;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.FileHelper;
import com.flyscale.alertor.helper.FotaHelper;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 9:24
 * @DESCRIPTION 单例
 * //ca验证改为双向验证
 * //注意对接文档中的修改ca证书协议，增加了根证书和客户端私钥
 * //可以在202.100.171.173 4000测试业务，202.100.190.14 9988测试连接
 */
public class NettyHelper {
    private static final NettyHelper ourInstance = new NettyHelper();

    public static final String TAG = "NettyHelper";
    //    final String HOST = "202.100.190.14";
//    final int PORT = 9988;
    boolean isRunning = true;
    public static final String sIdleStateHandler = "sIdleStateHandler";
    public static final String sSslHandler = "sSslHandler";

    SslContext mSslContext;
    //默认的trustManager
    public final TrustManager DEFAULT_TrustManager = getDefaultTrustManager();
    Bootstrap mBootstrap;
    EventLoopGroup mGroup;
    ChannelFuture mChannelFuture;
    public Channel mChannel;
    ChannelFutureListener mChannelFutureListener = new MyChannelFutureListener();

    FotaHelper mFotaHelper;
    //连接次数
    int mConnectCount = 0;
    final int MAX_CONNECT_COUNT = 4;
    public String mChangeIpTradeNumResp = "";
    public String mChangeCaTradeNumResp = "";

    public static NettyHelper getInstance() {
        return ourInstance;
    }

    private NettyHelper() {
        mFotaHelper = new FotaHelper(BaseApplication.sContext, new FotaAction());
    }

    public int getConnectCount() {
        return mConnectCount;
    }

    public Bootstrap getBootstrap() {
        return mBootstrap;
    }

    /**
     * 耗时操作
     */
    public void connect() {
        if (isConnect()) {
            disconnectByChangeIp(null);
        }
        mConnectCount++;
        Log.i(TAG, "connect: mConnectCount = " + mConnectCount);
        if (mConnectCount >= MAX_CONNECT_COUNT) {
            PersistConfig.saveNewIp("", -1);
            Log.i(TAG, "connect: 回滚之后的ip及端口号........." + PersistConfig.findConfig().getNewIp());
            //这是修改ca，ip的情况 修改失败 要一起回滚
            if (!TextUtils.isEmpty(mChangeCaTradeNumResp)) {
                clearCaFile();
                //这里ca证书的回滚由于没有重新replace 有可能使用的新证书也有可能使用的老证书
                //使用老证书通过
                //使用新证书 但是新证书和老证书相同 通过
                //todo 待测 使用新证书 和老证书不一样 这种情况的回滚
                modifySslHandler(null, false);
            }
        }

        ChannelFuture future = mBootstrap.connect(PersistConfig.findConfig().getTcpHostNameDebug1(), PersistConfig.findConfig().getTcpPortDebug());
//        ChannelFuture future = mBootstrap.connect("192.168.1.130", 60000);
        Log.i(TAG, "connect: ----" + PersistConfig.findConfig().getTcpHostNameDebug1() + "...." + PersistConfig.findConfig().getTcpPortDebug());
        future.addListener(mChannelFutureListener);
    }

    public class MyChannelFutureListener implements ChannelFutureListener {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            mChannelFuture = future;
            mChannel = mChannelFuture.channel();
            Log.i(TAG, "operationComplete: \n" + mChannel.isActive());
            if (future.isSuccess() && mChannel.isActive()) {
                mConnectCount = 0;
                Log.i(TAG, "operationComplete: okkkkkkkk");
                if (!TextUtils.isEmpty(PersistConfig.findConfig().getNewIp())) {
                    //新ip连接成功
                    Log.i(TAG, "operationComplete: 新ip连接成功");
                    PersistConfig.saveIp(PersistConfig.findConfig().getNewIp());
                    PersistConfig.savePort(PersistConfig.findConfig().getNewPort());
                    PersistConfig.saveNewIp("", -1);
                    if (!TextUtils.isEmpty(mChangeCaTradeNumResp)) {
                        //这里是修改ca和ip
                        //更换结果 	STRING[1]	0失败，1成功
//                        NettyHelper.getInstance().send(new UChangeClientCa("1",mChangeCaTradeNumResp));
                    } else {
                        //这里是单纯的修改ip
//                        NettyHelper.getInstance().send(new UChangeIP("1@",mChangeIpTradeNumResp));
                    }
                } else if (!TextUtils.isEmpty(mChangeCaTradeNumResp)) {
                    //修改ca和ip 失败
//                    NettyHelper.getInstance().send(new UChangeClientCa("0",mChangeCaTradeNumResp));
                    Log.i(TAG, "operationComplete: 修改ca和ip失败..........");
                } else if (!TextUtils.isEmpty(mChangeIpTradeNumResp)) {
                    //修改ip连接失败
//                    NettyHelper.getInstance().send(new UChangeIP("0@连接不上",mChangeIpTradeNumResp));
                    Log.i(TAG, "operationComplete: 修改ip连接失败..........");
                }
                mChangeCaTradeNumResp = "";
                mChangeIpTradeNumResp = "";
                Log.i(TAG, "155 \n " + mChangeCaTradeNumResp + "\n" + mChangeIpTradeNumResp);
            } else {
                Log.i(TAG, "operationComplete:  errrrrrrrrr");
                future.channel().eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, "run: future.channel().eventLoop().schedule00");
                        if (isRunning) {
                            connect();
                        }
                    }
                }, 3, TimeUnit.SECONDS);
            }
        }
    }


    /**
     * 初始化的时候必须注册
     */
    public void register() {
//        setSSLContext();
        mBootstrap = new Bootstrap();
        mGroup = new NioEventLoopGroup();
        mBootstrap.group(mGroup);
        mBootstrap.channel(NioSocketChannel.class);
        mBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        mBootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                ByteBuf delimiter = Unpooled.copiedBuffer(new byte[]{0x0d, 0x0a});
                //20s未发送数据，回调userEventTriggered
                pipeline.addLast(sIdleStateHandler, new IdleStateHandler(0, 20, 0, TimeUnit.SECONDS));
//                pipeline.addLast(mSslContext.newHandler(ch.alloc()));
                //缓冲区2M大小
                pipeline.addLast(new DelimiterBasedFrameDecoder(2097152, false, delimiter));

                pipeline.addLast(new ByteToMessageDecoder() {

                    @Override
                    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
                        byte[] bytes = new byte[in.readableBytes()];
                        // 复制内容到字节数组bytes
                        in.readBytes(bytes);
                        DDLog.i("decode：" + DDLog.printArrayHex(bytes));
                        TcpPacket tcpPacket = TcpPacketFactory.from(bytes);
                        DDLog.i("解密完成：" + tcpPacket);
                        out.add(tcpPacket);
                    }
                });
                pipeline.addLast(new MessageToByteEncoder<TcpPacket>() {
                    @Override
                    protected void encode(ChannelHandlerContext ctx, TcpPacket msg, ByteBuf out) throws Exception {
                        DDLog.i("encode " + msg);
                        byte[] tcpBytes = msg.getTcpBytes();
                        out.writeBytes(tcpBytes);
                        DDLog.i("发送成功：" + DDLog.printArrayHex(tcpBytes));
                    }
                });
                //这里要注意，ChannelInboundHandler要在配置解码器后再配置。否则还不会报错，坑
                pipeline.addLast(new NettyHandler());
            }
        });
    }


    /**
     * 修改ca证书失败 要清空
     */
    public void clearCaFile() {
        File fileClientCrt = new File(FileHelper.getBasePath() + FileHelper.S_CLIENT_CRT_NAME);
        File fileClientKey = new File(FileHelper.getBasePath() + FileHelper.S_CLIENT_KEY_NAME);
        File fileRootCrt = new File(FileHelper.getBasePath() + FileHelper.S_ROOT_CRT_NAME);
        if (fileClientCrt.exists()) {
            fileClientCrt.delete();
        }
        if (fileClientKey.exists()) {
            fileClientKey.delete();
        }
        if (fileRootCrt.exists()) {
            fileRootCrt.delete();
        }
        Log.i(TAG, "clearCaFile: 清空ca证书 \n");
    }

    /**
     * 设置SSLContext
     */
    public void setSSLContext() {
        File fileClientCrt = new File(FileHelper.getBasePath() + FileHelper.S_CLIENT_CRT_NAME);
        File fileClientKey = new File(FileHelper.getBasePath() + FileHelper.S_CLIENT_KEY_NAME);
        File fileRootCrt = new File(FileHelper.getBasePath() + FileHelper.S_ROOT_CRT_NAME);
        if (fileClientKey.exists() && fileClientCrt.exists() && fileRootCrt.exists()) {
            try {
                mSslContext = SslContextBuilder.forClient()
                        .keyManager(fileClientCrt, fileClientKey)
                        .trustManager(DEFAULT_TrustManager).build();
                Log.i(TAG, "setSSLContext: file加载成功");
            } catch (Exception e) {
                e.printStackTrace();
                clearCaFile();
                Log.i(TAG, "setSSLContext: file加载失败 删除file 并转为assets加载");
//                setSSLContext();
                return;
            }
            Log.i(TAG, "setSSLContext: 通过file加载");
        } else {
            try {
                mSslContext = SslContextBuilder.forClient()
                        .keyManager(BaseApplication.sContext.getAssets().open("client.crt")
                                , BaseApplication.sContext.getAssets().open("pkcs8_client.key"))
                        // 这里由于android的限制7.0以上无法信任用户添加的证书
                        // 所以信任所有证书
                        // 也许会有隐患，有时间可以优化一下
                        // https://blog.csdn.net/shadowyspirits/article/details/79756274
                        // https://developer.android.google.cn/training/articles/security-config.html
                        .trustManager(DEFAULT_TrustManager).build();
                Log.i(TAG, "setSSLContext: assets加载成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "setSSLContext: 通过assets加载");
        }
    }


    /**
     * 设置心跳频率
     *
     * @param heartHz
     */
    public void modifyIdleStateHandler(int heartHz) {
        mChannel.pipeline().replace(IdleStateHandler.class, sIdleStateHandler
                , new IdleStateHandler(0, heartHz, 0, TimeUnit.SECONDS));
    }

    /**
     * 修改ca证书
     */
    public void modifySslHandler(String tradeNum) {
        modifySslHandler(tradeNum, true);
    }

    public void modifySslHandler(String tradeNum, boolean changeCa) {
//        setSSLContext();
        if (mSslContext != null) {
            Log.i(TAG, "modifySslHandler: 4");
            if (mChannel != null && mChannel.pipeline().get(sSslHandler) != mSslContext.newHandler(mChannel.alloc())) {
                Log.i(TAG, "modifySslHandler: 5");
                if (changeCa) {
                    Log.i(TAG, "modifySslHandler: changeCa为 true， 开始替换ca证书");
                    mChannel.pipeline().replace(SslHandler.class, sSslHandler
                            , mSslContext.newHandler(mChannel.alloc()));
                } else Log.i(TAG, "modifySslHandler: changeCa为 false, 不进行替换ca证书");
            }
            Log.i(TAG, "modifySslHandler: 6");
        }
        if (!TextUtils.isEmpty(tradeNum)) {
            mChangeCaTradeNumResp = tradeNum;
            disconnectByChangeIp(tradeNum);
        }
    }

    /**
     * fota升级
     * 总包数@包序号@接收状态@失败原因
     */
    public void modifyFota() {
        mFotaHelper.checkVersion();
    }


    public void unRegister() {
        isRunning = false;
        if (mChannel != null) {
            mChannel.close();
            mChannelFuture = null;
            mChannel = null;
        }
        if (mGroup != null) {
            mGroup.shutdownGracefully();
            mGroup = null;
            mBootstrap = null;
        }
    }

    /**
     * 是否链接
     *
     * @return
     */
    public boolean isConnect() {
        return mChannel != null && mChannel.isOpen() && mChannel.isActive();
    }

    public void disconnectByChangeIp(String changeIpTradeNumResp) {
        if (isConnect()) {
            mChannel.disconnect();
            mChannelFuture.cancel(true);
            mChannelFuture.removeListener(mChannelFutureListener);
        }
        if (!TextUtils.isEmpty(changeIpTradeNumResp)) {
            mChangeIpTradeNumResp = changeIpTradeNumResp;
        }
    }

    /**
     * 根据实体类 发送报文
     */
    public void send(TcpPacket tcpPacket) {
        if (tcpPacket != null) {
            long address = tcpPacket.getAddress();
            String cmd;
            if (address != 0) {
                cmd = tcpPacket.getCmd().getValue();
            } else {
                cmd = "";
            }
            String data = tcpPacket.getData();
            if (isConnect()) {
                mChannel.writeAndFlush(tcpPacket);
                List<PersistPacket> list =
                        LitePal.select("cmd", "address", "data").find(PersistPacket.class);
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getCmd().equals(cmd) && list.get(i).getAddress() == address &&
                                list.get(i).getData().equals(data) && !cmd.equals("")) {
                            // TODO: 2021/2/24 发送完成后删除本条记录
                            LitePal.deleteAll(PersistPacket.class, "cmd  = ? and address = ? and data = ?",
                                    cmd, String.valueOf(address), data);
                        }
                    }
                }
            } else {
                Log.i(TAG, "send: 发送消息失败 请检查长连接是否已经断开");
                if (address != 0) {
                    if (address == 3) {
                        return;
                    }
                    PersistPacket.savePacket(cmd, address, data);
                }
            }
        } else {
            DDLog.e("send: 发送消息失败 消息为空！");
        }
    }

    /**
     * 获取默认的TrustManager
     * 信任所有证书
     *
     * @return
     */
    private TrustManager getDefaultTrustManager() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};
        return trustAllCerts[0];
    }
}
