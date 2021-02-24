package com.flyscale.alertor.helper;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.flyscale.alertor.receivers.TimingPlanReceiver;

/**
 * Created by liChang on 2021/2/8
 */
public class AlarmManagerUtil {

    private AlarmManager alarmManager;
    private Context mContext;

    PendingIntent pendingIntent;

    private AlarmManagerUtil(Context aContext) {
        this.mContext = aContext;
    }

    private static AlarmManagerUtil instance = null;

    public static AlarmManagerUtil getInstance(Context aContext) {
        if (instance == null) {
            synchronized (AlarmManagerUtil.class) {
                if (instance == null) {
                    instance = new AlarmManagerUtil(aContext);
                }
            }
        }
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getAlarmManagerStart(int requestCode, int week, String startTime, String endTime, String fileName,
                                     String voice, boolean beforePlay, long address) {
        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, TimingPlanReceiver.class);
        intent.putExtra("week", week);
        intent.putExtra("start", startTime);
        intent.putExtra("end", endTime);
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("fileName", fileName);
        intent.putExtra("voice", voice);
        intent.putExtra("beforePlay", beforePlay);
        intent.putExtra("address", address);
        pendingIntent = PendingIntent.getBroadcast(mContext, requestCode, intent, 0);

        String time = DateHelper.StringTimeHms();
        long persist = DateHelper.getFMDuration(time, endTime);
        long begin = DateHelper.getFMDuration(startTime, time);
        long times = 0;

        Log.i("TAG", "getAlarmManagerStart: 当前时间" + time);
        Log.i("TAG", "getAlarmManagerStart: 结束时间" + endTime);
        if (begin > 0 && persist < 0) {
            //直接开始下一个定时
            times = DateHelper.transferTime(startTime, week);
            Log.i("TAG", "getAlarmManagerStart: 下一个时间" + times);
        } else if (begin > 0 && persist > 0) {
            //中途插入
            Log.i("TAG", "getAlarmManagerStart: 中途插入的" + times);
        } else if (begin < 0 && persist >  0) {
            //还没到时间
            times = DateHelper.getFMDuration(time, startTime);
            Log.i("TAG", "getAlarmManagerStart: 还没到时间" + times);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + times, pendingIntent);
    }

    @SuppressLint("NewApi")
    public void AlarmManagerWorkOnOthers(int requestCode, int week, String startTime, String endTime, String fileName,
                                         String voice, boolean beforePlay, long address) {
        Log.i("TAG", "AlarmManagerWorkOnOthers: 执行重复定时任务");
        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mContext, TimingPlanReceiver.class);
        intent.putExtra("week", week);
        intent.putExtra("start", startTime);
        intent.putExtra("end", endTime);
        intent.putExtra("fileName", fileName);
        intent.putExtra("voice", voice);
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("beforePlay", beforePlay);
        intent.putExtra("address", address);
        pendingIntent = PendingIntent.getBroadcast(mContext, requestCode, intent, 0);

        long times = DateHelper.transferTime(startTime, week);
        Log.i("TAG", "AlarmManagerWorkOnOthers: 间隔时间=" + times);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + times,
                pendingIntent);
    }

    public void cancelAlarm() {
        alarmManager.cancel(pendingIntent);
    }

    public void cancelMusic(long persist) {
        if (persist > 0) {
            alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(mContext, TimingPlanReceiver.class);
            intent.putExtra("cancel", 1);
            pendingIntent = PendingIntent.getBroadcast(mContext, 6515649, intent, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                    + persist, pendingIntent);
        }
    }
}