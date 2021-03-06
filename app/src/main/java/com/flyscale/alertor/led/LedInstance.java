package com.flyscale.alertor.led;

import android.annotation.SuppressLint;
import android.flyscale.FlyscaleManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.helper.TimerTaskHelper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 高鹤泉
 * @TIME 2020/6/17 13:16
 * @DESCRIPTION 暂无
 */
public class LedInstance {

    private static final LedInstance ourInstance = new LedInstance();
    Timer mSignalTimer,mAlarmTimer;

    String TAG = "LedInstance";
    //是否允许闪烁信号灯
    boolean isBlinkSignalFlag = false;
    boolean isBlinkAlarmFlag = false;
    boolean isOnSignal = false;
    boolean isOnAlarm = true;
    boolean isAlarmOnStatus = true;
    FlyscaleManager mFlyscaleManager;
//    AlarmHandler mAlarmHandler = new AlarmHandler(Looper.myLooper());
    TimerTaskHelper mBlinkAlarmTimer;

    public static LedInstance getInstance() {
        return ourInstance;
    }

    @SuppressLint("WrongConstant")
    private LedInstance() {
        mFlyscaleManager = (FlyscaleManager) BaseApplication.sContext.getSystemService(FlyscaleManager.FLYSCALE_SERVICE);
    }
    

    /**
     * 电源指示灯（红灯）
     * 灯常亮：代表有市电
     * 灯灭：表示无市电
     */
    public void showChargeLed(){
        mFlyscaleManager.setLightColor(Constant.CHARGE_LED,Constant.LED_COLOR_LEVEL_MAX);
    }

    public void offChargeLed(){
        mFlyscaleManager.setLightColor(Constant.CHARGE_LED,Constant.LED_COLOR_LEVEL_MIN);
    }

    public String getChargeLed(){
        String result = mFlyscaleManager.getLightColor(Constant.CHARGE_LED);
//        Log.i(TAG, "getChargeLed: status --- " + result);
        return result;
    }

    int mBlinkChargeCount = 0;
    TimerTaskHelper mBlinkChargeTimerHelper;

    public void blinkChargeLed(){
        if(mBlinkChargeTimerHelper != null){
            mBlinkChargeTimerHelper.stop();
        }
        mBlinkChargeCount = 0;
        mBlinkChargeTimerHelper = new TimerTaskHelper(new TimerTask() {
            @Override
            public void run() {
                if(getChargeLed().equals("0")){
                    showChargeLed();
                }else {
                    offChargeLed();
                }
                mBlinkChargeCount ++;
                Log.i(TAG, "run: mBlinkChargeCount --- " + mBlinkChargeCount);
                if(mBlinkChargeCount >= 2){
                    mBlinkChargeTimerHelper.stop();
                    mBlinkChargeCount = 0;
                    if(BaseApplication.sFlyscaleManager.getAdapterState().equals("1")){
                        showChargeLed();
                    }else {
                        offChargeLed();
                    }
                }
            }
        },1000);
        mBlinkChargeTimerHelper.start(10);
    }

    /**
     * 信号指示灯（黄灯）
     * 常亮；表示信号强
     * 灯闪：表示信号弱（亮1S，灭1S）
     * 灯灭：表示无信号
     */
    private void showSignalLed(){
        mFlyscaleManager.setLightColor(Constant.SIGNAL_LED,Constant.LED_COLOR_LEVEL_MAX);
        isOnSignal = true;
    }

    private void offSignalLed(){
        mFlyscaleManager.setLightColor(Constant.SIGNAL_LED,Constant.LED_COLOR_LEVEL_MIN);
        isOnSignal = false;
    }

    /**
     * 取消闪烁 常亮
     */
    public void cancelBlinkShowSignalLed(){
        destroySignalTimer();
        showSignalLed();
    }

    /**
     * 取消闪烁 关闭
     */
    public void cancelBlinkOffSignalLed(){
        destroySignalTimer();
        offSignalLed();
    }

    /**
     * 信号灯闪烁
     */
    public void blinkSignalLed(){
        isBlinkSignalFlag = true;
        if(mSignalTimer != null){
            mSignalTimer.cancel();
            mSignalTimer.purge();
            mSignalTimer = null;
        }
        mSignalTimer = new Timer();
        mSignalTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isBlinkSignalFlag){
                    if(isOnSignal){
                        offSignalLed();
                    }else {
                        showSignalLed();
                    }
                }
            }
        },50,1000);
    }

    /**
     * 销毁信号灯定时器
     */
    public void destroySignalTimer(){
        isBlinkSignalFlag = false;
        if(mSignalTimer != null){
            mSignalTimer.cancel();
            mSignalTimer.purge();
            mSignalTimer = null;
        }
    }

    /**
     * 是否亮灯
     * @param type
     * @return
     */
//    public boolean isOnLed(int type){
//        String color = mFlyscaleManager.getLightColor(type);
//        Log.i(TAG, "isOnLed: " + color);
//    }



    /**
     * 状态指示灯（绿灯）
     * 常亮：表示连接到服务器
     * 灯灭：表示故障
     */
    public void showStateLed(){
        mFlyscaleManager.setLightColor(Constant.STATE_LED,Constant.LED_COLOR_LEVEL_MAX);
    }
    public void offStateLed(){
        mFlyscaleManager.setLightColor(Constant.STATE_LED,Constant.LED_COLOR_LEVEL_MIN);
    }


    public void blinkAlarmLed(){

        if(mBlinkAlarmTimer != null){
            mBlinkAlarmTimer.stop();
        }
        isBlinkAlarmFlag = true;
        mBlinkAlarmTimer = new TimerTaskHelper(new TimerTask() {
            @Override
            public void run() {
                if(isOnAlarm){
                    offAlarmLed();
                }else {
                    showAlarmLed();
                }
                Log.i(TAG, "run: blinkAlarmLed isOnAlarm  --- " + isOnAlarm);
            }
        },200);
        mBlinkAlarmTimer.start(50);
//        isBlinkAlarmFlag = true;
//        if(mAlarmTimer != null){
//            mAlarmTimer.cancel();
//            mAlarmTimer.purge();
//            mAlarmTimer = null;
//        }
//        mAlarmTimer = new Timer();
//        mAlarmTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                if(isBlinkAlarmFlag){
//                    if(isOnAlarm){
//                        offAlarmLed();
//                    }else {
//                        showAlarmLed();
//                    }
//                    Log.i(TAG, "run:blinkAlarmLed isOnAlarm  = " + isOnAlarm);
//                }
//            }
//        },50,300);
    }

    private void showAlarmLed(){
        mFlyscaleManager.setLightColor(Constant.ALARM_LED,Constant.LED_COLOR_LEVEL_MAX);
        isOnAlarm = true;
    }
    private void offAlarmLed(){
        mFlyscaleManager.setLightColor(Constant.ALARM_LED,Constant.LED_COLOR_LEVEL_MIN);
        isOnAlarm = false;
    }

    /**
     * 销毁报警灯闪烁
     */
    public void destroyAlarmTimer(){
        isBlinkAlarmFlag = false;
        if(mBlinkAlarmTimer != null){
            mBlinkAlarmTimer.stop();
        }
//        isBlinkAlarmFlag = false;
//        if(mAlarmTimer != null){
//            mAlarmTimer.cancel();
//            mAlarmTimer.purge();
//            mAlarmTimer = null;
//        }
    }

    public boolean isBlinkAlarmFlag() {
        return isBlinkAlarmFlag;
    }

    public void cancelBlinkOffAlarmLed(){
        isAlarmOnStatus = false;
        destroyAlarmTimer();
        Log.i(TAG, "cancelBlinkOffAlarmLed: " + mFlyscaleManager.getLightColor(Constant.ALARM_LED));
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                offAlarmLed();
                Log.i(TAG, "run: offAlarmLed");
            }
        },201);

//        mAlarmHandler.sendEmptyMessageDelayed(offWhat,305);
//        Log.i(TAG, "cancelBlinkOffAlarmLed: " + mFlyscaleManager.getLightColor(Constant.ALARM_LED));
    }
    public void cancelBlinkShowAlarmLed(){
        isAlarmOnStatus = true;
        destroyAlarmTimer();
        Log.i(TAG, "cancelBlinkShowAlarmLed: " + mFlyscaleManager.getLightColor(Constant.ALARM_LED));
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                showAlarmLed();
                Log.i(TAG, "run: showAlarmLed");
            }
        },201);

//        mAlarmHandler.sendEmptyMessageDelayed(showWhat,305);
//        Log.i(TAG, "cancelBlinkShowAlarmLed: " + mFlyscaleManager.getLightColor(Constant.ALARM_LED));
    }


    /**
     * 报警灯的开关状态
     * @return
     */
    public boolean isAlarmOnStatus() {
        return isAlarmOnStatus;
    }

//    int showWhat = 1;
//    int offWhat = 2;
//    @SuppressLint("HandlerLeak")
//    public class AlarmHandler extends Handler{
//
//        public AlarmHandler(Looper looper) {
//            super(looper);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            int what = msg.what;
//            if(what == showWhat){
//                showAlarmLed();
//                Log.i(TAG, "handleMessage: showAlarmLed()" );
//            }else if(what == offWhat){
//                offAlarmLed();
//                Log.i(TAG, "handleMessage: offAlarmLed()");
//            }
//        }
//    }
}
