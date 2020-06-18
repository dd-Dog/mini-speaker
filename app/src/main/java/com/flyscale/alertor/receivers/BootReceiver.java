package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.flyscale.alertor.MainActivity;
import com.flyscale.alertor.services.AlarmService;

/**
 * @author 高鹤泉
 * @TIME 2020/6/18 17:34
 * @DESCRIPTION 开机广播
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //开机启动服务
        Log.i("gaohequan", "onReceive: 开机了");
        context.startActivity(new Intent(context, MainActivity.class));
//        context.startActivity(new Intent(context, AlarmService.class));
    }
}
