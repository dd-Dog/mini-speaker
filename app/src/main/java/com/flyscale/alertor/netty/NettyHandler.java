package com.flyscale.alertor.netty;

import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.alarmManager.AlarmManager;
import com.flyscale.alertor.alarmManager.AlarmMediaPlayer;
import com.flyscale.alertor.alarmManager.IpAlarmInstance;
import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.factory.BaseDataFactory;
import com.flyscale.alertor.data.packet.TcpPacket;
import com.flyscale.alertor.data.packet.TcpPacketFactory;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistWhite;
import com.flyscale.alertor.data.up.UAddDeleteWhiteList;
import com.flyscale.alertor.data.up.UChangeAlarmNumber;
import com.flyscale.alertor.data.up.UChangeHeart;
import com.flyscale.alertor.data.up.UHeart;
import com.flyscale.alertor.data.up.URing;
import com.flyscale.alertor.data.up.UUpdateVersion;
import com.flyscale.alertor.data.up.UVoice;
import com.flyscale.alertor.eventBusManager.EventBusUtils;
import com.flyscale.alertor.eventBusManager.EventType;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.MD5Util;
import com.flyscale.alertor.helper.UserActionHelper;
import com.flyscale.alertor.helper.DataConvertHelper;
import com.flyscale.alertor.helper.FileHelper;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.ThreadPool;
import com.flyscale.alertor.jni.NativeHelper;
import com.flyscale.alertor.led.LedInstance;
import com.flyscale.alertor.media.AlarmMediaInstance;
import com.flyscale.alertor.media.ReceiveMediaInstance;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 9:51
 * @DESCRIPTION 暂无
 */
public class NettyHandler extends SimpleChannelInboundHandler<TcpPacket> {

    String TAG = "NettyHandler";

    public NettyHandler() {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        Log.i(TAG, "handlerAdded: ");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        Log.i(TAG, "handlerRemoved: ");
    }

    /**
     * channel没有连接到远程节点
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Log.i(TAG, "channelInactive:  --- 准备重连 ---  ");
        LedInstance.getInstance().offStateLed();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                NettyHelper.getInstance().connect();
            }
        }, 65 * 1000);
    }

    /**
     * channel被创建但没有注册到eventLoop
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        Log.i(TAG, "channelUnregistered: ");
    }


    /**
     * channel处于活动状态 连接到了远程节点 可以接收和发送
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Log.i(TAG, "channelActive:  ---  链接成功 ---");
        //保存第一次登陆的时间 永久不变
        PersistConfig.saveFirstLoginTime(System.currentTimeMillis());
        if (!UserActionHelper.isFastConnect(120 * 1000)) {
            MediaHelper.play(MediaHelper.SERVER_CONNECT_SUCCESS, true);
        }
        LedInstance.getInstance().showStateLed();

        //开始鉴权
        NettyHelper.getInstance().send(TcpPacketFactory.createPacketSend(TcpPacketFactory.LOGIN, "460031234567890/0A9464026708209/"));
    }

    /**
     * //利用写空闲发送心跳检测消息
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
//                NettyHelper.getInstance().send(new UHeart());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Log.i(TAG, "exceptionCaught: " + cause.getMessage());
        ctx.close();
    }

    /**
     * channel已经注册到eventLoop
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        Log.i(TAG, "channelRegistered: ");
    }


    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, TcpPacket tcpPacket) throws Exception {
        DDLog.i("channelRead0 tcpPacket=" + tcpPacket);
        if (tcpPacket != null) {
            long address = tcpPacket.getAddress();
            if (address == TcpPacketFactory.LOGIN_CONFIRM) {
                //登录鉴权
                String md5 = MD5Util.md5(tcpPacket.getData(), MD5Util.getKI());
                PersistConfig.saveRandomKey(tcpPacket.getData());
                PersistConfig.saveLogin(false);
                DDLog.i("鉴权MD5=" + md5);
                TcpPacket packetSend = TcpPacketFactory.createPacketSend(TcpPacketFactory.LOGIN_CONFIRM, md5);
                NettyHelper.getInstance().send(packetSend);
            } else if (address == TcpPacketFactory.LOGIN_RESULT) {
                //判断登录成功
                String data = tcpPacket.getData();
                DDLog.i("登录返回：" + data);
                if (!TextUtils.isEmpty(data)) {
                    if (data.split("/") != null) {
                        if (TextUtils.equals(TcpPacketFactory.LOGIN_CODE.SUCCESS.getCode() + "", data.split("/")[0])){
                            DDLog.i("登录成功");
                            PersistConfig.saveLogin(true);
                            //set default key
                            return;
                        }
                    }
                }
                DDLog.e("登录失败！");
                PersistConfig.saveLogin(false);
            }
        }
        /*BaseData baseData = BaseDataFactory.getDataInstance(msg).formatToObject(msg);
        int type = BaseDataFactory.parseType(msg);
        final String tradeNum = baseData.getTradeNum();
        Log.i(TAG, "下行报文：channelRead0: 开始 ---------------------");
        Log.i(TAG, "报文内容：" + msg);
        Log.i(TAG, "报文类型type : " + type );
        Log.i(TAG, "下行报文：channelRead0: 结束 =====================");

        if(type == BaseData.TYPE_HEART_D){

        }else if(type == BaseData.TYPE_ALARM_D || type == BaseData.TYPE_DOOR_ALARM_D
                || type == BaseData.TYPE_SMOKE_ALARM_D || type == BaseData.TYPE_GAS_ALARM_D){
            //报警结果	STRING[1]	0表示正常接收，1没有正常接收，重发
            String result = baseData.getAlarmResult();
            if(TextUtils.equals(result,"0")){
                //ip报警成功
                IpAlarmInstance.getInstance().setStatus(IpAlarmInstance.STATUS_ALARM_SUCCESS);
            }
        }else if(type == BaseData.TYPE_RING_D){
            //响铃
//            NettyHelper.getInstance().send(new URing(baseData.getSendCount(),tradeNum));
        }else if(type == BaseData.TYPE_VOICE_D){
            //下载报警语音包
            final String hex = baseData.getMessageBodyResp();
            //总包数@包序号@设置状态@终端录音存储状态
            //设置状态:0表示成功，1表示失败
            //总包数:语音报分包总数
            //包序号:包序号,比如是第几个包
            //终端录音存储状态：0表示空间满，1表示空间不满，可以继续接受录音
            String message = baseData.getTotalPacket() + "@" + baseData.getPacketNum() + "@0@1";
//            NettyHelper.getInstance().send(new UVoice(baseData.getSendCount(),message,tradeNum));
            ThreadPool.getSyncInstance().execute(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "run: 下载文件");
                    FileHelper.byteToFile(DataConvertHelper.hexToBytes(hex),FileHelper.S_ALARM_RESP_NAME);
                    AlarmMediaPlayer.getInstance().playReceive(FileHelper.S_ALARM_RESP_FILE,3);
//                    ReceiveMediaInstance.getInstance().play(FileHelper.S_ALARM_RESP_FILE,3);
                }
            });
        }else if(type == BaseData.TYPE_CHANGE_ALARM_NUMBER_D){
            //修改报警号码
            String number = baseData.getAlarmNum();
            PersistConfig.saveAlarmNum(number);
//            NettyHelper.getInstance().send(new UChangeAlarmNumber("1@",tradeNum));
        }else if(type == BaseData.TYPE_CHANGE_IP_D){
            //修改ip
            String newIp = baseData.getIpAddress();
            String newPort = baseData.getIpPort();
            PersistConfig.saveNewIp(newIp, Integer.parseInt(newPort));
            NettyHelper.getInstance().disconnectByChangeIp(tradeNum);
        }else if(type == BaseData.TYPE_ADD_OR_DELETE_WHITE_LIST_D){
            //白名单 0添加1删除
            String flag = baseData.getAddOrDeleteFlag();
            String whiteList = baseData.getWhiteList();
            if(TextUtils.equals("0",flag))
                PersistWhite.saveList(whiteList);
            else if(TextUtils.equals("1",flag))
                PersistWhite.deleteList(whiteList);
//            NettyHelper.getInstance().send(new UAddDeleteWhiteList("1@",tradeNum));
        }else if(type == BaseData.TYPE_CHANGE_HEART_D){
            //修改心跳频率
            int heartHZ = baseData.getHeartHZ();
            NettyHelper.getInstance().modifyIdleStateHandler(heartHZ);
//            NettyHelper.getInstance().send(new UChangeHeart("1@",tradeNum));
        }else if(type == BaseData.TYPE_UPDATE_VERSION_D){
            //终端版本升级
            //总包数@包序号@接收状态@失败原因
            String total = baseData.getTotalPacket();
            String num = baseData.getPacketNum();
            NettyHelper.getInstance().modifyFota(total,num,tradeNum);
            //总包数@包序号@接收状态@失败原因
            //总包数:语音报分包总数
            //包序号:包序号,比如是第几个包
            //接收状态:0接收成功，1接收失败
            //失败原因：成功不填写（长度为0），失败填写原因
            //收到下行报文 直接回复升级成功的上行报文
//            String message = total + "@" + num + "@0@";
//            NettyHelper.getInstance().send(new UUpdateVersion(message,tradeNum));
        }else if(type == BaseData.TYPE_CHANGE_CLIENT_CA_D){
            //终端更换证书
            Log.i(TAG,
                    "channelRead0: 更换证书以前的ip及端口号......." + PersistConfig.findConfig().getIp() + PersistConfig.findConfig().getPort());
            String clientCa = baseData.getClientCaMessage();
            String clientKey = baseData.getClientPwdMessage();
            String rootCa = baseData.getRootCaMessage();
            final String ip = baseData.getIpAddress();
            final String port = baseData.getIpPort();
            final byte[] clientCaB = DataConvertHelper.hexToBytes(clientCa);
            final byte[] clientKeyB = DataConvertHelper.hexToBytes(clientKey);
            final byte[] rootCaB = DataConvertHelper.hexToBytes(rootCa);
            ThreadPool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    FileHelper.byteToFile(clientCaB,FileHelper.S_CLIENT_CRT_NAME);
                    FileHelper.byteToFile(clientKeyB, FileHelper.S_CLIENT_KEY_NAME);
                    FileHelper.byteToFile(rootCaB,FileHelper.S_ROOT_CRT_NAME);
                    //修改ca的同时  要修改ip  逻辑要包含修改ip
                    Log.i(TAG, "run: 要更换的ip及端口号 \n" + ip + Integer.parseInt(port));
                    PersistConfig.saveNewIp(ip, Integer.parseInt(port));
                    NettyHelper.getInstance().modifySslHandler(tradeNum);

                }
            });
        }*/
    }
}
