package com.flyscale.alertor.helper;

import android.util.Log;


import com.flyscale.alertor.base.BaseApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {


    private static String mYear;
    private static String mMonth;
    private static String mDay;
    private static String mWay;
    private static String mHour;
    private static String mMin;
    private static String mSecond;

    private static SimpleDateFormat sf = null;
    public static boolean[] isTodayOn = {false,true,false,true,false,false,false};



    public static final long INTERVAL_DAY = 86400000L;
    public static final long INTERVAL_FIFTEEN_MINUTES = 900000L;
    public static final long INTERVAL_HALF_DAY = 43200000L;
    public static final long INTERVAL_HALF_HOUR = 1800000L;
    public static final long INTERVAL_HOUR = 3600000L;

    /**
     * 根据二进制字符串判断FM开启日期
     *
     * */
    public static void setDateFM(String binary){
        Log.e("fengpj","当前广播开启周期 = " + binary);
        int length = binary.length();
        for (int i=0;i<7;i++){
            isTodayOn[i] = binary.substring(length-i-1,length-i).equals("0");
        }
    }

    /**
     * 获取当前日期
     * */
    public static String StringData(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            mWay ="天";
        }else if("2".equals(mWay)){
            mWay ="一";
        }else if("3".equals(mWay)){
            mWay ="二";
        }else if("4".equals(mWay)){
            mWay ="三";
        }else if("5".equals(mWay)){
            mWay ="四";
        }else if("6".equals(mWay)){
            mWay ="五";
        }else if("7".equals(mWay)){
            mWay ="六";
        }
        return mYear + "年" + mMonth + "月" + mDay+"日"+"/星期"+mWay;
    }

    /**
     * 获取当前日期
     * HH:mm
     * */
    public static String StringTimeHm(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        mHour = completionString(mHour);
        mMin = String.valueOf(c.get(Calendar.MINUTE));
        mMin = completionString(mMin);
        return mHour + ":" + mMin;
    }

    /**
     * 获取当前星期几
     * */
    public static int getDayOfWeek(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int dayOfWeek = 0;
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWay)){
            dayOfWeek = 7;
        }else if("2".equals(mWay)){
            dayOfWeek = 1;
        }else if("3".equals(mWay)){
            dayOfWeek = 2;
        }else if("4".equals(mWay)){
            dayOfWeek = 3;
        }else if("5".equals(mWay)){
            dayOfWeek = 4;
        }else if("6".equals(mWay)){
            dayOfWeek = 5;
        }else if("7".equals(mWay)){
            dayOfWeek = 6;
        }
        return dayOfWeek;
    }

    /**
     * 判断2个时间大小
     * yyyy-MM-dd HH:mm:ss 格式（自己可以修改成想要的时间格式）
     * @param startTime
     * @param endTime
     * @return
     */
    public static int timeCompare(String startTime, String endTime) {
        int i = 0;
        //注意：传过来的时间格式必须要和这里填入的时间格式相同
        Log.e("fengpj","startTime = " + startTime + "  endTime = " + endTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        try {
            Date date1 = dateFormat.parse(splicingString(startTime));//开始时间
            Date date2 = dateFormat.parse(splicingString(endTime));//结束时间
            // 1 结束时间小于开始时间 2 开始时间与结束时间相同 3 结束时间大于开始时间
            if (date2.getTime() < date1.getTime()) {
                //结束时间小于开始时间
                i = 1;
            } else if (date2.getTime() == date1.getTime()) {
                //开始时间与结束时间相同
                i = 2;
            } else if (date2.getTime() > date1.getTime()) {
                //结束时间大于开始时间
                i = 3;
            }
        } catch (Exception e) {
            Log.e("fengpj","时间未能正常对比");
        }
        return i;
    }

    /**
     * 获取所需频道
     * */
    public static String getFreq(String data){
        String freq = data.substring(7,14);
        return freq;
    }

    /**
     * 获取开始播放时间
     * */
    public static String getStartFMTime(String data){
        String startTime = data.substring(15,21);
        return startTime;
    }

    /**
     * 获取结束播放时间
     * */
    public static String getEndFMTime(String data){
        String endTime = data.substring(22,28);
        return endTime;
    }

    /**
     * 转化拼接字符串  调整时间表达
     * HH:mm:ss
     * */
    public static String splicingString(String string){
        String hour = string.substring(0,2);
        String min = string.substring(2,4);
        String second = string.substring(4);
        String time = hour + ":" + min + ":" + second;
        return  time;
    }

    /**
     * 转化拼接字符串  调整时间表达
     * HH:mm
     * */
    public static String splicingString2(String string){
        String hour = string.substring(0,2);
        String min = string.substring(2,4);
        String time = hour + ":" + min;
        return  time;
    }

    /**
     * 转化拼接字符串  调整时间表达
     * HH:mm
     * */
    public static String splicingString3(String string){
        String year = string.substring(0,4);
        String month = string.substring(4,6);
        String day = string.substring(6,8);
        String hour = string.substring(8,10);
        String min = string.substring(10,12);
        String second  = string.substring(12);
        String time = year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + second;
        return  time;
    }

    /**
     * 筛选可用的日期
     * */
    public static int getFirstDay(int today){
        for(int i=0;i < 7;i++){
            if(isTodayOn[today%7]){
                today++;
                if (today ==7){
                    return today;
                }
                return today%7;
            }
            Log.e("fengpj",""+ isTodayOn[today%7]);
            today++;
        }
        return 0;
    }

    /**
     *获取当前日期
     */
    public static String getCurrentDate() {
        Date d = new Date();
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(d);
    }



    /**
     * 获取当前时分秒
     * HH:mm：ss
     * */
    public static String StringTimeHms(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        mHour = completionString(mHour);
        mMin = String.valueOf(c.get(Calendar.MINUTE));
        mMin = completionString(mMin);
        mSecond = String.valueOf(c.get(Calendar.SECOND));
        mSecond = completionString(mSecond);
        return mHour + mMin + mSecond;
    }

    /**
     * 获取当前具体时间
     * YY:MM:DD HH:mm：ss
     * */
    public static String StringTimeYmdhms(){
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        mYear = String.valueOf(c.get(Calendar.YEAR));
        mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
        mMonth = completionString(mMonth);
        mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        mDay = completionString(mDay);
        mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        mHour = completionString(mHour);
        mMin = String.valueOf(c.get(Calendar.MINUTE));
        mMin = completionString(mMin);
        mSecond = String.valueOf(c.get(Calendar.SECOND));
        mSecond = completionString(mSecond);
        return mYear + mMonth + mDay + mHour + mMin + mSecond;
    }

    /**
     * 补全时间字符
     * */
    private static String completionString(String string){
        if (string.length() == 1){
            string = "0"+string;
        }
        return string;
    }

    /**
     * 获取两个时间的差值
     * HH:mm:ss
     * */
    public static long getTimeDiff(String currentTime,String startTime){
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        try
        {
            Date d1 = df.parse(splicingString(currentTime));
            Date d2 = df.parse(splicingString(startTime));
            long diff = d2.getTime() - d1.getTime();//这样得到的差值是毫秒级别
            long days = diff / (1000 * 60 * 60 * 24);

            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
            //System.out.println(""+days+"天"+hours+"小时"+minutes+"分");
            return diff;
        }catch (Exception e) {
            Log.e("fengpj","无法正确获取时间差");
        }
        return 0;
    }

    /**
     * 获取两个时间的差值
     * yyyy-MM-dd HH:mm:ss
     * */
    public static long getTimeDiff2(String currentTime,String startTime){
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            Date d1 = df.parse(currentTime);
            Date d2 = df.parse(splicingString3(startTime));
            long diff = d2.getTime() - d1.getTime();//这样得到的差值是毫秒级别
            long days = diff / (1000 * 60 * 60 * 24);

            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
            //System.out.println(""+days+"天"+hours+"小时"+minutes+"分");
            return diff;
        }catch (Exception e) {
            Log.e("fengpj","无法正确获取时间差");
        }
        return 0;
    }

    /**
     * 获取fm持续时间
     * HH:mm:ss
     * */
    public static long getFMDuration(String startTime,String endTime){
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        Log.e("fengpj","getFMDuration startTime = " + startTime + " endTime = " +endTime);
        try
        {
            Date d1 = df.parse(splicingString(startTime));
            Date d2 = df.parse(splicingString(endTime));
            long diff = d2.getTime() - d1.getTime();//这样得到的差值是毫秒级别
            long days = diff / (1000 * 60 * 60 * 24);

            long hours = (diff-days*(1000 * 60 * 60 * 24))/(1000* 60 * 60);
            long minutes = (diff-days*(1000 * 60 * 60 * 24)-hours*(1000* 60 * 60))/(1000* 60);
            //System.out.println(""+days+"天"+hours+"小时"+minutes+"分");
            return diff;
        }catch (Exception e) {
            Log.e("fengpj","未能正确获得FM持续时间");
        }
        return 0;
    }

    /**
     * 判断隔天fm是否开启
     * */
    public  static boolean isTomorrowOn(){
        int date = getDayOfWeek();
        if(date == 6){
            date = 0;
        }
        return isTodayOn[date];
    }

    /**
     * 判断今天fm是否开启
     * */
    public  static boolean isTodayOn(){
        int date = getDayOfWeek()-1;
        return isTodayOn[date];
    }

    /**
     * 设置FM定时启动闹钟
     * */
    public static void updataAlarmForFM(int id,String weeklyrecord,String freq,String startTime,
                                        String endTime,String volumn){
        if (timeCompare(StringTimeHms(),startTime) == 3){
            Log.e("fengpj","广播"+id+"今日设置时间还未开始 时间差" + getTimeDiff(StringTimeHms(),startTime));
            FMUtil.cancelFMAlarmManager(BaseApplication.sContext,id);
            FMUtil.startFMAlarmManager(BaseApplication.sContext,id,getTimeDiff(StringTimeHms(),startTime),weeklyrecord,freq);
        }else if(timeCompare(StringTimeHms(),startTime) == 1 && timeCompare(StringTimeHms(),endTime) == 3){
            Log.e("fengpj","广播"+id+"今日设置时间已开始但是还未结束");
            FMUtil.cancelFMAlarmManager(BaseApplication.sContext,id);
            FMUtil.startFMAlarmManager(BaseApplication.sContext,id,0,weeklyrecord,freq);
        }else if (timeCompare(StringTimeHms(),endTime) == 1){
            Log.e("fengpj","广播"+id+"今日设置时间已过 不再设置 时间差" +(INTERVAL_DAY - getTimeDiff(startTime,StringTimeHms())) );
            FMUtil.cancelFMAlarmManager(BaseApplication.sContext,id);
            FMUtil.startFMAlarmManager(BaseApplication.sContext,id,INTERVAL_DAY - getTimeDiff(startTime,StringTimeHms()),weeklyrecord,freq);
        }
        Log.e("fengpj",id+"\n"+weeklyrecord+"\n"+freq+"\n"+startTime+"\n"+endTime+"\n"+volumn);
    }


    /**
     * 设定重复闹钟
     * */
    public static void updataAlarmForFMRepeat(int id){
        String freq = FMLitepalUtil.getFreq(id);
        String weeklyrecord = FMLitepalUtil.getWeeklyRecord(id);
        setDateFM(FMUtil.toBinary(Integer.valueOf(weeklyrecord).intValue()));
        //Log.e("fengpj","" + FMUtil.toBinary(Integer.valueOf(weeklyrecord).intValue()));
        Log.e("fengpj","id = " + id + "  isTodayOn = " + isTodayOn());
        //if(isTomorrowOn())
            FMUtil.cancelFMAlarmManager(BaseApplication.sContext, id);
            FMUtil.startFMAlarmManager(BaseApplication.sContext, id, INTERVAL_DAY - getTimeDiff(FMLitepalUtil.getStartTime(id),StringTimeHms()), weeklyrecord,freq);
//        }else{
//            FMUtil.cancelFMAlarmManager(BaseApplication.sContext, id);
//            FMUtil.startFMAlarmManager(BaseApplication.sContext, id, 0 + INTERVAL_DAY, weeklyrecord,freq);
//        }
    }


    /**
     * 设置插入FM定时启动闹钟
     * */
    public static void updataAlarmForBrFM(int id,String startDate,String freq,String startTime,
                                        String endTime,String volumn){
        String starttime = startDate + startTime;
        long time = getTimeDiff2(getCurrentDate(),starttime);
        FMUtil.startBrFMAlarmManager(BaseApplication.sContext,id,time,freq);
        Log.e("fengpj","设置插入FM定时启动闹钟"+ id+"\n"+startDate+"\n"+freq+"\n"+startTime+"\n"+endTime+"\n"+volumn);
    }

}
