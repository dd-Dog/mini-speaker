package com.flyscale.alertor.devicestate;

import android.content.Context;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.helper.DateUtil;
import com.flyscale.alertor.helper.FMLitepalUtil;
import com.flyscale.alertor.helper.FMUtil;

public class RemotePlayFMState implements IState {
    /**
     * 状态触发优先级
     */
    public static final int PRIORITY = 5;
    private StateManager stateManager;
    public static float freq = 0;
    public static int fmid = 0;
    public static Context context = BaseApplication.sContext;
    public static boolean hasTask = false;

    public RemotePlayFMState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void start() {
        if (hasTask) {
            FMUtil.startFM(BaseApplication.sContext);
            FMUtil.adjustFM(context, freq);
            FMUtil.fmCallBack(fmid, "0", "0");
            String startTime = DateUtil.StringTimeHms();
            String endTime = FMLitepalUtil.getEndTime(fmid);
            long time = DateUtil.getFMDuration(startTime, endTime);
            FMUtil.stopFMAlarmManager(context, fmid, time);
        }else {
            stop();
        }
    }

    @Override
    public void pause() {
        FMUtil.stopFM(context);
    }

    @Override
    public void stop() {
        if(hasTask){
            FMUtil.stopFM(context);
            FMUtil.fmCallBack(fmid,"1","0");
            hasTask =false;
            stateManager.setStateByPriority(PRIORITY + 1, false);
        }else {
            hasTask = false;
            stateManager.setStateByPriority(PRIORITY + 1, false);
        }
    }
    @Override
    public int getPriority() {
        return PRIORITY;
    }

    public static float getFreq() {
        return freq;
    }

    public static void setFreq(float freq) {
        RemotePlayFMState.freq = freq;
    }

    public static int getFmid() {
        return fmid;
    }

    public static void setFmid(int fmid) {
        RemotePlayFMState.fmid = fmid;
    }

    public static boolean isHasTask() {
        return hasTask;
    }

    public static void setHasTask(boolean hasTask) {
        RemotePlayFMState.hasTask = hasTask;
    }
}
