package com.flyscale.alertor;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.flyscale.FlyscaleManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.flyscale.alertor.base.BaseActivity;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistWhite;
import com.flyscale.alertor.helper.DateHelper;
import com.flyscale.alertor.helper.FotaHelper;
import com.flyscale.alertor.services.AlarmService;

import java.util.List;

public class MainActivity extends BaseActivity {

    String TAG = "gaohequanTest";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        startService(new Intent(this, AlarmService.class));

        FlyscaleManager flyscaleManager = (FlyscaleManager) getSystemService("flyscale");
        flyscaleManager.createHotspot("FLY510L-BJQ", "12345678",  4);

        Log.i(TAG, "onCreate: 20200703:1610");

//        PersistWhite.saveNum("15902227963");
//        PersistWhite.deleteNum("15902227963");
//        PersistConfig.saveIsAcceptOtherNum(false);
//        PersistConfig.saveAlarmNum("099016886119");
//        PersistConfig.saveIsIpAlarmFirst(false);
//        PersistConfig.saveSpecialNum("15902227963");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
