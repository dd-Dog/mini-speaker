package com.flyscale.alertor.helper;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.receivers.TimingPlanReceiver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liChang on 2021/2/8
 */
public class AlarmManagerUtil {

    private AlarmManager alarmManager;
    private Intent intent;
    private List<PendingIntent> list;
    private static AlarmManagerUtil alarmManagerUtils;
    private Context mContext;

    /**
     * 使用单例模式，避免重复new对象造成数据丢失
     *
     * @param context  上下文
     *
     * @return  实例
     */
    public static AlarmManagerUtil getInstance(Context context) {
        if (alarmManagerUtils == null) {
            alarmManagerUtils = new AlarmManagerUtil(context);
        }
        return alarmManagerUtils;
    }

    /**
     * 单例模式私有化构造方法，对AlarmManager和Intent进行初始化
     *
     * @param context  上下文
     */
    private AlarmManagerUtil(Context context){
        this.mContext = context;
        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(mContext, TimingPlanReceiver.class);
        list = PersistConfig.findConfig().getList();
    }

    /**
     * 开启按时间段重复执行的定时，适用于小于安卓API19
     *
     * @param param 执行命令的参数
     *
     * @param cmd   执行哪个命令
     *
     * @param requestCode   保证PendingIntent唯一
     *
     * @param triggerAtMillis   开始执行时间
     *
     * @param intervalMillis    间隔执行时间
     */
    public void setRepeating(String param, int cmd, int requestCode, long triggerAtMillis, long intervalMillis){
        intent.putExtra("param",param);
        intent.putExtra("cmd",cmd);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, requestCode,intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        PersistConfig.saveAlarmManager(list);
        list.add(pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,triggerAtMillis,intervalMillis,pendingIntent);
    }

    /**
     * 开启只执行一次的定时，适用于小于安卓API19
     *
     * @param param 命令参数
     *
     * @param cmd   执行命令
     *
     * @param requestCode   保证PendingIntent唯一
     *
     * @param triggerAtMillis   开始执行时间
     */
    public void set(String param,int cmd,int requestCode,long triggerAtMillis){
        intent.putExtra("param",param);
        intent.putExtra("cmd",cmd);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, requestCode,intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        list.add(pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP,triggerAtMillis,pendingIntent);
    }

    /**
     * 开启只执行一次的定时，适用于大于等于安卓API19
     *
     * @param param 命令参数
     *
     * @param cmd   执行命令
     *
     * @param requestCode   保证PendingIntent唯一
     *
     * @param triggerAtMills    开始执行时间
     *
     * @param windowLengthMillis    延迟多久执行
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void setWindow(String param, int cmd, int requestCode, long triggerAtMills, long windowLengthMillis){
        intent.putExtra("param",param);
        intent.putExtra("cmd",cmd);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, requestCode,intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        list.add(pendingIntent);
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP,triggerAtMills,windowLengthMillis,pendingIntent);
    }

    /**
     * 取消定时
     */
    public void alarCancel(){
        if (null == list){
            list = new ArrayList<>();
        }
        if (list.size() > 0){
            for (int i = 0; i < list.size(); i++){
                alarmManager.cancel(list.get(i));
            }
            list.clear();
        }
    }
}