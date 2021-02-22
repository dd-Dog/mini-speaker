package com.flyscale.alertor.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;

import com.flyscale.alertor.base.BaseApplication;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

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
    public static int getNetworkType() {
        ConnectivityManager connectivityManager = (ConnectivityManager) BaseApplication.sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return WIFI;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return MOBILE;
            }
        } else {
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

    /**
     * 得到基站数据
     */
    @SuppressLint("MissingPermission")
    public static  String getBaseData(Context context) {
        // lac连接基站位置区域码 cellid连接基站编码 mcc MCC国家码 mnc MNC网号
        // signalstrength连接基站信号强度

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        //获取所有的基站信息
        List<CellInfo> infoLists = telephonyManager.getAllCellInfo();
        DDLog.d( "infoLists=" + infoLists);
        if (infoLists.size() != 0) {
            for (CellInfo info : infoLists) {
                if (info.toString().contains("CellInfoLte")) {
                    CellInfoLte cellInfoLte = (CellInfoLte) info;
                    CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
                    CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                    /*bean.setSignalstrength(cellSignalStrengthLte.getDbm() + "");
                    bean.setCell_id(cellIdentityLte.getCi() + "");
                    bean.setLac(cellIdentityLte.getTac() + "");
                    bean.setMcc(cellIdentityLte.getMcc() + "");
                    bean.setMnc(cellIdentityLte.getMnc() + "");*/
                    int mnc = cellIdentityLte.getMnc();
                    return  mnc < 10 ?
                            cellIdentityLte.getMcc() + ",0" + mnc + "," + cellIdentityLte.getTac():
                            cellIdentityLte.getMcc() + "," +  cellIdentityLte.getMnc() + "," + cellIdentityLte.getTac();

                } else if (info.toString().contains("CellInfoWcdma")) {
                    CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) info;
                    CellIdentityWcdma cellIdentity = cellInfoWcdma.getCellIdentity();
                    CellSignalStrengthWcdma cellSignalStrength = cellInfoWcdma.getCellSignalStrength();
                    /*bean.setCell_id(cellIdentity.getCid() + "");
                    bean.setLac(cellIdentity.getLac() + "");
                    bean.setMcc(cellIdentity.getMcc() + "");
                    bean.setMnc(cellIdentity.getMnc() + "");
                    bean.setSignalstrength(cellSignalStrength.getDbm() + "");*/
                    int mnc = cellIdentity.getMnc();
                    return mnc < 10 ?
                            cellIdentity.getMcc() + ",0" + mnc + "," + cellIdentity.getLac() :
                            cellIdentity.getMcc() + "," + mnc + "," + cellIdentity.getLac();
                } else if (info.toString().contains("CellInfoGsm")) {
                    CellInfoGsm cellInfoGsm = (CellInfoGsm) info;
                    CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
                    CellSignalStrengthGsm cellSignalStrengthGsm = cellInfoGsm.getCellSignalStrength();
                    /*bean.setSignalstrength(cellSignalStrengthGsm.getDbm() + "");
                    bean.setCell_id(cellIdentityGsm.getCid() + "");
                    bean.setLac(cellIdentityGsm.getLac() + "");
                    bean.setMcc(cellIdentityGsm.getMcc() + "");
                    bean.setMnc(cellIdentityGsm.getMnc() + "");*/
                    int mnc = cellIdentityGsm.getMnc();
                    return mnc < 10 ?
                            cellIdentityGsm.getMcc() + ",0" + mnc + "," + cellIdentityGsm.getLac():
                            cellIdentityGsm.getMcc() + "," + mnc + "," + cellIdentityGsm.getLac();
                } else if (info.toString().contains("CellInfoCdma")) {
                    CellInfoCdma cellInfoCdma = (CellInfoCdma) info;
                    CellIdentityCdma cellIdentityCdma = cellInfoCdma.getCellIdentity();
                    CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                    /*bean.setCell_id(cellIdentityCdma.getBasestationId() + "");
                    bean.setSignalstrength(cellSignalStrengthCdma.getCdmaDbm() + "");
                    bean.setLac("0");
                    bean.setMcc("0");
                    bean.setMnc("0");*/
                    return "0,0,0";
                }

            }
        }
        return "0,0,0";
    }

}
