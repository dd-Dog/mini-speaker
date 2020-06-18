package com.flyscale.alertor.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 13:34
 * @DESCRIPTION 暂无
 */
public class DateHelper {

    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String yyyyMMddHHmm = "yyyyMMddHHmm";

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

}
