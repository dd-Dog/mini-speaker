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
    boolean isStop = true;


    public TimerTaskHelper(TimerTask timerTask, long periodTime) {
        mTimerTask = timerTask;
        mPeriodTime = periodTime;
        if(mTimer == null){
            mTimer = new Timer();
        }
    }

    public void start(int delay){
        if(mPeriodTime <= 0){
            mTimer.schedule(mTimerTask,delay);
        }else {
            mTimer.schedule(mTimerTask,delay,mPeriodTime);
        }
        isStop = false;
    }

    public void stop(){
        if(mTimer != null){
            mTimer.cancel();
            if(mTimerTask != null){
                mTimerTask.cancel();
            }
            mTimer.purge();
        }
        isStop = true;
    }

    public boolean isStop() {
        return isStop;
    }
}
