package android.flyscale;

/**
 * Created by bian on 2019/5/17.
 */


import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import java.util.Arrays;

public class FlyscaleManager {

    public static final String FLYSCALE_SERVICE = "flyscale";

    public static final int PREFERRED_NETWORK_MODE_AUTO = 0;

    public static final int PREFERRED_NETWORK_MODE_2G_ONLY = 4;
    public static final int PREFERRED_NETWORK_MODE_3G_2G = 1;
    public static final int PREFERRED_NETWORK_MODE_3G_ONLY = 3;
    public static final int PREFERRED_NETWORK_MODE_4G_2G = 5;
    public static final int PREFERRED_NETWORK_MODE_4G_3G_2G = 0;
    public static final int PREFERRED_NETWORK_MODE_4G_ONLY = 2;
    public static final int PREFERRED_NETWORK_MODE_UNKNOWN = -1;

    private static final int LIGHT_BYTE_BATTERY = 0;//LIGHT_ID_BATTERY = 3
    private static final int LIGHT_BYTE_NOTIFICATIONS = 1;//LIGHT_ID_NOTIFICATIONS = 4
    private static final int LIGHT_BYTE_ATTENTION = 2;//LIGHT_ID_ATTENTION = 5
    public static final int LED_FLASHLIGHT = 3;


    public static final int CHARGE_LED = 11;//充电指示灯
    public static final int SIGNAL_LED = 12;//信号指示灯
    public static final int STATE_LED = 13;//状态指示灯
    public static final int ALARM_LED = 14;//报警灯

    private static final String TAG = "FlyscaleManager";


    private Context mContext;
    private IFlyscaleManager mService;

    public FlyscaleManager(Context context, IFlyscaleManager flyscaleManager) {
        this.mContext = context;
        this.mService = flyscaleManager;
    }


    /**
     * 获取可选的网络模式
     * @return
     */
    public int[] getAllPreferredNetworkModes() {
        return new int[]{
                PREFERRED_NETWORK_MODE_AUTO,
                PREFERRED_NETWORK_MODE_4G_2G,
                PREFERRED_NETWORK_MODE_4G_ONLY,
                PREFERRED_NETWORK_MODE_2G_ONLY};
    }

    /**
     * 设置背光时间
     *
     * @param timeOut
     */
    public void setScreenOffTimeOut(int timeOut) {
        Log.i(TAG, "setScreenOffTimeOut,timeOut=" + timeOut);
        try {
            this.mService.setScreenOffTimeOut(timeOut);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取背光灯时间
     * @return
     */
    public int getScreenOffTimeOut() {
        Log.i(TAG, "getScreenOffTimeOut");
        try {
            return this.mService.getScreenOffTimeOut();
        } catch (RemoteException localRemoteException) {
            localRemoteException.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取键盘背光灯超时时间
     *
     * @return
     */
    public int getButtonLightOffTimeOut() {
        Log.i(TAG, "getButtonLightOffTimeOut");
        try {
            return mService.getButtonLightOffTimeOut();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return Integer.MIN_VALUE;
    }


    /**
     * 按键音是否开启
     * @return
     */
    public boolean isButtonVoiceEnable() {
        Log.i(TAG, "isButtonVoiceEnable");
        try {
            return this.mService.isButtonVoiceEnable();
        } catch (RemoteException remoteException) {
            remoteException.printStackTrace();
        }
        return false;
    }

    /**
     * 设置按键音是否开启
     * @param enable
     */
    public void setButtonVoice(boolean enable) {
        Log.i(TAG, "setButtonVoice,enable=" + enable);
        try {
            this.mService.setButtonVoice(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * TTS是否开启
     * @return
     */
    public boolean isTTSEnable() {
        Log.i(TAG, "isTTSEnable");
        try {
            return this.mService.isTTSEnable();
        } catch (RemoteException localRemoteException) {
            localRemoteException.printStackTrace();
        }
        return false;
    }

    /**
     * 设置TTS
     * @param enable
     */
    public void setTTSEnable(boolean enable) {
        Log.i(TAG, "setTTSEnable,enable=" + enable);
        try {
            this.mService.setTTSEnable(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前网络模式
     *
     * @return
     */
    public int getNetworkMode() {
        Log.i(TAG, "getNetworkMode");
        try {
            return mService.getNetworkMode(0);//phoneId=0
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return PREFERRED_NETWORK_MODE_4G_2G;
    }


    /**
     * 进入休眠
     */
    public void goToSleep() {
        Log.i(TAG, "goToSleep");
        try {
            this.mService.goToSleep(SystemClock.uptimeMillis());
        } catch (RemoteException localRemoteException) {
            localRemoteException.printStackTrace();
        }
    }

    /**
     * adb是否开启
    * @return
     */
    public boolean isAdbEnabled() {
        Log.i(TAG, "isAdbEnabled");
        try {
            String str = this.mService.getUsbConfig();
            if (str == null) {
                return false;
            }
            return Arrays.asList(str.split(",")).contains("adb");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置ADB
     * @param enable
     */
    public void setAdbEnabled(boolean enable) {
        Log.i(TAG, "setAdbEnabled,enable=" + enable);
        try {
            mService.setUsbConfig(enable ? "adb" : "none");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 是否亮屏
     * @return
     */
    public boolean isScreenOn() {
        Log.i(TAG, "isScreenOn");
        try {
            return this.mService.isScreenOn();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 唤醒屏幕
     */
    public void wakeUp() {
        Log.i(TAG, "wakeUp");
        try {
            this.mService.wakeUp(SystemClock.uptimeMillis());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 锁屏
     * @param bundle
     */
    public void lockNow(Bundle bundle) {
        Log.i(TAG, "lockNow");
        try {
            this.mService.lockNow(bundle);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重启
     * @param reason
     */
    public void reboot(String reason) {
        Log.i(TAG, "reboot,reason=" + reason);
        try {
            this.mService.reboot(false, reason, true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置键盘背光灯时间
     * @param timeOut
     */
    public void setButtonLightOffTimeOut(int timeOut) {
        Log.i(TAG, "setButtonLightOffTimeOut,timeOut=" + timeOut);
        try {
            this.mService.setButtonLightOffTimeOut(timeOut);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取LED灯颜色
     * @param lightByte
     * @return
     */
    public String getLightColor(int lightByte){
        Log.i(TAG, "getLightColor,lightByte=" + lightByte);
        try {
            return this.mService.getLightColor(lightByte);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "-1";
    }
    /**
     * 设置LED灯颜色，针对双色灯
     * @param lightByte 指定LED灯
     * @param color 亮度值
     */
    public void setLightColor(int lightByte, int color) {
        Log.i(TAG, "setLightColor,lightByte=" + lightByte + ",color=" + color);
        try {
            this.mService.setLightColor(lightByte, color);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置LED灯闪烁 三色灯调用
     * @param color 颜色
     * @param onMS
     * @param offMS
     */
    public void setLightFlashing(int color, int onMS, int offMS) {
        Log.i(TAG, "setLightFlashing");
        setLightFlashing(LIGHT_BYTE_NOTIFICATIONS, color, onMS, offMS);
    }

    /**
     * 设置LED灯闪烁 双色灯调用
     * @param lightByte 指定LED灯
     * @param color 亮度值
     * @param onMS
     * @param offMS
     */
    public void setLightFlashing(int lightByte, int color, int onMS, int offMS) {
        Log.i(TAG, "setLightFlashing,lightByte=" + lightByte);
        try {
            this.mService.setLightFlashing(lightByte, color, onMS, offMS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getMuteState(){
        Log.i(TAG, "getMuteState");
        try {
            return this.mService.getMuteState();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 手电筒是否开启
     * @return
     */
    public int getFlashlightEnabled() {
        Log.i(TAG, "getFlashlightEnabled");
        try {
            return this.mService.getFlashlightBrightness();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 开关手电筒
     * @param brightness
     */
    public void setFlashlightEnabled(int brightness) {
        Log.i(TAG, "setFlashlightEnabled,brightness=" + brightness);
        try {
            this.mService.setFlashlightEnabled(brightness);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置网络模式
     * @param networkMode
     */
    public void setNetworkMode2(int networkMode) {
        Log.i(TAG, "setNetworkMode,networkMode=" + networkMode);
        try {
            this.mService.setNetworkMode2(networkMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置网络模式
     * @param networkMode
     */
    @Deprecated
    public void setNetworkMode(int networkMode) {
        Log.i(TAG, "setNetworkMode2,networkMode=" + networkMode);
        try {
            this.mService.setNetworkMode(0, networkMode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 系统静音
     * @param mute
     */
    public void setSystemMuted(boolean mute) {
        Log.i(TAG, "setSystemMuted,mute=" + mute);
        try {
            this.mService.setSystemMuted(mute);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关机
     */
    public void shutdown() {
        Log.i(TAG, "shutdown");
        try {
            this.mService.shutdown(false, true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void turnOffLight(int lightByte) {
        Log.i(TAG, "turnOffLight,lightByte=" + lightByte);
        try {
            this.mService.turnOffLight(lightByte);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setBrightness(int lightByte, int brightness) {
        Log.i(TAG, "setBrightness,lightByte=" + lightByte + ",brightness=" + brightness);
        try {
            this.mService.setBrightness(lightByte, brightness);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static boolean mLedLocked = false;

    public void setLedLocked(boolean locked) {
        mLedLocked = locked;
    }

    /**
     * 获取首选SIM卡ID
     * @return
     */
    public int getPrimaryCard() {
        try {
            return mService.getPrimaryCard();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 设置首选SIM卡
     * @param phoneId
     */
    public void setPrimaryCard(int phoneId) {
        try {
            mService.setPrimaryCard(phoneId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取激活的SIM卡信息
     * @return
     */
    public String getActiveSimInfos() {
        try {
            return mService.getActiveSimInfos();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置数据网络开关
     * @param phoneId
     * @param enabled
     */
    public void setMobileDataEnabledByPhoneId(int phoneId, boolean enabled) {
        try {
            mService.setMobileDataEnabledByPhoneId(phoneId, enabled);
        } catch (RemoteException e) {
        }
    }

    /**
     * 获取数据网络开关
     * @param phoneId
     * @return
     */
    public boolean getMobileDataEnabledByPhoneId(int phoneId) {
        try {
            return mService.getMobileDataEnabledByPhoneId(phoneId);
        } catch (RemoteException e) {
            return true;
        }
    }

    /**
     * 设置默认SIM卡的数据开关
     * @param enabled
     */
    public void setMobileDataEnabled(boolean enabled) {
        try {
            mService.setMobileDataEnabled(enabled);
        } catch (RemoteException e) {
        }
    }

    /**
     * 获取默认SIM卡的数据开关
     * @return
     */
    public boolean getMobileDataEnabled() {
        try {
            return mService.getMobileDataEnabled();
        } catch (RemoteException e) {
            return true;
        }
    }

    /**
     * 获取SIM卡个数
     * @return
     */
    public int getPhoneCount() {
        try {
            return mService.getPhoneCount();
        } catch (RemoteException e) {
            return 1;
        }
    }

    /**
     * 是否支持双卡
     * @return
     */
    public boolean isMultiSim() {
        try {
            return mService.isMultiSim();
        } catch (RemoteException e) {
            return false;
        }
    }

    /**
     * 获取默认SIM卡ID
     * @return
     */
    public int getDefaultPhoneId() {
        try {
            return mService.getDefaultPhoneId();
        } catch (RemoteException e) {
            return 0;
        }
    }

    public void createHotspot(String ssid, String passwd, int safeMode){
        try {
            mService.createHotspot(ssid, passwd, safeMode);
        } catch (RemoteException e) {
        }
    }

    public void closeHotSpot(){
        try {
            mService.closeHotSpot();
        } catch (RemoteException e) {
        }
    }

    public int getHotspotState(){
        try {
            return mService.getHotspotState();
        } catch (RemoteException e) {
            return -1;
        }
    }

}

