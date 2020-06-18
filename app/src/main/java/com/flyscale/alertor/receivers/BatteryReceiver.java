package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.led.LedInstance;

/**
 * @author 高鹤泉
 * @TIME 2020/6/17 13:57
 * @DESCRIPTION 暂无
 */
public class BatteryReceiver extends BroadcastReceiver {


    int mBatteryLevel = -1;
    int mLastBatteryLevel = -1;
    int mBatteryStatus = BatteryManager.BATTERY_STATUS_UNKNOWN;
    int mPlugged = 0;


    @Override
    public void onReceive(Context context, Intent intent) {
        mLastBatteryLevel = mBatteryLevel;
        mBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,100);
        mBatteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS,BatteryManager.BATTERY_STATUS_UNKNOWN);
        mPlugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);

        whenIsCharge();
    }

    public void register(BatteryReceiver receiver){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        BaseApplication.sContext.registerReceiver(receiver,filter);
    }

    public void unRegister(BatteryReceiver receiver){
        BaseApplication.sContext.unregisterReceiver(receiver);
    }

    /**
     * 当正在充电的时候
     */
    public void whenIsCharge(){
        if(mPlugged != 0){
            LedInstance.getInstance().showChargeLed();
        }else {
            LedInstance.getInstance().offChargeLed();
        }
    }
}
