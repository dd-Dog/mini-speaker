package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.flyscale.alertor.helper.DateUtil;
import com.flyscale.alertor.helper.FMLitepalUtil;
import com.flyscale.alertor.helper.FMUtil;


public class FMReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(action.equals("FLYSCALE_FM_INFORMATION")){
            float a =intent.getFloatExtra("freq",0);
            boolean b = intent.getBooleanExtra("isFmOn",false);
            boolean e = intent.getBooleanExtra("isMuted",false);
            String c = intent.getStringExtra("freqList");
            int d = intent.getIntExtra("count",0);
            Log.e("fengpj","FLYSCALE_FM_INFORMATION"+a+"\n"+b+"\n"+c+"\n"+d+"\n"+e);
        }else if(action.equals("FLYSCALE_FM_CALLBACK_INFORMATION")){
            float a =intent.getFloatExtra("freq",0);
            boolean b = intent.getBooleanExtra("isFmOn",false);
            boolean e = intent.getBooleanExtra("isMuted",false);
            String c = intent.getStringExtra("freqList");
            int d = intent.getIntExtra("count",0);
            Log.e("fengpj","FLYSCALE_FM_CALLBACK_INFORMATION"+a+"\n"+b+"\n"+c+"\n"+d+"\n"+e);
        }else if(action.equals("FLYSCALE_ALARMMANAGER_FM_START")){
            int fmid = intent.getIntExtra("fmId",0);
            String weekly = intent.getStringExtra("weekly");
            Float freq = Float.parseFloat(intent.getStringExtra("freq"));
            DateUtil.updataAlarmForFMRepeat(fmid);
            Log.e("fengpj","定时器到时" + fmid + " 周期" + weekly);
            if(DateUtil.isTodayOn()){
                FMUtil.startFM(context);
                FMUtil.adjustFM(context,freq);
                String startTime = DateUtil.StringTimeHms();
                String endTime = FMLitepalUtil.getEndTime(fmid);
                long time = DateUtil.getFMDuration(startTime,endTime);
                FMUtil.stopFMAlarmManager(context,fmid,time);
            }
        }else if(action.equals("FLYSCALE_ALARMMANAGER_FM_STOP")){
            FMUtil.stopFM(context);
        }
    }
}
