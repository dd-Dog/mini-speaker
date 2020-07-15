package com.flyscale.alertor.alarmManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.helper.ThreadPool;
import com.flyscale.alertor.netty.AlarmHelper;

/**
 * @author 高鹤泉
 * @TIME 2020/7/15 11:00
 * @DESCRIPTION 暂无
 */
@Deprecated
public class RemoteControlReceiver2 extends BroadcastReceiver {
    final String ACTION = "flyscale.privkey.REMOTE_CONTROL";
    String TAG = "RemoteControlReceiver";

    @Override
    public void onReceive(Context context, final Intent intent) {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if(TextUtils.equals(intent.getAction(),ACTION)){
                    String status = intent.getStringExtra("status");
                    //A: 0100
                    //B: 0010
                    //C: 0001
                    //D: 1000
                    //门磁：0011
                    //红外：0101
                    //烟感：1001
                    //气感：1011
                    //按键和传感器的8中状态
                    Log.i(TAG, "run: 遥控器接受的状态 ---- " + status);
                    if(status.equals("0001")){
                        //布防
                        PersistConfig.saveIsArming(true);
                    }else if(status.equals("1000")){
                        //撤防
                        PersistConfig.saveIsArming(false);
                    }
                    if(TextUtils.equals(status,"0011") || TextUtils.equals(status,"0101")){
                        //如果是门磁和红外 孟工说 红外的按照门磁的报警
                        if(PersistConfig.findConfig().isArming()){
                            //如果布防
                            AlarmManager.finishLastAlarmOrReceive();
                            AlarmManager.pollingAlarm(BaseData.TYPE_DOOR_ALARM_U,false);
                        }
                    }else if(status.equals("0100")){
                        AlarmManager.pressAlarmKey();
                    }else if(status.equals("0010")){
                        AlarmManager.press110Key();
                    }else if(status.equals("1001")){
                        //烟感
                        AlarmManager.finishLastAlarmOrReceive();
                        AlarmManager.pollingAlarm(BaseData.TYPE_SMOKE_ALARM_U,false);
                    }else if(status.equals("1011")){
                        AlarmManager.finishLastAlarmOrReceive();
                        AlarmManager.pollingAlarm(BaseData.TYPE_GAS_ALARM_U,false);
                    }
                }
            }
        });
    }


}
