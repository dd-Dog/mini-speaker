package com.flyscale.alertor.devicestate;

import android.content.Context;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.BreakFMLitepalBean;
import com.flyscale.alertor.helper.BreakFMLitepalUtil;
import com.flyscale.alertor.helper.DateUtil;
import com.flyscale.alertor.helper.FMLitepalUtil;
import com.flyscale.alertor.helper.FMUtil;

public class RemoteBreakFMState implements IState {
    /**
     * 状态触发优先级
     */
    public static final int PRIORITY = 4;
    private StateManager stateManager;
    public static float freq = 0;
    public static int fmid = 0;
    public static Context context = BaseApplication.sContext;

    public RemoteBreakFMState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void start() {
        FMUtil.startFM(context);
        FMUtil.adjustFM(context,freq);
        String startTime = DateUtil.StringTimeHms();
        String endTime = BreakFMLitepalUtil.getEndTime(fmid);
        FMUtil.fmCallBack2(fmid,"0","0");
        long time = DateUtil.getFMDuration(startTime,endTime);
        FMUtil.stopBrFMAlarmManager(context,fmid,time);
    }

    @Override
    public void pause() {
        FMUtil.stopFM(context);
    }

    @Override
    public void stop() {
            FMUtil.stopFM(context);
            BreakFMLitepalBean litepalBean = new BreakFMLitepalBean();
            litepalBean.setName("FM" + fmid);
            litepalBean.setStartDate("0");
            litepalBean.setFreq("0.0");
            litepalBean.setStartTime("00:00");
            litepalBean.setEndTime("00:00");
            litepalBean.setVolume("0");
            litepalBean.setIsSetUp("false");
            litepalBean.setAddress("");
            litepalBean.setData("");
            litepalBean.updateAll("name = ?","FM" + BreakFMLitepalUtil.getCorrectLine(fmid));
            FMUtil.fmCallBack2(fmid,"0","0");
        stateManager.setStateByPriority(PRIORITY + 1, false);
    }
    @Override
    public int getPriority() {
        return PRIORITY;
    }

    public static float getFreq() {
        return freq;
    }

    public static void setFreq(float freq) {
        RemoteBreakFMState.freq = freq;
    }

    public static int getFmid() {
        return fmid;
    }

    public static void setFmid(int fmid) {
        RemoteBreakFMState.fmid = fmid;
    }
}
