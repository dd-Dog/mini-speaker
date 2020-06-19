package com.flyscale.alertor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.flyscale.alertor.base.BaseActivity;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.services.AlarmService;

public class MainActivity extends BaseActivity {

    String TAG = "gaohequanTest";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        startService(new Intent(this, AlarmService.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
