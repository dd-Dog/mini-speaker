package com.flyscale.alertor.alarmManager;

import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.helper.PhoneUtil;
import com.flyscale.alertor.helper.TimerTaskHelper;
import com.flyscale.alertor.helper.UserActionHelper;
import com.flyscale.alertor.led.LedInstance;

import java.util.TimerTask;

/**
 * @author 高鹤泉
 * @TIME 2020/7/8 13:11
 * @DESCRIPTION 暂无
 */
public class CallAlarmInstance {
    private static final CallAlarmInstance ourInstance = new CallAlarmInstance();
    TimerTaskHelper mTimerTaskHelper;
    final int DEFAULT_PERIOD = 25 * 1000;
    public static final int STATUS_NONE = 10;//初始状态
    public static final int STATUS_ALARMING = 11;//正在报警
    public static final int STATUS_ALARM_SUCCESS = 12;//报警成功
    public static final int STATUS_ALARM_FINISH = 14;//报警结束
    int mStatus = STATUS_NONE;
    //是否摘机
    boolean isOffhook = false;

    public static CallAlarmInstance getInstance() {
        return ourInstance;
    }

    private CallAlarmInstance() {

    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
        if(mStatus == STATUS_ALARM_FINISH){
            PhoneUtil.endCall(BaseApplication.sContext);
            AlarmManager.finishAlarmBlink();
            if(!mTimerTaskHelper.isStop()){
                mTimerTaskHelper.stop();
            }
        }else if(mStatus == STATUS_ALARM_SUCCESS){
            mTimerTaskHelper.stop();
            if(UserActionHelper.isMute()){
                LedInstance.getInstance().blinkChargeLed();
            }
        }
    }

    public void polling(boolean is110){
        int ipStatus = IpAlarmInstance.getInstance().getStatus();
        if(ipStatus == IpAlarmInstance.STATUS_ALARMING || ipStatus == IpAlarmInstance.STATUS_ALARM_SUCCESS){
            IpAlarmInstance.getInstance().setStatus(IpAlarmInstance.STATUS_ALARM_FINISH);
        }
        AlarmManager.startAlarmBlink(false);
        final String sendNumber;
        if(is110){
            sendNumber = PersistConfig.findConfig().getSpecialNum();
        }else {
            sendNumber = PersistConfig.findConfig().getAlarmNum();
        }
        mStatus = STATUS_ALARMING;
        mTimerTaskHelper = new TimerTaskHelper(new TimerTask() {
            @Override
            public void run() {
                if(mStatus == STATUS_ALARM_SUCCESS){
                    mTimerTaskHelper.stop();
                }else {
                    isOffhook = PhoneUtil.isOffhook(BaseApplication.sContext);
                    if(isOffhook){
                        //摘机状态 主动挂断
                        PhoneUtil.endCall(BaseApplication.sContext);
                        while (isOffhook){
                            //死循环查询 直到挂断
                            isOffhook = PhoneUtil.isOffhook(BaseApplication.sContext);
                        }
                    }
                    PhoneUtil.call(BaseApplication.sContext,sendNumber);
                }
            }
        },DEFAULT_PERIOD);
        mTimerTaskHelper.start(50);
    }
}
