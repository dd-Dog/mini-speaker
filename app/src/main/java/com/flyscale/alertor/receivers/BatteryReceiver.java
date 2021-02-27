package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.Constants;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.packet.TcpPacketFactory;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.helper.ClientInfoHelper;
import com.flyscale.alertor.helper.DateHelper;
import com.flyscale.alertor.helper.FillZeroUtil;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.PhoneManagerUtil;
import com.flyscale.alertor.helper.SPUtil;
import com.flyscale.alertor.led.LedInstance;
import com.flyscale.alertor.netty.NettyHelper;

/**
 * @author 高鹤泉
 * @TIME 2020/6/17 13:57
 * @DESCRIPTION 暂无
 */
public class BatteryReceiver extends BroadcastReceiver {


    public static int sBatteryLevel = -1;
    int mLastBatteryLevel = -1;
    //    int mBatteryStatus = BatteryManager.BATTERY_STATUS_UNKNOWN;
    String TAG = "BatteryReceiver";
    private int mBatteryLevel = 100;
    private int mBatteryStatus = 1;
    private int mPlugType = 0;
    private int batteryVoltage;
    private int mTrampSwitch = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
//        Log.i(TAG, "onReceive: action = " + action);
        mBatteryLevel = intent.getIntExtra("level", 100);
        mBatteryStatus = intent.getIntExtra("status", 1);
        mPlugType = intent.getIntExtra("plugged", 1);
        batteryVoltage = intent.getIntExtra("voltage", 0);

        if (mBatteryStatus != 1) {
            mBatteryStatus = 0;
        }

        SPUtil.put(context, Constants.BatteryInfo.BATTERY_LEVEL, mBatteryLevel);
        SPUtil.put(context, Constants.BatteryInfo.PLUG_TYPE, mPlugType);
        SPUtil.put(context, Constants.BatteryInfo.BATTERY_STATUS, mBatteryStatus);
        SPUtil.put(context, Constants.BatteryInfo.BATTERY_VOLTAGE, batteryVoltage);
        SPUtil.put(context, Constants.TrampSwitch.TRAMP_SWITCH, mTrampSwitch);

        if (TextUtils.equals(action, Intent.ACTION_BATTERY_CHANGED)) {
            mLastBatteryLevel = sBatteryLevel;
            sBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 100);
//            mBatteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS,BatteryManager.BATTERY_STATUS_UNKNOWN);
//            Log.i(TAG, "onReceive: sBatteryLevel = " + sBatteryLevel);
            whenIsChange();
        } else if (TextUtils.equals(action, Intent.ACTION_BATTERY_LOW)) {
            if (!BaseApplication.sFlyscaleManager.getAdapterState().equals("1")) {
                MediaHelper.play(MediaHelper.BATTERY_LOW_CHARGE, true);
            }
        } else if (TextUtils.equals(action, BRConstant.ACTION_AC)) {
            String status = intent.getStringExtra("status");
//            Log.i(TAG, "onReceive: sPlugged = " + status);
            whenIsCharge();
        }
    }

    /**
     * 第一次为当前电量与登录时的电量比较
     * 电量变化超过20%，发送心跳消息，并且保存当前电量
     * 以后就是比较为当前电量与上一次保存电量
     */
    private void whenIsChange() {
        int battery = PersistConfig.findConfig().getBattery();
        if (sBatteryLevel - battery >= 20 || battery - sBatteryLevel >= 20) {
            if (battery == 0) {
                return;
            }
            Log.i(TAG, "whenIsChange: 电量变化超过20%");
            final long time = System.currentTimeMillis();
            NettyHelper.getInstance().send(TcpPacketFactory.createPacketSend(TcpPacketFactory.HEARTBEAT_DATA,
                    FillZeroUtil.getString(3, String.valueOf(PhoneManagerUtil.getBatteryLevel(BaseApplication.sContext))) + "/" +
                            DateHelper.longToString(time, DateHelper.yyyyMMdd_HHmmss) + "/" +
                            PhoneManagerUtil.getBatteryStatus(BaseApplication.sContext) + "/" +
                            (float) (Math.round((PhoneManagerUtil.getBatteryVoltage(BaseApplication.sContext).floatValue() / 1000) * 10)) / 10 + "/" +
                            36 + "/" +
                            PhoneManagerUtil.getTamperSwitch(BaseApplication.sContext) + "/" + ClientInfoHelper.getMusicVolume()
            ));
            PersistConfig.saveBattery(PhoneManagerUtil.getBatteryLevel(BaseApplication.sContext));
        }
    }

    public void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        // 电量低
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        // 从电量低恢复.
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        // AC充电
        filter.addAction(BRConstant.ACTION_AC);
        BaseApplication.sContext.registerReceiver(this, filter);
    }

    public void unRegister() {
        BaseApplication.sContext.unregisterReceiver(this);
    }

    /**
     * 当正在充电的时候
     */
    public void whenIsCharge() {
        if (BaseApplication.sFlyscaleManager.getAdapterState().equals("1")) {
            LedInstance.getInstance().showChargeLed();
        } else {
            LedInstance.getInstance().offChargeLed();
        }
    }
}
