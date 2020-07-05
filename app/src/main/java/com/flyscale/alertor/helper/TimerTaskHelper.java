package com.flyscale.alertor.helper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author 高鹤泉
 * @TIME 2020/6/22 17:38
 * @DESCRIPTION 定时器
 */
public class TimerTaskHelper  {
    Timer mTimer;
    TimerTask mTimerTask;
    long mPeriodTime;

    public TimerTaskHelper(TimerTask timerTask, long periodTime) {
        mTimerTask = timerTask;
        mPeriodTime = periodTime;
        if(mTimer == null){
            mTimer = new Timer();
        }
    }

    public void start(int delay){
        mTimer.schedule(mTimerTask,delay,mPeriodTime);
    }

    public void stop(){
        if(mTimer != null){
            mTimer.cancel();
            if(mTimerTask != null){
                mTimerTask.cancel();
            }
            mTimer.purge();
        }
    }
}
