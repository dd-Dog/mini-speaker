package com.flyscale.alertor;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.flyscale.alertor.base.BaseActivity;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.services.AlarmService;

public class MainActivity extends BaseActivity {


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
