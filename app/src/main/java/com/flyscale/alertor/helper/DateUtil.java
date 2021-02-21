package com.flyscale.alertor.helper;

import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class DateUtil {

    public static boolean isMonOn = false;
    public static boolean isTuesOn = false;
    public static boolean isWedOn = false;
    public static boolean isThurOn = false;
    public static boolean isFriOn = false;
    public static boolean isSatOn = false;
    public static boolean isSunOn = false;

    private static String mYear;
    private static String mMonth;
    private static String mDay;
    private static String mWay;
    private static String mHour;
    private static String mMin;
    private static String mSecond;

    private static SimpleDateFormat sf = null;
    public static boolean[] isTodayOn = {false,false,false,false,false,false,false};

    public static String freq = "";
    public static String startTime = "";
    public static String endTime = "";

    /**
     * 根据二进制字符串判断FM开启日期
     *
     * */
    public static void setDateFM(String binary){
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
        mMin = String.valueOf(c.get(Calendar.MINUTE));
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
     * yyyy-MM-dd HH:mm 格式（自己可以修改成想要的时间格式）
     * @param startTime
     * @param endTime
     * @return
     */
    public static int timeCompare(String startTime, String endTime) {
        int i = 0;
        //注意：传过来的时间格式必须要和这里填入的时间格式相同
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        try {
            Date date1 = dateFormat.parse(splicingString2(startTime));//开始时间
            Date date2 = dateFormat.parse(splicingString2(endTime));//结束时间
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

    public static String getCurrentDate() {
        Date d = new Date();
        sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(d);
    }

    public static void setData(String data){
        freq = getFreq(data);
        startTime = getStartFMTime(data);
        endTime = getEndFMTime(data);
    }

    /**
     * 设置FM定时启动闹钟
     * */
    public static void setAlarmForFM(){
        int timeCompare = timeCompare(StringTimeHm(),startTime);
        if(timeCompare == 3){
            int today = getDayOfWeek();
            if(getFirstDay(today)!=0){
                if (getFirstDay(today) == today){
                    String time = getCurrentDate().substring(0,11) + splicingString(startTime);


                }else{

                }
            }
        }else if(timeCompare == 2 || timeCompare == 3){

        }
    }

}
