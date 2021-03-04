package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.flyscale.alertor.data.persist.BreakFMLitepalBean;
import com.flyscale.alertor.devicestate.RemoteBreakFMState;
import com.flyscale.alertor.devicestate.RemotePlayFMState;
import com.flyscale.alertor.devicestate.StateManager;
import com.flyscale.alertor.helper.BreakFMLitepalUtil;
import com.flyscale.alertor.helper.DateUtil;
import com.flyscale.alertor.helper.FMLitepalUtil;
import com.flyscale.alertor.helper.FMUtil;
import com.flyscale.alertor.jni.NativeHelper;
import com.flyscale.alertor.netty.NettyHandler;
import com.flyscale.alertor.netty.NettyHelper;


public class FMReceiver extends BroadcastReceiver {
    private static boolean isBrFMplaying = false;
    private RemoteBreakFMState remoteBreakFMState;
    private RemotePlayFMState remotePlayFMState;
    private StateManager stateManager;

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
            float freq = Float.parseFloat(intent.getStringExtra("freq"));
            DateUtil.updataAlarmForFMRepeat(fmid);
            Log.e("fengpj","定时器到时" + fmid + " 周期" + weekly);
            if(DateUtil.isTodayOn()){
                RemotePlayFMState.setFmid(fmid);
                RemotePlayFMState.setFreq(freq);
                stateManager.setStateByPriority(RemotePlayFMState.PRIORITY,true);
//                FMUtil.startFM(context);
//                FMUtil.adjustFM(context,freq);
//                FMUtil.fmCallBack(fmid,"0","0");
//                String startTime = DateUtil.StringTimeHms();
//                String endTime = FMLitepalUtil.getEndTime(fmid);
//                long time = DateUtil.getFMDuration(startTime,endTime);
//                FMUtil.stopFMAlarmManager(context,fmid,time);
            }
        }else if(action.equals("FLYSCALE_ALARMMANAGER_FM_STOP")){
            int fmid = intent.getIntExtra("fmId",0);
            RemotePlayFMState.setFmid(fmid);
//            if (!isBrFMplaying) {
//                FMUtil.stopFM(context);
//            }
//            FMUtil.fmCallBack(fmid,"1","0");
            remotePlayFMState.stop();
        }else if(action.equals("FLYSCALE_ALARMMANAGER_BRFM_START")){
            int fmid = intent.getIntExtra("fmId",0);
            float freq = Float.parseFloat(intent.getStringExtra("freq"));
            RemoteBreakFMState.setFmid(fmid);
            RemoteBreakFMState.setFreq(freq);
	    stateManager.setStateByPriority(RemoteBreakFMState.PRIORITY,true);
//            FMUtil.startFM(context);
//            FMUtil.adjustFM(context,freq);
//            String startTime = DateUtil.StringTimeHms();
//            String endTime = BreakFMLitepalUtil.getEndTime(fmid);
//            FMUtil.fmCallBack2(fmid,"0","0");
//            isBrFMplaying = true;
//            long time = DateUtil.getFMDuration(startTime,endTime);
//            FMUtil.stopBrFMAlarmManager(context,fmid,time);
        }else if(action.equals("FLYSCALE_ALARMMANAGER_BRFM_STOP")){
            int fmid = intent.getIntExtra("fmId",0);
            RemoteBreakFMState.setFmid(fmid);
	    remoteBreakFMState.stop();
//            FMUtil.stopFM(context);
//            BreakFMLitepalBean litepalBean = new BreakFMLitepalBean();
//            litepalBean.setName("FM" + fmid);
//            litepalBean.setStartDate("0");
//            litepalBean.setFreq("0.0");
//            litepalBean.setStartTime("00:00");
//            litepalBean.setEndTime("00:00");
//            litepalBean.setVolume("0");
//            litepalBean.setIsSetUp("false");
//            litepalBean.setAddress("");
//            litepalBean.setData("");
//            litepalBean.updateAll("name = ?","FM" + BreakFMLitepalUtil.getCorrectLine(fmid));
//            FMUtil.fmCallBack2(fmid,"0","0");
            //isBrFMplaying = false;
            //FMUtil.fmReduction();
        }
    }
}
