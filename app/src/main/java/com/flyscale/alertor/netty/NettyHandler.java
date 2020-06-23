package com.flyscale.alertor.netty;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.factory.BaseDataFactory;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistWhite;
import com.flyscale.alertor.data.up.UAddDeleteWhiteList;
import com.flyscale.alertor.data.up.UChangeAlarmNumber;
import com.flyscale.alertor.data.up.UHeart;
import com.flyscale.alertor.helper.DataConvertHelper;
import com.flyscale.alertor.helper.DateHelper;
import com.flyscale.alertor.helper.FileHelper;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.ThreadPool;
import com.flyscale.alertor.led.LedInstance;
import com.flyscale.alertor.media.AlarmMediaInstance;
import com.flyscale.alertor.media.ReceiveMediaInstance;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 9:51
 * @DESCRIPTION 暂无
 */
public class NettyHandler extends SimpleChannelInboundHandler<String> {

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
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Log.i(TAG, "channelInactive:  --- 重连 ---  ");
        LedInstance.getInstance().offStateLed();
        NettyHelper.getInstance().connect();
    }

    /**
     * channel被创建但没有注册到eventLoop
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
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Log.i(TAG, "channelActive:  ---  链接成功 ---");
        //保存第一次登陆的时间 永久不变
        PersistConfig.saveFirstLoginTime(System.currentTimeMillis());
        MediaHelper.play(MediaHelper.CONNECT_SUCCESS,true);
        LedInstance.getInstance().showStateLed();
    }

    /**
     *    //利用写空闲发送心跳检测消息
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if(evt instanceof IdleStateEvent){
            if(((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE){
                NettyHelper.getInstance().send(new UHeart());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Log.i(TAG, "exceptionCaught: " + cause.getMessage());
//        cause.printStackTrace();
//        ctx.close();
    }

    /**
     * channel已经注册到eventLoop
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        Log.i(TAG, "channelRegistered: ");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        BaseData baseData = BaseDataFactory.getDataInstance(msg).formatToObject(msg);
        int type = BaseDataFactory.parseType(msg);

        Log.i(TAG, "channelRead0: 开始 ---------------------");
        Log.i(TAG, "下行报文：" + msg);
        Log.i(TAG, "类型type : " + type );
        Log.i(TAG, "channelRead0: 结束 =====================");

        if(type == BaseData.TYPE_HEART_D){

        }else if(type == BaseData.TYPE_ALARM_D){
            //报警结果	STRING[1]	0表示正常接收，1没有正常接收，重发
            String result = baseData.getAlarmResult();
            if(TextUtils.equals(result,"0")){
                //报警成功
                AlarmHelper.getInstance().setAlarmResult(true);
            }
        }else if(type == BaseData.TYPE_RING_D){
            //响铃
            AlarmHelper.getInstance().alarmStart(true);
        }else if(type == BaseData.TYPE_VOICE_D){
            //下载报警语音包
            final String hex = baseData.getMessageBodyResp();
            ThreadPool.getSyncInstance().execute(new Runnable() {
                @Override
                public void run() {
                    FileHelper.byteToFile(DataConvertHelper.hexToBytes(hex),FileHelper.S_ALARM_RESP_NAME);
                    //播放报警信息时候 要把 报警音关闭 但是报警灯不关
                    AlarmMediaInstance.getInstance().stopLoopAlarm();
                    ReceiveMediaInstance.getInstance().play(FileHelper.S_ALARM_RESP_FILE,3);
                }
            });
        }else if(type == BaseData.TYPE_CHANGE_ALARM_NUMBER_D){
            //修改报警号码
            String number = baseData.getAlarmNum();
            PersistConfig.saveAlarmNum(number);
            NettyHelper.getInstance().send(new UChangeAlarmNumber("1@"));
        }else if(type == BaseData.TYPE_CHANGE_IP_D){
            //修改ip
            String newIp = baseData.getIpAddress();
            String newPort = baseData.getIpPort();
            PersistConfig.saveNewIp(newIp, Integer.parseInt(newPort));
            NettyHelper.getInstance().connect();
        }else if(type == BaseData.TYPE_ADD_OR_DELETE_WHITE_LIST_D){
            //白名单 0添加1删除
            String flag = baseData.getAddOrDeleteFlag();
            String whiteList = baseData.getWhiteList();
            if(TextUtils.equals("0",flag))
                PersistWhite.saveList(whiteList);
            else if(TextUtils.equals("1",flag))
                PersistWhite.deleteList(whiteList);
            NettyHelper.getInstance().send(new UAddDeleteWhiteList("1@"));
        }
    }
}
