package com.flyscale.alertor.alarmManager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.up.UAlarm;
import com.flyscale.alertor.data.up.UDoorAlarm;
import com.flyscale.alertor.data.up.UGasAlarm;
import com.flyscale.alertor.data.up.USmokeAlarm;
import com.flyscale.alertor.helper.TimerTaskHelper;
import com.flyscale.alertor.helper.UserActionHelper;
import com.flyscale.alertor.led.LedInstance;
import com.flyscale.alertor.netty.NettyHelper;

import java.util.TimerTask;

/**
 * @author 高鹤泉
 * @TIME 2020/7/8 13:11
 * @DESCRIPTION 暂无
 */
public class IpAlarmInstance {
    private static final IpAlarmInstance ourInstance = new IpAlarmInstance();
    public static final int STATUS_NONE = 0;//初始状态
    public static final int STATUS_ALARMING = 1;//正在报警
    public static final int STATUS_ALARM_SUCCESS = 2;//报警成功
    public static final int STATUS_ALARM_FINISH = 4;//报警结束

    String TAG = "IpAlarmInstance";
    int mStatus = STATUS_NONE;
    int mSendCount =1;
    TimerTaskHelper mTimerTaskHelper;
    final int DEFAULT_PERIOD = 1000;

    public static IpAlarmInstance getInstance() {
        return ourInstance;
    }

    private IpAlarmInstance() {

    }

    public void setStatus(int status) {
        mStatus = status;
        if(mStatus == STATUS_ALARM_FINISH){
            AlarmMediaPlayer.getInstance().stopAlarmSuccess();
            AlarmManager.finishAlarmBlink();
            if(!mTimerTaskHelper.isStop()){
                mTimerTaskHelper.stop();
            }
            //静默报警 挂断后 闪灯
            if(UserActionHelper.isMute()){
                LedInstance.getInstance().blinkChargeLed();
            }
        }else if(mStatus == STATUS_ALARM_SUCCESS){
            if(!mTimerTaskHelper.isStop()){
                mTimerTaskHelper.stop();
            }
            if(UserActionHelper.isMute()){
                //静默报警成功后 挂断
                setStatus(STATUS_ALARM_FINISH);
            }else {
                AlarmMediaPlayer.getInstance().stopLoopAlarm();
                AlarmMediaPlayer.getInstance().playAlarmSuccess(new AlarmMediaPlayer.OnPlayFinishListener() {
                    @Override
                    public void onPlayFinish() {
                        AlarmMediaPlayer.getInstance().playLoopAlarm(false);
                    }
                });
            }
        }
    }

    public int getStatus() {
        return mStatus;
    }

    public void polling(final int type){
        //网络连接正常 开始声光报警
        AlarmManager.startAlarmBlink(false);
        mStatus = STATUS_ALARMING;
        mSendCount = 1;
        mTimerTaskHelper = new TimerTaskHelper(new TimerTask() {
            @Override
            public void run() {
                if(mStatus != STATUS_ALARM_SUCCESS && mSendCount <= 3){
                    //ip报警未成功并且报警次数小于3次 则继续ip报警
                    if(type == BaseData.TYPE_ALARM_U){
                        NettyHelper.getInstance().send(new UAlarm(mSendCount));
                    }else if(type == BaseData.TYPE_DOOR_ALARM_U){
                        NettyHelper.getInstance().send(new UDoorAlarm(mSendCount));
                    }else if(type == BaseData.TYPE_SMOKE_ALARM_U){
                        NettyHelper.getInstance().send(new USmokeAlarm(mSendCount));
                    }else if(type == BaseData.TYPE_GAS_ALARM_U){
                        NettyHelper.getInstance().send(new UGasAlarm(mSendCount));
                    }
                    mSendCount++;
                }else {
                    mTimerTaskHelper.stop();
                    if(mSendCount > 3){
                        //如果报警次数大于3次 则放弃ip报警 ip报警失败
                        //转语音报警
                        Log.i(TAG, "run: mSendCount = " + mSendCount);
                        setStatus(STATUS_ALARM_FINISH);
                        CallAlarmInstance.getInstance().polling(false);
                    }else {
                        //报警成功 小于3次
                        //停止报警声 播放报警已发出
                        //然后再响报警声
                        setStatus(STATUS_ALARM_SUCCESS);
                    }
                }
            }
        },DEFAULT_PERIOD);
        mTimerTaskHelper.start(0);
    }
}
