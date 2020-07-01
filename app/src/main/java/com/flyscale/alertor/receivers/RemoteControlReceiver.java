package com.flyscale.alertor.receivers;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.up.UDoorAlarm;
import com.flyscale.alertor.helper.ThreadPool;
import com.flyscale.alertor.netty.AlarmHelper;
import com.flyscale.alertor.netty.CallAlarmHelper;
import com.flyscale.alertor.netty.NettyHelper;

/**
 * @author 高鹤泉
 * @TIME 2020/6/23 15:38
 * @DESCRIPTION 暂无
 */
public class RemoteControlReceiver extends BroadcastReceiver {

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
                    Log.i(TAG, "onReceive: " + status);
                    if(status.equals("0001")){
                        //可以报警
                        PersistConfig.saveCanAlarm(true);
                    }else if(status.equals("1000")){
                        //不可以报警
                        PersistConfig.saveCanAlarm(false);
                    }

                    if(PersistConfig.findConfig().isCanAlarm()){
                        if(status.equals("0100")){
                            AlarmHelper.getInstance().polling(null);
                        }else if(status.equals("0010")){
                            CallAlarmHelper.getInstance().polling(null,true);
                        }else if(status.equals("0011")){
                            AlarmHelper.getInstance().polling(null,BaseData.TYPE_DOOR_ALARM_U);
                        }else if(status.equals("0101")){
                            //红外报警
                            //孟工说 红外的按照门磁的报警
                            AlarmHelper.getInstance().polling(null,BaseData.TYPE_DOOR_ALARM_U);
                        }else if(status.equals("1001")){
                            AlarmHelper.getInstance().polling(null,BaseData.TYPE_SMOKE_ALARM_U);
                        }else if(status.equals("1011")){
                            AlarmHelper.getInstance().polling(null,BaseData.TYPE_GAS_ALARM_U);
                        }
                    }
                }
            }
        });

    }
}
