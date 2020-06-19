package com.flyscale.alertor.netty;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.factory.BaseDataFactory;
import com.flyscale.alertor.helper.DataConvertHelper;
import com.flyscale.alertor.helper.DateHelper;
import com.flyscale.alertor.helper.FileHelper;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.ThreadPool;
import com.flyscale.alertor.led.LedInstance;
import com.flyscale.alertor.media.AlarmMediaInstance;
import com.flyscale.alertor.media.ReceiveMediaInstance;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 9:51
 * @DESCRIPTION 暂无
 */
public class NettyHandler extends SimpleChannelInboundHandler<String> {

    String TAG = NettyHelper.TAG;

    public NettyHandler() {

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
            AlarmHelper.getInstance().alarmStart();
        }else if(type == BaseData.TYPE_VOICE_D){
            //下载报警语音包
            final String hex = baseData.getMessageBodyResp();
            Log.i(TAG, "channelRead0: 下载语音 时间 = " + DateHelper.longToString(DateHelper.yyyyMMddHHmmss));
            ThreadPool.getSyncInstance().execute(new Runnable() {
                @Override
                public void run() {
                    FileHelper.byteToFile(DataConvertHelper.hexToBytes(hex),FileHelper.S_ALARM_RESP_NAME);
                    Log.i(TAG, "run: 下载结束语音 时间 = " + DateHelper.longToString(DateHelper.yyyyMMddHHmmss));
                    //播放报警信息时候 要把 报警音关闭 但是报警灯不关
                    AlarmMediaInstance.getInstance().stopLoopAlarm();
                    ReceiveMediaInstance.getInstance().play(FileHelper.S_ALARM_RESP_FILE,3);
                }
            });
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Log.i(TAG, "channelInactive:  --- 重连 ---  " + ctx.name());
        NettyHelper.getInstance().setConnectStatus(NettyHelper.DISCONNECTION);
        NettyHelper.getInstance().connect();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        Log.i(TAG, "channelRegistered: ");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        Log.i(TAG, "channelUnregistered: ");
        LedInstance.getInstance().offStateLed();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Log.i(TAG, "channelActive:  ---  链接成功 ---");
        NettyHelper.getInstance().setConnectStatus(NettyHelper.CONNECTED);
        MediaHelper.play(MediaHelper.CONNECT_SUCCESS,true);
        LedInstance.getInstance().showStateLed();
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Log.i(TAG, "exceptionCaught: " + cause.getMessage());
    }


}
