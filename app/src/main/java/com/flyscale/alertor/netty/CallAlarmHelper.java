package com.flyscale.alertor.netty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.helper.PersistDataHelper;
import com.flyscale.alertor.helper.PhoneUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 高鹤泉
 * @TIME 2020/6/16 16:30
 * @DESCRIPTION 电话语音报警
 */
public class CallAlarmHelper {
    private static final CallAlarmHelper ourInstance = new CallAlarmHelper();
    Timer mTimer;
    AtomicBoolean mAlarmResult = new AtomicBoolean(false);
    String TAG = "CallAlarmHelper";
    PhoneStateReceiver mPhoneStateReceiver = new PhoneStateReceiver();

    boolean isOffhook = true;

    public static CallAlarmHelper getInstance() {
        return ourInstance;
    }

    private CallAlarmHelper() {
    }

    /**
     * 设置报警结果
     * @param result
     */
    public void setAlarmResult(boolean result){
        mAlarmResult.set(result);
    }

    /**
     * 轮询 每隔20秒打一次电话 直到打通
     * 如果callNumber为空 则去本地存储默认号码
     */
    public void polling(String callNumber){
        register();
        if(TextUtils.isEmpty(callNumber)){
            callNumber =  PersistDataHelper.getAlarmNumber();
        }
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
        }
        mTimer = new Timer();
        mAlarmResult = new AtomicBoolean(false);
        final String finalCallNumber = callNumber;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!mAlarmResult.get()){
                    //这里打电话之前要先判断是否挂断电话
                    //如果挂断电话的状态直接打电话
                    //如果不是挂断的状态 ，则需要挂断电话。这个挂断是有过程的 大概一秒 然后才会变成挂断的状态
                    isOffhook = PhoneUtil.isOffhook(BaseApplication.sContext);
                    if(isOffhook){
                        PhoneUtil.call(BaseApplication.sContext, finalCallNumber);
                    }else {
                        PhoneUtil.endCall(BaseApplication.sContext);
                        while (!isOffhook){
                            isOffhook = PhoneUtil.isOffhook(BaseApplication.sContext);
                            Log.i(TAG, "run: 等待挂断电话" );
                        }
                        PhoneUtil.call(BaseApplication.sContext, finalCallNumber);
                    }
                }else {
                    destroy();
                }
            }
        },50,5 * 1000);
    }


    public void destroy(){
        unRegister();
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        mAlarmResult = new AtomicBoolean(false);
    }


    /**
     * 通话状态的广播
     */
    public class PhoneStateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("com.android.phone.FLYSCALE_PHONE_STATE")){
                int state = intent.getIntExtra("phone_state", 0);
                Log.i(TAG, "onReceive: " + state);
                if(state == 2){
                    setAlarmResult(true);
                }
            }
        }
    }

    public void register(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.phone.FLYSCALE_PHONE_STATE");
        BaseApplication.sContext.registerReceiver(mPhoneStateReceiver,filter);
    }
    public void unRegister(){
        BaseApplication.sContext.unregisterReceiver(mPhoneStateReceiver);
    }
}
