package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.led.LedInstance;

/**
 * @author 高鹤泉
 * @TIME 2020/6/17 13:57
 * @DESCRIPTION 暂无
 */
public class BatteryReceiver extends BroadcastReceiver {


    public static int sBatteryLevel = -1;
    int mLastBatteryLevel = -1;
    int mBatteryStatus = BatteryManager.BATTERY_STATUS_UNKNOWN;
    public static int sPlugged = 0;
    String TAG = "BatteryReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(TextUtils.equals(action,Intent.ACTION_BATTERY_CHANGED)){
            mLastBatteryLevel = sBatteryLevel;
            sBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,100);
            mBatteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS,BatteryManager.BATTERY_STATUS_UNKNOWN);
            sPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);

            Log.i(TAG, "onReceive: " + sBatteryLevel + " -- " + sPlugged);
            whenIsCharge();
        }else if(TextUtils.equals(action,Intent.ACTION_BATTERY_LOW)){
            MediaHelper.play(MediaHelper.BATTERY_LOW,true);
        }

    }

    public void register(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        // 电量低
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        // 从电量低恢复.
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        BaseApplication.sContext.registerReceiver(this,filter);
    }

    public void unRegister(){
        BaseApplication.sContext.unregisterReceiver(this);
    }

    /**
     * 当正在充电的时候
     */
    public void whenIsCharge(){
        if(sPlugged != 0){
            LedInstance.getInstance().showChargeLed();
        }else {
            LedInstance.getInstance().offChargeLed();
        }
    }
}
