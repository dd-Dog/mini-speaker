package com.flyscale.alertor.alarmManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistPair;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.ThreadPool;

/**
 * @author 高鹤泉
 * @TIME 2020/7/15 11:00
 * @DESCRIPTION 暂无
 */
public class RemoteControlReceiver extends BroadcastReceiver {
    final String ACTION = "flyscale.privkey.REMOTE_CONTROL";
    String TAG = "RemoteControlReceiver";

    static long sGasTime = 0;//5
    static long sSmokeTime = 0;//3
    static long sDoorTime = 0;//1
    static long sInfrared = 0 ;//1

    @Override
    public void onReceive(Context context, final Intent intent) {
        if(TextUtils.equals(intent.getAction(),ACTION)){
            long tempTime = System.currentTimeMillis();
            if(tempTime - sGasTime < 5000){
                return;
            }
            if(tempTime - sSmokeTime < 3000){
                return;
            }
            if(tempTime - sDoorTime < 1000){
                return;
            }
            if(tempTime - sInfrared < 1000){
                return;
            }
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
                MediaHelper.play(MediaHelper.PAIR_ARMING,true);
            }else if(status.equals("1000")){
                //撤防
                PersistConfig.saveIsArming(false);
                MediaHelper.play(MediaHelper.PAIR_DISARMING,true);
            }
            if(TextUtils.equals(status,"0011") || TextUtils.equals(status,"0101")){
                if(TextUtils.equals(status,"0011")){//门磁
                    if(!PersistPair.findPair().isDoor()){
                        MediaHelper.play(MediaHelper.PAIR_DOOR,true);
                        PersistPair.saveDoor(true);
                        return;
                    }
                    sDoorTime = System.currentTimeMillis();
                }
                if(TextUtils.equals(status,"0101")){//红外
                    if(!PersistPair.findPair().isInfrared()){
                        MediaHelper.play(MediaHelper.PAIR_INFRARED,true);
                        PersistPair.saveInfrared(true);
                        return;
                    }
                    sInfrared = System.currentTimeMillis();
                }
                //如果是门磁和红外 孟工说 红外的按照门磁的报警
                if(PersistConfig.findConfig().isArming()){
                    //如果布防
                    AlarmManager.finishLastAlarmOrReceive();
                    AlarmManager.pollingAlarm(BaseData.TYPE_DOOR_ALARM_U,false);
                }
            }else if(status.equals("0100")){
                if(!PersistPair.findPair().isRemoteControl()){
                    MediaHelper.play(MediaHelper.PAIR_REMOTE_CONTROL,true);
                    PersistPair.saveControl(true);
                }else {
                    AlarmManager.pressAlarmKey();
                }
            }else if(status.equals("0010")){
                AlarmManager.press110Key();
            }else if(status.equals("1001")){
                if(PersistPair.findPair().isSmoke()){
                    //烟感
                    sSmokeTime = System.currentTimeMillis();
                    AlarmManager.finishLastAlarmOrReceive();
                    AlarmManager.pollingAlarm(BaseData.TYPE_SMOKE_ALARM_U,false);
                }else {
                    MediaHelper.play(MediaHelper.PAIR_SMOKE,true);
                    PersistPair.saveSmoke(true);
                }
            }else if(status.equals("1011")){
                //气感
                if(PersistPair.findPair().isGas()){
                    sGasTime = System.currentTimeMillis();
                    AlarmManager.finishLastAlarmOrReceive();
                    AlarmManager.pollingAlarm(BaseData.TYPE_GAS_ALARM_U,false);
                }else {
                    MediaHelper.play(MediaHelper.PAIR_GAS,true);
                    PersistPair.saveGas(true);
                }
            }
        }
    }


}
