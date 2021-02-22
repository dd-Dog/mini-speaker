package com.flyscale.alertor.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class FMUtil {

    public static float freq = 0f;
    public static boolean isFmOn = false;
    public static boolean isMuted = false;
    public static String freqList = "";
    public static int count = 0;

    public FMUtil() {
    }

    /**
     * 十进制转二进制
     * */
    public static String toBinary(int date){
        String binary = Integer.toBinaryString(date);
        return binary;
    }


    public static void startFM(Context context){
        Intent intent = new Intent("com.android.flyscale.FM_START");
        context.sendBroadcast(intent);
    }

    public static void pauseFM(Context context){
        Intent intent = new Intent("com.android.flyscale.FM_PAUSE");
        context.sendBroadcast(intent);
    }

    public static void searchFM(Context context){
        Intent intent = new Intent("com.android.flyscale.FM_SEARCH");
        context.sendBroadcast(intent);
    }

    public static void stopFM(Context context){
        Intent intent = new Intent("com.android.flyscale.FM_STOP");
        context.sendBroadcast(intent);
    }

    public static void adjustFM(Context context,float freq){
        Intent intent = new Intent("com.android.flyscale.FM_ADJUST");
        intent.putExtra("freq",freq);
        context.sendBroadcast(intent);
    }

    public static void informationFM(Context context){
        Intent intent = new Intent("com.android.flyscale.FM_INFORMATION");
        context.sendBroadcast(intent);
    }

    public static float getFreq() {
        return freq;
    }

    public static void setFreq(float freq) {
        FMUtil.freq = freq;
    }

    public static boolean isIsFmOn() {
        return isFmOn;
    }

    public static void setIsFmOn(boolean isFmOn) {
        FMUtil.isFmOn = isFmOn;
    }

    public static boolean isIsMuted() {
        return isMuted;
    }

    public static void setIsMuted(boolean isMuted) {
        FMUtil.isMuted = isMuted;
    }

    public static String getFreqList() {
        return freqList;
    }

    public static void setFreqList(String freqList) {
        FMUtil.freqList = freqList;
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        FMUtil.count = count;
    }

    public static void setFMStatus(boolean isFmOn,boolean isMuted,String freqList,float freq,int count){
        FMUtil.isFmOn = isFmOn;
        FMUtil.isMuted = isMuted;
        FMUtil.freqList = freqList;
        FMUtil.freq = freq;
        FMUtil.count = count;
    }

    /**
     * 设定fm启动闹钟
     * */
    public static void startFMAlarmManager(Context context,int fmId,long time,String weekly,String freq){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("FLYSCALE_ALARMMANAGER_FM_START");
        intent.putExtra("fmId",fmId);
        intent.putExtra("weekly",weekly);
        intent.putExtra("freq",freq);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, fmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent);
        Log.e("fengpj","定时启动设置"+fmId + "当前时间" + System.currentTimeMillis() + "设置时间" +(System.currentTimeMillis() + time) );
    }

    /**
     * 设定fm停止闹钟
     * */
    public static void stopFMAlarmManager(Context context,int fmId,long time){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("FLYSCALE_ALARMMANAGER_FM_STOP");
        intent.putExtra("fmId",fmId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, fmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent);
        Log.e("fengpj","定时启动设置"+fmId + "当前时间" + System.currentTimeMillis() + "设置时间" +(System.currentTimeMillis() + time) );
    }

    /**
     * 取消fm闹钟提醒
     */
    public static void cancelFMAlarmManager(Context context,int fmId)
    {
        // 取消AlarmManager的定时服务
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent("FLYSCALE_ALARMMANAGER_FM_START");// 和设定闹钟时的action要一样
        // 这里PendingIntent的requestCode、intent和flag要和设定闹钟时一样
        PendingIntent pi=PendingIntent.getBroadcast(context, fmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pi);
        Log.e("fengpj","取消定时设置"+fmId);
    }
}
