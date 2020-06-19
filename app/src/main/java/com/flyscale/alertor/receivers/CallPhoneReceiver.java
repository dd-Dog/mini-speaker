package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.helper.PersistDataHelper;
import com.flyscale.alertor.netty.CallAlarmHelper;

/**
 * @author 高鹤泉
 * @TIME 2020/6/19 10:01
 * @DESCRIPTION 暂无
 */
public class CallPhoneReceiver extends BroadcastReceiver {
    static String mSendNum = "",mReceiveNum = "";
    static final int CALL_SEND = 1;
    static final int CALL_RECEIVE = 2;
    static final int CALL_IDLE = 3;//闲置
    int mCallState = CALL_IDLE;
    static boolean isCallActive = false;//通话是否成功
    static boolean isRinging = false;
    String TAG = "CallPhoneReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals("android.intent.action.NEW_OUTGOING_CALL")){
            //拨打电话
            mSendNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            mReceiveNum = "";
            Log.i(TAG, "onReceive: mSendNum = " + mSendNum);
        }else if(action.equals("com.android.phone.FLYSCALE_PHONE_STATE")){
            //电话状态
            int state = intent.getIntExtra("phone_state", 0);
            if(state == 3){
                //呼入
                mCallState = CALL_RECEIVE;
                isCallActive = false;
            }else if(state == 5){
                //呼出
                mCallState = CALL_SEND;
                isCallActive = false;
            }else if(state == 9){
                //断开
                mCallState = CALL_IDLE;
            }
            //通话成功
            if(state == 2){
                isCallActive = true;
                destroyCallAlarm();
            }
            Log.i(TAG, "onReceive: mCallState = " + mCallState + " -- phone_state = " + state);
        }else {
            //接听电话
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            switch (telephonyManager.getCallState()){
                case TelephonyManager.CALL_STATE_RINGING:
                    mReceiveNum = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    mSendNum = "";
                    isRinging = true;
                    Log.i(TAG, "onReceive: mReceiveNum = " +mReceiveNum);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                case TelephonyManager.CALL_STATE_IDLE:
                    isRinging = false;
                    break;
            }
        }
    }


    /**
     * 是否正在响铃
     * @return
     */
    public static boolean isRinging(){
        return isRinging;
    }

    /**
     * 获取来电电话号码
     * @return
     */
    public static String getReceiveNum(){
        return mReceiveNum;
    }


    /**
     * 正在通话中
     * @return
     */
    public static boolean isIsCallActive() {
        return isCallActive;
    }



    /**
     * 销毁电话报警
     */
    public void destroyCallAlarm(){
        if(mCallState == CALL_SEND){
            if(TextUtils.equals(mSendNum,PersistDataHelper.getAlarmNumber())){
                CallAlarmHelper.getInstance().destroy(true,true,false);
            }else if(TextUtils.equals(mSendNum,PersistDataHelper.getSpecialNumber())){
                CallAlarmHelper.getInstance().destroy(false,true,false);
            }
        }
    }

    public void register(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.phone.FLYSCALE_PHONE_STATE");
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        filter.addAction("android.intent.action.PHONE_STATE");
        BaseApplication.sContext.registerReceiver(this,filter);
    }

    public void unRegister(){
        BaseApplication.sContext.unregisterReceiver(this);
    }
}
