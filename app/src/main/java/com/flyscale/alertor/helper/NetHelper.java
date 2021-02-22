package com.flyscale.alertor.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.flyscale.alertor.base.BaseApplication;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 作者 ： 高鹤泉
 * 时间 ： 2019/2/28 下午3:42
 */
public class NetHelper {
    //无网
    public static final int NONE = 0;
    //移动网络
    public static final int MOBILE = 1;
    //wifi
    public static final int WIFI = 2;

    /**
     * 获取网络类型
     * @return
     */
    public static String getNetworkTypeStr() {
        int type = getNetworkType();
        if (type == WIFI) {
            return "wifi";
        } else if (type == MOBILE) {
            TelephonyManager telephonyManager = (TelephonyManager) BaseApplication.sContext.getSystemService(Context.TELEPHONY_SERVICE);
            switch (telephonyManager.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "2g";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "3g";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "4g";
                default:
                    return "";
            }
        } else {
            return "";
        }
    }

    /**
     * 获取网络类型
     * @return
     */
    public static int getNetworkType(){
        ConnectivityManager connectivityManager = (ConnectivityManager) BaseApplication.sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                return WIFI;
            }else if(networkInfo.getType() == ConnectivityManager.TYPE_MOBILE){
                return MOBILE;
            }
        }else {
            return NONE;
        }
        return NONE;
    }

    /**
     * 判断网络是否可用
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 获取本机IP
     */
    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }
}
