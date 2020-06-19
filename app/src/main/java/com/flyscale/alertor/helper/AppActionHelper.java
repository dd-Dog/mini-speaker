package com.flyscale.alertor.helper;

/**
 * @author 高鹤泉
 * @TIME 2020/6/19 14:12
 * @DESCRIPTION 暂无
 */
public class AppActionHelper {

    private static long sLastClickTime = 0;


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
