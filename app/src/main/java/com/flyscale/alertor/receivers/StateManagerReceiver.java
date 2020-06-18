package com.flyscale.alertor.receivers;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.flyscale.alertor.helper.DateHelper;
import com.flyscale.alertor.services.AlarmService;

/**
 * @author 高鹤泉
 * @TIME 2020/6/15 11:40
 * @DESCRIPTION 暂无
 */
public class StateManagerReceiver {

    AlarmService mAlarmService;
    SimStateReceiver mSimStateReceiver;
    NetStateReceiver mNetStateReceiver;

    private final static int SIM_VALID = 0;
    private final static int SIM_INVALID = 1;
    private final static int NET_VALID = 2;
    private final static int NET_INVALID = 3;
    private int mSimState = -1;
    private int mNetState = -1;
    String TAG = "StateManagerReceiver";

    private final static String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    private final static String ACTION_NET_STATE_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";

    public StateManagerReceiver(AlarmService alarmService) {
        mAlarmService = alarmService;
        //注册sim卡广播
        mSimStateReceiver = new SimStateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SIM_STATE_CHANGED);
        alarmService.registerReceiver(mSimStateReceiver,intentFilter);
        //注册网络广播
        mNetStateReceiver = new NetStateReceiver();
        IntentFilter netFilter = new IntentFilter();
        netFilter.addAction(ACTION_NET_STATE_CHANGED);
        mAlarmService.registerReceiver(mNetStateReceiver,netFilter);
    }


    public class SimStateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_SIM_STATE_CHANGED)){
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                int state = tm.getSimState();
                switch (state){
                    case TelephonyManager.SIM_STATE_READY:
                        mSimState = SIM_VALID;
                        break;
                    default:
                        mSimState = SIM_INVALID;
                        break;
                }
                Log.i(TAG, "onReceive: mSimState = " + mSimState);
                setState();
            }
        }
    }

    public class NetStateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_NET_STATE_CHANGED)){
                NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
                if(networkInfo != null){
                    if(networkInfo.isConnected()){
                        mNetState = NET_VALID;
                    }else {
                        mNetState = NET_INVALID;
                    }
                }else {
                    mNetState = NET_INVALID;
                }
                Log.i(TAG, "onReceive: mNetState = " + mNetState);
                setState();
            }
        }
    }

    /**
     * 设置当前的状态
     */
    public void setState(){
        if(mSimState == SIM_INVALID){
            mAlarmService.setState(AlarmService.STATE_NO_SIM);
        }else if(mSimState == SIM_VALID && mNetState == NET_INVALID){
            mAlarmService.setState(AlarmService.STATE_NO_NET);
        }else if(mSimState == SIM_VALID && mNetState == NET_VALID){
            mAlarmService.setState(AlarmService.STATE_SIM_NET_SUCCESS);
        }
    }

    public void destroy(){
        mAlarmService.unregisterReceiver(mSimStateReceiver);
        mAlarmService.unregisterReceiver(mNetStateReceiver);
    }
}
