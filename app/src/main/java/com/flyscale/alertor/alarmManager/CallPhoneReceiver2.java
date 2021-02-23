package com.flyscale.alertor.alarmManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistWhite;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.PhoneUtil;
import com.flyscale.alertor.receivers.BRConstant;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 高鹤泉
 * @TIME 2020/7/10 17:53
 * @DESCRIPTION 暂无
 */
public class CallPhoneReceiver2 extends BroadcastReceiver {
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

    public static final int STATE_SEND = 1;
    public static final int STATE_RECEIVE =2;
    public static final int STATE_NONE = -1;
    static String mSendNum = "",mReceiveNum = "";
    public static int sPhoneState = STATE_NONE;
    String TAG = "CallPhoneReceiver2";

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "onReceive: action = " + action);
        if(TextUtils.equals(action,"android.intent.action.NEW_OUTGOING_CALL")){
            //拨打电话
            mSendNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            mReceiveNum = "";
            sPhoneState = STATE_SEND;
        }else if(action.equals(BRConstant.ACTION_PHONE_INCOME)){
            mReceiveNum = intent.getStringExtra("number");
            mSendNum = "";
            sPhoneState = STATE_RECEIVE;
        }
        if(action.equals("com.android.phone.FLYSCALE_PHONE_STATE")){
            //电话状态
            int state = intent.getIntExtra("phone_state", 0);
            if(state == INCOMING || state == CALL_WAITING){
                //呼入
                ifEndCall();
            }else if(state == DIALING){
                //呼出
            }else if(state == DISCONNECTED){
                //断开
                int callAlarmStatus = CallAlarmInstance.getInstance().getStatus();
                //如果通话成功（即报警成功）则报警结束
                if(callAlarmStatus == CallAlarmInstance.STATUS_ALARM_SUCCESS){
                    CallAlarmInstance.getInstance().setStatus(CallAlarmInstance.STATUS_ALARM_FINISH);
                }
                sPhoneState = STATE_NONE;
                mSendNum = "";
                mReceiveNum = "";
            }else if(state == ACTIVE){
                //接通
                if(CallAlarmInstance.getInstance().getStatus() == CallAlarmInstance.STATUS_ALARMING){
                    if(TextUtils.equals(mSendNum,PersistConfig.findConfig().getAlarmNum())
                            || TextUtils.equals(mSendNum,PersistConfig.findConfig().getSpecialNum())){
                        DDLog.w("语音报警成功！");
                        CallAlarmInstance.getInstance().setStatus(CallAlarmInstance.STATUS_ALARM_SUCCESS);
                        //电话接通，定时10秒后主动挂断
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                //挂断电话
                                PhoneUtil.endCall(context);
                            }
                        }, 10* 1000);
                    }
                }
            }
        }
    }



    /**
     * 不接受普通号码呼入  并且白名单不包含这个号码 直接挂断
     */
    public void ifEndCall(){
        if(!PersistConfig.findConfig().isAcceptOtherNum() && !PersistWhite.isContains(mReceiveNum)){
            PhoneUtil.endCall(BaseApplication.sContext);
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
