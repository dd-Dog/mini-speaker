package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistWhite;
import com.flyscale.alertor.helper.PhoneUtil;
import com.flyscale.alertor.netty.AlarmHelper;
import com.flyscale.alertor.netty.CallAlarmHelper;

/**
 * @author 高鹤泉
 * @TIME 2020/6/19 10:01
 * @DESCRIPTION 暂无
 */
public class CallPhoneReceiver extends BroadcastReceiver {
    static String mSendNum = "",mReceiveNum = "";
    static int mCallState = 1;
    String TAG = "CallPhoneReceiver";
    boolean isActivated = false;

    public static final int INVALID = 0;
    public static final int IDLE = 1;           /* The call is idle.  Nothing active */
    public static final int ACTIVE = 2;         /* There is an active call 通话成功 */
    public static final int INCOMING = 3;       /* A normal incoming phone call 有电话呼入 */
    public static final int CALL_WAITING = 4;   /* Incoming call while another is active */
    public static final int DIALING = 5;        /* An outgoing call during dial phase   呼出电话 */
    public static final int REDIALING = 6;      /* Subsequent dialing attempt after a failure */
    public static final int ONHOLD = 7;         /* An active phone call placed on hold */
    public static final int DISCONNECTING = 8;  /* A call is being ended.  呼叫正在断开 */
    public static final int DISCONNECTED = 9;   /* State after a call disconnects  呼叫断开 */
    public static final int CONFERENCED = 10;   /* Call part of a conference call */

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "onReceive: action = " + action);
        if(action.equals("android.intent.action.NEW_OUTGOING_CALL")){
            //拨打电话
            mSendNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            mReceiveNum = "";
        }else if(action.equals(BRConstant.ACTION_PHONE_INCOME)){
            mReceiveNum = intent.getStringExtra("number");
            mSendNum = "";
        }
        if(action.equals("com.android.phone.FLYSCALE_PHONE_STATE")){
            //电话状态
            int state = intent.getIntExtra("phone_state", 0);
            if(state == INCOMING || state == CALL_WAITING){
                //呼入
                mCallState = state;
                ifEndCall();
                Log.i(TAG, "onReceive: 呼入");
            }else if(state == DIALING){
                //呼出
                mCallState = state;
                Log.i(TAG, "onReceive: 呼出");
            }else if(state == DISCONNECTED){
                //断开
                if(mCallState == DIALING){
                    if(isActivated){
                        //呼出的电话  已经通过话  现在断开了
                        CallAlarmHelper.getInstance().setAlarming(false);
                    }
                }
                mCallState = state;
                isActivated = false;
                mSendNum = "";
                mReceiveNum = "";
                Log.i(TAG, "onReceive: 断开");
            }
            //通话成功
            if(state == ACTIVE){
                destroyCallAlarm();
            }
            Log.i(TAG, "onReceive: 呼入呼出断开mCallState = " + mCallState
                    + " ----- 实际状态phone_state = " + state + " ----- sendNumber = " + mSendNum + " ----- receiveNumber = " + mReceiveNum);
        }
    }


    /**
     * 获取来电电话号码
     * @return
     */
    public static String getReceiveNum(){
        return mReceiveNum;
    }

    /**
     * 呼入呼出断开的状态
     * @return
     */
    public static int getCallState() {
        return mCallState;
    }

    /**
     * 不接受普通号码呼入  并且白名单不包含这个号码 直接挂断
     */
    public void ifEndCall(){
        if(!PersistConfig.findConfig().isAcceptOtherNum() && !PersistWhite.isContains(mReceiveNum)){
            PhoneUtil.endCall(BaseApplication.sContext);
        }
    }

    /**
     * 销毁电话报警
     */
    public void destroyCallAlarm(){
        if(mCallState == DIALING){
            //呼出电话报警成功
            isActivated = true;
            CallAlarmHelper.getInstance().setAlarmResult(true);
            Log.i(TAG, "destroyCallAlarm: mSendNum = " + mSendNum + " ---- getSpecialNum() = "
                    + PersistConfig.findConfig().getSpecialNum() + " ----getAlarmNum = " + PersistConfig.findConfig().getAlarmNum());
            if(TextUtils.equals(mSendNum, PersistConfig.findConfig().getAlarmNum()) || TextUtils.equals(mSendNum,PersistConfig.findConfig().getSpecialNum())){
                CallAlarmHelper.getInstance().destroy(true,false,false,true);
            }
        }
    }

    public void register(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.phone.FLYSCALE_PHONE_STATE");
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction(BRConstant.ACTION_PHONE_INCOME);
        BaseApplication.sContext.registerReceiver(this,filter);
    }

    public void unRegister(){
        BaseApplication.sContext.unregisterReceiver(this);
    }
}
