package com.flyscale.alertor.helper;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.flyscale.alertor.data.persist.PersistClock;
import com.flyscale.alertor.receivers.TimingPlanReceiver;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by liChang on 2021/2/8
 */
public class AlarmManagerUtil {

    private AlarmManager alarmManager;
    private Context mContext;
    private static final long TIME_INTERVAL = 24 * 60 * 60 * 1000;//闹钟执行任务的时间间隔 ---- 7天
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
        Log.i("TAG", "cancelAlarm: " + requestCode);
        PersistClock.saveAlarm(week, startTime, endTime, voice, beforePlay, address, requestCode, fileName);
        Intent intent = new Intent(mContext, TimingPlanReceiver.class);
        intent.putExtra("week", week);
        intent.putExtra("start", startTime);
        intent.putExtra("end", endTime);
        intent.putExtra("requestCode", requestCode);
        intent.putExtra("fileName", fileName);
        intent.putExtra("voice", voice);
        intent.putExtra("beforePlay", beforePlay);
        intent.putExtra("address", address);
        pendingIntent = PendingIntent.getBroadcast(mContext, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        String time = DateHelper.StringTimeHms();
        long persist = DateHelper.getFMDuration(time, endTime);
        long begin = DateHelper.getFMDuration(startTime, time);
        long times = 0;

        Log.i("TAG", "getAlarmManagerStart: 当前时间" + time);
        Log.i("TAG", "getAlarmManagerStart: 结束时间" + endTime);
        if (DateUtil.getDayOfWeek() == week) {
            if (begin > 0 && persist < 0) {
                //直接开始下一个定时
                times = 7 * TIME_INTERVAL - begin;
                Log.i("TAG", "getAlarmManagerStart: 下一个时间" + times);
            } else if (begin > 0 && persist > 0) {
                //中途插入
                Log.i("TAG", "getAlarmManagerStart: 中途插入的" + times);
            } else if (begin < 0 && persist > 0) {
                //还没到时间
                times = DateHelper.getFMDuration(time, startTime);
                Log.i("TAG", "getAlarmManagerStart: 还没到时间" + times);
            }
        } else if (DateUtil.getDayOfWeek() > week) {
            week = (7 - DateUtil.getDayOfWeek()) + week;
            times = week * TIME_INTERVAL - begin;
        } else if (DateUtil.getDayOfWeek() < week) {
            week = week - DateUtil.getDayOfWeek();
            times = week * TIME_INTERVAL + DateHelper.getFMDuration(time, startTime);
        }
        Log.i("TAG", "getAlarmManagerStart: 下次时间" + times);
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
        pendingIntent = PendingIntent.getBroadcast(mContext, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        String time = DateHelper.StringTimeHms();
        long begin = DateHelper.getFMDuration(startTime, time);
        long times;

        if (DateUtil.getDayOfWeek() > week) {
            week = (7 - DateUtil.getDayOfWeek()) + week;
            times = week * TIME_INTERVAL - begin;
        } else if (DateUtil.getDayOfWeek() < week) {
            week = week - DateUtil.getDayOfWeek();
            times = week * TIME_INTERVAL + DateHelper.getFMDuration(time, startTime);
        } else {
            times = 7 * TIME_INTERVAL - begin;
            Log.i("TAG", "getAlarmManagerStart: 下一个时间" + times);
        }
        Log.i("TAG", "getAlarmManagerStart: 下次时间" + times);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + times,
                pendingIntent);
    }

    public void cancelAlarm() {
        Log.i("TAG", "cancelAlarm: 执行取消定时");
        List<PersistClock> songs = LitePal.findAll(PersistClock.class);
        for (int i = 0; i < songs.size(); i++) {
            Log.i("TAG", "cancelAlarm: " + songs.get(i).getRequestCode());
            alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(mContext, TimingPlanReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(mContext, songs.get(i).getRequestCode(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
            LitePal.deleteAll(PersistClock.class, "requestCode = ?", String.valueOf(songs.get(i).getRequestCode()));
        }
    }

    public void cancelMusic(long persist) {
        if (persist > 0) {
            alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(mContext, TimingPlanReceiver.class);
            intent.putExtra("cancel", 1);
            pendingIntent = PendingIntent.getBroadcast(mContext, 6515649, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                    + persist, pendingIntent);
        }
    }

    public void initProgram() {
        List<PersistClock> songs = LitePal.findAll(PersistClock.class);
        if (!songs.isEmpty()) {
            for (int i = 0; i < songs.size(); i++) {
                PersistClock s = songs.get(i);
                int week = s.getWeek();
                String startTime = s.getStartTime();
                String endTime = s.getEndTime();
                String fileName = s.getFileName();
                String voice = s.getVoice();
                int requestCode = s.getRequestCode();
                boolean beforePlay = s.isBefore();
                long address = s.getAddress();
                if (address != 0) {
                    getAlarmManagerStart(requestCode, week, startTime, endTime, fileName, voice, beforePlay, address);
                }
            }
        }
    }
}