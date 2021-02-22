package com.flyscale.alertor.helper;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 13:34
 * @DESCRIPTION 暂无
 */
public class DateHelper {

    /**
     * 一天的毫秒数
     */
    public static long ONE_DAY_MSEC = 86400000;
    public static long ONE_HOUR_MSEC = 3600000;
    public static long ONE_MINUTE_MSEC = 60000;
    public static long ONE_SECOND_MSEC = 1000;

    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String yyyyMMddHHmm = "yyyyMMddHHmm";
    public static final String yyyyMMdd = "yyyyMMdd";
    public static final String yyyyMMddHH_mm = "yyyyMMddHH:mm";
    public static final String yyyy_MM_dd_hh_mm_ss = "yyyy-MM-dd hh:mm:ss";
    public static final String yyyyMMdd_HHmmss = "yyyyMMdd-HHmmss";
    public static final String yyMMddHHmmss = "yyMMddHHmmss";


    /**
     * 日期转毫秒
     * @param date 符合pattern的日期格式
     * @param pattern
     * @return
     */
    public static long stringToLong(String date,String pattern){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.CHINESE);
        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1 == null ? 0 : date1.getTime();
    }

    /**
     * 毫秒转时间字符串
     * @param date
     * @param pattern
     * @return
     */
    public static String longToString(Long date,String pattern){
        if(date != null && date > 0){
            Date dateParam = new Date();
            dateParam.setTime(date);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            simpleDateFormat.applyPattern(pattern);
            String result = simpleDateFormat.format(dateParam);
            return result;
        }
        return "";
    }

    public static String longToString(String pattern){
        return longToString(System.currentTimeMillis(),pattern);
    }


    /**
     * 将时间转换成毫秒
     * @param time
     * @return
     */
    public static long transferTime(String time, int week) {
        //得到日历实例，主要是为了下面的获取时间
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        //获取当前毫秒值
        long systemTime = System.currentTimeMillis();
        //是设置日历的时间，主要是让日历的年月日和当前同步
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        // 这里时区需要设置一下，不然可能个别手机会有8个小时的时间差
        mCalendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        mCalendar.set(Calendar.DAY_OF_WEEK, week);
        mCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, 2)));
        mCalendar.set(Calendar.MINUTE, Integer.parseInt(time.substring(2, 4)));
        mCalendar.set(Calendar.SECOND, Integer.parseInt(time.substring(4, 6)));
        long selectTime = mCalendar.getTimeInMillis();
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
//        if(systemTime > selectTime) {
//            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
//        }
        return selectTime;
    }

}
