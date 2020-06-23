package com.flyscale.alertor.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.flyscale.FlyscaleManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.TextView;

import com.flyscale.alertor.BuildConfig;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.receivers.BatteryReceiver;

import static android.content.Context.BATTERY_SERVICE;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 13:17
 * @DESCRIPTION 暂无
 */
public class ClientInfoHelper {
    /**
     * 获取imei
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getIMEI(){
        TelephonyManager telephonyManager = (TelephonyManager) BaseApplication.sContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        if(TextUtils.isEmpty(imei)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imei = telephonyManager.getImei();
            }
        }
        if(TextUtils.isEmpty(imei)){
            //android 10以上已经获取不了imei了 用 android id代替
            imei = Settings.System.getString(BaseApplication.sContext.getContentResolver(),Settings.Secure.ANDROID_ID);
        }
        if(TextUtils.isEmpty(imei)){
            imei = "imei_is_null";
        }
        return imei;
    }

    /**
     * 获取iccid
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getICCID(){
//        return "89860320249940634519";
//        return "89860320249940634527";
        TelephonyManager telephonyManager = (TelephonyManager) BaseApplication.sContext.getSystemService(Context.TELEPHONY_SERVICE);
        String iccid = telephonyManager.getSimSerialNumber();
        if(TextUtils.isEmpty(iccid)){
            iccid = "iccid_is_null";
        }
        return iccid;
    }

    /**
     * 电量百分比
     * @return
     */
    public static String getBatteryLevel(){
        return BatteryReceiver.sBatteryLevel + "%";
    }

    /**
     * 市电 市电 是否接入220V电源，0接入，1未接入
     * @return
     */
    public static int getAC(){
        return BatteryReceiver.sPlugged == 0 ? 1 : 0;
    }

    /**
     * 基站信息  STRING[10]  基站1@基站2@基站3；按基站距离排序由近到远
     * @return
     */
    public static String getStationsInfo(){
        return "0@0@0";
    }

    /**
     * 终端类型	STRING[10]	001TCL，002卡尔
     * 和周工保持一致 写死
     * @return
     */
    public static String getTerminalInfo(){
        return "019";
    }

    /**
     * //网络制式	STRING
     */
    public static String getNetType(){
        return InternetUtil.getNetworkTypeStr(InternetUtil.getNetworkState(BaseApplication.sContext));
    }

    /**
     * 型号	STRING	厂家定义终端型号
     * @return
     */
    public static String getTerminalModel(){
        String model = Build.MODEL;
        if(TextUtils.isEmpty(model)){
            model = "model_is_null";
        }
        return model;
    }

    /**
     * 终端第一次登录时间	STRING	时间格式：yyyy-MM-dd hh:mm:ss，记录终端第一次登录平台的时间，永久不变
     */
    public static String getFirstLoginTime(){
        return PersistConfig.findConfig().getFirstLogin();
    }
}
