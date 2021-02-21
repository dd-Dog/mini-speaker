package com.flyscale.alertor.helper;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.flyscale.FlyscaleManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import com.flyscale.alertor.Constants;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PhoneManagerUtil {

    @SuppressLint("MissingPermission")
    public static List<String> getICCID(Context context) {
        List<String> iccids = new ArrayList<String>();
        FlyscaleManager fm = (FlyscaleManager) context.getSystemService("flyscale");
        if (fm == null) {
            DDLog.e("FlyscaleManager is null, get IccId failed!");
            return iccids;
        }
        int primaryCard = fm.getPrimaryCard();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE + primaryCard);
        if (tm == null) {
            DDLog.e("TelephonyManager is null for phoneId=" + primaryCard + ", get IccId failed!");
            return iccids;
        }
        String operator = tm.getSimOperator();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            SubscriptionManager sub = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            List<SubscriptionInfo> info = sub.getActiveSubscriptionInfoList();
            int count = sub.getActiveSubscriptionInfoCount();
            if (count > 0) {
                if (count > 1) {
                    iccids.add(info.get(0).getIccId());
                    iccids.add(info.get(1).getIccId());
                } else {
                    iccids.add(tm.getSimSerialNumber());
                }
            }
        } else {
            iccids.add(tm.getSimSerialNumber());
        }

        return iccids;
    }

    public static Integer getBatteryLevel(Context context) {
        return (Integer) SPUtil.get(context, Constants.BatteryInfo.BATTERY_LEVEL, 0);
    }

    public static Integer getBatteryStatus(Context context) {
        return (Integer) SPUtil.get(context, Constants.BatteryInfo.BATTERY_STATUS, 1);
    }

    public static Integer getPlugType(Context context) {
        return (Integer) SPUtil.get(context, Constants.BatteryInfo.PLUG_TYPE, 0);
    }

    public static Integer getBatteryHealth(Context context) {
        return (Integer) SPUtil.get(context, Constants.BatteryInfo.BATTERY_HEALTH, 0);
    }

    public static Integer getBatteryVoltage(Context context) {
        return (Integer) SPUtil.get(context, Constants.BatteryInfo.BATTERY_VOLTAGE, 0);
    }

    public static Integer getBatteryTemperature(Context context) {
        return (Integer) SPUtil.get(context, Constants.BatteryInfo.BATTERY_TEMPERATURE, 0);
    }

    public static Integer getTamperSwitch(Context context) {
        return (Integer) SPUtil.get(context, Constants.BatteryInfo.BATTERY_TEMPERATURE, 0);
    }

    /*
     * 获取SDK版本信息
     * */
    public static String getApiVersion() {
        return Build.VERSION.SDK;
    }

    /*
     * 获取系统版本
     * */
    public static String getReleaseVersion() {
        return Build.VERSION.RELEASE;
    }

    /*
     * 获取设备型号
     * */
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /*
     * 产品内部代码
     * */
    public static String getProduct() {
        return Build.PRODUCT;
    }

    /*
     * 固件编号
     * */
    public static String getDisplay() {
        return Build.DISPLAY;
    }

    /*
     * IMEI
     * */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getIMEI1(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    @SuppressLint("MissingPermission")
    public static String getIMEI2(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            return tm.getImei(1);
        } else {
            return "";
        }
    }

    /*
     * IMSI
     * */
    public static String getIMSI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = null;
        try {
            IMSI = telephonyManager.getSubscriberId();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return IMSI;
    }


    public static String getDeviceSN() {

        String serial = null;

        try {

            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {

            e.printStackTrace();

        }

        return serial;

    }

    public static String getDeviceSN2() {

        String serialNumber = android.os.Build.SERIAL;

        return serialNumber;
    }


    public static String getOEM() {
        return "SPRD";
    }

    public static String getPlatform() {
        return Build.HARDWARE;
    }

    public static String getSystem() {
        return "Android";
    }

    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    @SuppressLint("MissingPermission")
    public static Location getLocation(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = lm.getLastKnownLocation("gps");
        return lastKnownLocation;
    }

    public static String getSwVersion(Context context) {
        FlyscaleManager fm = (FlyscaleManager) context.getSystemService("flyscale");
//        return fm.getSystemSwVersion();
//        return "sp9280e_test";
        return Build.DISPLAY;
    }

    /**
     * 厂家
     * @return
     */
    public static String getFactory() {
        return "FlyScale";
    }

    @SuppressLint("MissingPermission")
    public static String getMEID(Context context) {
        String meidStr = "-1";
        FlyscaleManager fm = (FlyscaleManager) context.getSystemService(FlyscaleManager.FLYSCALE_SERVICE);
        if (fm == null) {
            DDLog.e("FlyscaleManager is null, get meid failed!");
            return meidStr;
        }
        int phoneType = fm.getCurrentPhoneType();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (phoneType == TelephonyManager.PHONE_TYPE_GSM) {
            DDLog.e("show IMEI");
        } else if (phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
            DDLog.e("show MEID");
            meidStr = telephonyManager.getDeviceId();
            return meidStr;
        }
        return meidStr;
    }

}
