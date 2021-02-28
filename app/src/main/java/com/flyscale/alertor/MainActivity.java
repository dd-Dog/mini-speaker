package com.flyscale.alertor;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.flyscale.FlyscaleManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.flyscale.alertor.base.BaseActivity;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistWhite;
import com.flyscale.alertor.helper.ClientInfoHelper;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.DateHelper;
import com.flyscale.alertor.helper.FotaHelper;
import com.flyscale.alertor.helper.LoginHelper;
import com.flyscale.alertor.helper.WifiUtil;
import com.flyscale.alertor.media.MusicPlayer;
import com.flyscale.alertor.services.AlarmService;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends BaseActivity {

    String TAG = "gaohequanTest";
    private BroadcastReceiver mReceiver;
    TextView textView;
    MusicPlayer musicPlayer;
    public static Timer timer;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, AlarmService.class));
        textView = findViewById(R.id.textView);

        FlyscaleManager flyscaleManager = (FlyscaleManager) getSystemService("flyscale");
        String iccid = ClientInfoHelper.getIMEI();
        if (TextUtils.isEmpty(iccid) || "null".equals(iccid)) {
            iccid = "0000";
        }
        musicPlayer = MusicPlayer.getInstance();

        showProgram();  //根据广播改变节目显示

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
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("flyscale.music.start");
        intentFilter.addAction("flyscale.music.pause");
        intentFilter.addAction("flyscale.music.stop");
        intentFilter.addAction("flyscale.emr.start");
        intentFilter.addAction("FLYSCALE_FM_INFORMATION");
        registerReceiver(this.mReceiver, intentFilter);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new WifiReceiver(), filter);
    }

    private void showProgram() {
        this.mReceiver = new BroadcastReceiver() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (timer != null) {
                    timer.cancel();
                }
                assert action != null;
                switch (action) {
                    case "flyscale.music.start":
                        Log.i(TAG, "onReceive: MP3");
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(1);
                            }
                        }, 0, 100);
                        break;
                    case "flyscale.music.stop":
                        handler.sendEmptyMessage(3);
                        break;
                    case "flyscale.music.pause":
                        Log.i(TAG, "onReceive: 暂停了");
                        break;
                    case "flyscale.emr.start":
                        Log.i(TAG, "onReceive: 紧急语音");
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(2);
                            }
                        }, 0, 100);
                        break;
                    case "FLYSCALE_FM_INFORMATION":
                        float a =intent.getFloatExtra("freq",0);
                        boolean b = intent.getBooleanExtra("isFmOn",false);
                        if (b) {
                            textView.setText("FM " + a);
                        }
                        break;
                }
            }
        };
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //当前播放文件名
                    final String s = MusicPlayer.music.substring(MusicPlayer.music.lastIndexOf(File.separator)).replace("/", "");
                    textView.setText("MP3  " + DateHelper.ssToMM(musicPlayer.getTime()) + "/" + musicPlayer.getDuration());
                    break;
                case 2:
                    //紧急语音
                    textView.setText("紧急语音 " + DateHelper.ssToMM(musicPlayer.getTime()) + "/" + musicPlayer.getDuration());
                    break;
                case 3:
                    //啥也不显示
                    textView.setText("");
                    break;
            }
        }
    };

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
        unregisterReceiver(mReceiver);
    }
}
