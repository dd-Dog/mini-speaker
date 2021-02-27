package com.flyscale.alertor;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.flyscale.FlyscaleManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseActivity;
import com.flyscale.alertor.helper.ClientInfoHelper;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.LoginHelper;
import com.flyscale.alertor.helper.WifiUtil;
import com.flyscale.alertor.services.AlarmService;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity {

    String TAG = "gaohequanTest";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        startService(new Intent(this, AlarmService.class));

        FlyscaleManager flyscaleManager = (FlyscaleManager) getSystemService("flyscale");
        String iccid = ClientInfoHelper.getIMEI();
        if (TextUtils.isEmpty(iccid) || "null".equals(iccid)) {
            iccid = "0000";
        }
        int length = iccid.length();
//        flyscaleManager.createHotspot("FLY510L-BJQ_" + iccid.substring(length - 4, length), "12345678", 4);


        Log.i(TAG, "onCreate: 20200720:1430");

        //18199007916
        //13319054517
//        PersistWhite.saveNum("13319054517");
//        PersistWhite.deleteNum("15902227963");
//        PersistConfig.saveIsAcceptOtherNum(false);
//        PersistConfig.saveAlarmNum("15902227963");
//        PersistConfig.saveIsIpAlarmFirst(false);
//        PersistConfig.saveSpecialNum("19902012807");

        if (LoginHelper.isFlyDebug()){
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    connectWifi();
                }
            }, 10 * 1000, 10 * 1000);
        }
    }

    static boolean isWifiConnected = false;
    private void connectWifi() {
        boolean wifiOpen = WifiUtil.isWifiOpen(this);
        if (!wifiOpen) {
            DDLog.i("wifi关闭，正在打开wifi");
            WifiUtil.switchWifi(this, true);
            return;
        }
        if (!isWifiConnected){
            DDLog.i("wifi正在连接...");
            WifiUtil wifiUtil = new WifiUtil();
            wifiUtil.connectWifi(this, LoginHelper.getWifiSSID(), LoginHelper.getWifiPwd(), "WPA");
        }else {
            DDLog.i("wifi已连接");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new WifiReceiver(), filter);
    }

    static class WifiReceiver extends BroadcastReceiver {
        private static final String TAG = "wifiReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
                Log.i(TAG, "wifi信号强度变化");
            }
            //wifi连接上与否
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    Log.i(TAG, "wifi断开");
                    isWifiConnected = false;
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    //获取当前wifi名称
                    Log.i(TAG, "连接到网络 " + wifiInfo.getSSID());
                    isWifiConnected = true;
                }
            }
            //wifi打开与否
            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
                if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                    Log.i(TAG, "系统关闭wifi");
                } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                    Log.i(TAG, "系统开启wifi");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
