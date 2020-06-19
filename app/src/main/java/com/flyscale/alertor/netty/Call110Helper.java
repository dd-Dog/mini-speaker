package com.flyscale.alertor.netty;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.helper.PersistDataHelper;
import com.flyscale.alertor.helper.PhoneUtil;
import com.flyscale.alertor.receivers.CallPhoneReceiver;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 高鹤泉
 * @TIME 2020/6/19 14:41
 * @DESCRIPTION 暂无
 */
public class Call110Helper {

    private static final Call110Helper instance = new Call110Helper();

    Timer mTimer;
    AtomicBoolean mAlarmResult = new AtomicBoolean(false);
    //正在报警
    public boolean isAlarming = false;
    //是否摘机
    boolean isOffhook = false;

    private Call110Helper() {
    }

    public static Call110Helper getInstance(){
        return instance;
    }

    /**
     * 设置报警结果
     * @param result
     */
    public void setAlarmResult(boolean result){
        mAlarmResult.set(result);
    }

    public void polling(){
        AlarmHelper.getInstance().alarmStart();
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
        }
        mTimer = new Timer();
        mAlarmResult = new AtomicBoolean(false);
        isAlarming = true;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!mAlarmResult.get()){
                    isOffhook = PhoneUtil.isOffhook(BaseApplication.sContext);
                    if(isOffhook){
                        //接听
                        PhoneUtil.endCall(BaseApplication.sContext);
                        while (isOffhook){
                            isOffhook = PhoneUtil.isOffhook(BaseApplication.sContext);
                        }
                    }
                    PhoneUtil.call(BaseApplication.sContext, PersistDataHelper.getSpecialNumber());
                }else {
                    destroy();
                }
            }
        },50,20 *1000);
    }

    public void destroy(){
        AlarmHelper.getInstance().alarmFinish();
        isAlarming = false;
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        mAlarmResult = new AtomicBoolean(false);
    }
}
