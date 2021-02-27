package com.flyscale.alertor.helper;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

public class WifiUtil {

    public static WifiManager getWifiManager(Context context) {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public static int getWifiState(Context context) {
        int wifiState = getWifiManager(context).getWifiState();
        return wifiState;
    }

    public static boolean isWifiOpen(Context context){
        return getWifiManager(context).getWifiState() == WifiManager.WIFI_STATE_ENABLED;
    }

    public static void scan(Context context) {
        switchWifi(context, true);
        getWifiManager(context).startScan();
    }

    public static void switchWifi(Context context, boolean open) {
        getWifiManager(context).setWifiEnabled(open);
    }

    /**
     * 连接wifi
     *
     * @param targetSsid wifi的SSID
     * @param targetPsd  密码
     * @param enc        加密类型
     */
    public void connectWifi(Context context, String targetSsid, String targetPsd, String enc) {
        // 1、注意热点和密码均包含引号，此处需要需要转义引号
        String ssid = "\"" + targetSsid + "\"";
        String psd = "\"" + targetPsd + "\"";

        //2、配置wifi信息
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        switch (enc) {
            case "WEP":
                // 加密类型为WEP
                conf.wepKeys[0] = psd;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case "WPA":
                // 加密类型为WPA
                conf.preSharedKey = psd;
                break;
            case "OPEN":
                //开放网络
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        //3、链接wifi
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals(ssid)) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }

    /**
     * 忘记某一个wifi密码
     *
     * @param wifiManager
     * @param targetSsid
     */
    public static void removeWifiBySsid(WifiManager wifiManager, String targetSsid) {
//        Log.d(TAG, "try to removeWifiBySsid, targetSsid=" + targetSsid);
        List<WifiConfiguration> wifiConfigs = wifiManager.getConfiguredNetworks();

        for (WifiConfiguration wifiConfig : wifiConfigs) {
            String ssid = wifiConfig.SSID;
//            Log.d(TAG, "removeWifiBySsid ssid=" + ssid);
            if (ssid.equals(targetSsid)) {
//                Log.d(TAG, "removeWifiBySsid success, SSID = " + wifiConfig.SSID + " netId = " + String.valueOf(wifiConfig.networkId));
                wifiManager.removeNetwork(wifiConfig.networkId);
                wifiManager.saveConfiguration();
            }
        }
    }
}
