package com.flyscale.alertor.helper;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.flyscale.alertor.receivers.TimingPlanReceiver;

/**
 * Created by liChang on 2021/2/8
 */
public class AlarmManagerUtil {

    private AlarmManager alarmManager;
    private Context mContext;
//    private static final long TIME_INTERVAL = 7 * 24 * 60 * 60;//闹钟执行任务的时间间隔
    private static final long TIME_INTERVAL = 5 * 60 * 1000;//测试时间

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
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +
//                DateHelper.transferTime(startTime, week), pendingIntent);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);//测试时间
    }

    @SuppressLint("NewApi")
    public void AlarmManagerWorkOnOthers(int requestCode, int week, String startTime, String endTime, String fileName,
                                         String voice, boolean beforePlay, long address) {
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
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + DateHelper.transferTime(startTime, week)
//                + TIME_INTERVAL, pendingIntent);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + TIME_INTERVAL, pendingIntent);//测试时间
    }

    public void cancelAlarm() {
        alarmManager.cancel(pendingIntent);
    }
}