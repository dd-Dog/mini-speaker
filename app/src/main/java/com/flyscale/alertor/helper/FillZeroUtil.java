package com.flyscale.alertor.helper;

/**
 * Created by liChang on 2021/2/7
 */

public class FillZeroUtil {

    /**
     * 后面补0
     * @param length 补完0后的总长度
     * @param data 补充数据
     * @return
     */
    public static String getString(String data, int length) {
        String retString = data;
        if (data.length() == length) {
            return retString;
        } else {
            for (int i = 0; i < length - data.length(); i++) {
                retString = retString + 0;
            }
        }
        return retString;
    }

    /**
     * 前面补0
     * @param length 补完0后的总长度
     * @param data 补充数据
     * @return
     */
    public static String getString(int length, String data) {
        String retString = data;
        if (data.length() == length) {
            return retString;
        } else {
            for (int i = 0; i < length - data.length(); i++) {
                retString = 0 + retString;
            }
        }
        return retString;
    }
}
