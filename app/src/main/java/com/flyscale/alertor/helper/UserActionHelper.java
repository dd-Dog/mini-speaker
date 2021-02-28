package com.flyscale.alertor.helper;

import android.text.TextUtils;

import com.flyscale.alertor.base.BaseApplication;

/**
 * @author 高鹤泉
 * @TIME 2020/6/19 14:12
 * @DESCRIPTION 暂无
 */
public class UserActionHelper {

    private static long sLastClickTime = 0;
    private static long sLastConnectTime = 0;


    /**
     * 是否静默
     * @return
     */
    public static boolean isMute(){
        boolean isMute = TextUtils.equals("1" , BaseApplication.sFlyscaleManager.getMuteState());
        return isMute;
    }

    /**
     * 再次连接速度是否过快
     * @param delay
     * @return
     */
    public static boolean isFastConnect(int delay){
        long time = System.currentTimeMillis();
        if(time - sLastConnectTime < delay){
            return true;
        }
        sLastConnectTime = time;
        return false;
    }


    /**
     * 快速点击了？
     * @param delay
     * @return
     */
    public static boolean isFastClick(int delay){
        long time = System.currentTimeMillis();
        if(time - sLastClickTime < delay){
            return true;
        }
        sLastClickTime = time;
        return false;
    }

    public static boolean isFastClick(){
        return isFastClick(500);
    }
}
