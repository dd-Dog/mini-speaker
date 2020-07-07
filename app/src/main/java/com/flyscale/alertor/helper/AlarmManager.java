package com.flyscale.alertor.helper;

import android.content.Context;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.led.LedInstance;
import com.flyscale.alertor.media.AlarmMediaInstance;
import com.flyscale.alertor.netty.NettyHelper;

import java.util.TimerTask;

/**
 * @author 高鹤泉
 * @TIME 2020/7/5 16:56
 * @DESCRIPTION 暂无
 */
public class AlarmManager {
    private static final AlarmManager ourInstance = new AlarmManager();
    String TAG = "AlarmManager";

    //报警结果
    boolean alarmResult = false;
    //接警结果
    boolean receiveResult = false;
    int mSendCount = 1;
    //是否正在报警
    boolean isAlarming = false;
    //拨打的电话
    String mSendNumber;
    final int DEFAULT_CALL_TIME = 25 * 1000;
    final int DEFAULT_IP_TIME = 1000;
    //是否执行timer
    boolean isRunTimerFlag = true;
    //ip报警timer
    TimerTaskHelper mIpTimer;
    //语音报警timer
    TimerTaskHelper mCallTimer;

    Context mContext = BaseApplication.sContext;



    public static AlarmManager getInstance() {
        return ourInstance;
    }

    private AlarmManager() {
    }


    /**
     * 设置报警结果
     * @param alarmResult
     */
    public void setAlarmResult(boolean alarmResult) {
        this.alarmResult = alarmResult;
    }

    /**
     * 设置接警结果
     * @param receiveResult
     */
    public void setReceiveResult(boolean receiveResult) {
        this.receiveResult = receiveResult;
    }

    /**
     * 设置是否正在报警
     * @param alarming
     */
    public void setAlarming(boolean alarming) {
        isAlarming = alarming;
    }

    /**
     * 设置timer是否可执行逻辑
     * @param runTimerFlag
     */
    public void setRunTimerFlag(boolean runTimerFlag) {
        isRunTimerFlag = runTimerFlag;
    }


    /**
     * 轮询 每一秒发送一次报警信息
     * @param type
     */
    public void polling(int type){
        //报警时，如果网络没有连通，要提示“网络连接失败”。
        if(!NetHelper.isNetworkConnected(mContext)){
            MediaHelper.play(MediaHelper.NET_CONNECT_FAIL,true);
            return;
        }
        //报警时，如果没有连接到服务器，要提示“连接服务器失败”。
        if(!NettyHelper.getInstance().isConnect()){
            MediaHelper.play(MediaHelper.SERVER_CONNECT_FAIL,true);
            return;
        }
        Log.i(TAG, "polling: 准备报警");
        alarmStart(false);
        if(PersistConfig.findConfig().isIpAlarmFirst()){
            Log.i(TAG, "polling: ip报警 type = " + type);

        }else {
            Log.i(TAG, "polling: 语音报警");
        }

    }


    public class IPTimerTask extends TimerTask{
        @Override
        public void run() {
            if(alarmResult){

            }
        }
    }

    /**
     * 开始响警报闪警报
     * @param isReceive
     */
    public void alarmStart(boolean isReceive){
        boolean isMute = PersistConfig.findConfig().isMute();
        Log.i(TAG, "alarmStart: isMute 是否静默 = " + isMute);
        if(isReceive){
            //接警方 必须响警报
            isMute = false;
        }
        if(isMute){
            return;
        }
        if(!AlarmMediaInstance.getInstance().isPlaying()){
            AlarmMediaInstance.getInstance().playLoopAlarm();
            LedInstance.getInstance().blinkAlarmLed();
        }
    }


}
