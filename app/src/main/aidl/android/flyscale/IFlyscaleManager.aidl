// FlyscaleService.aidl
package android.flyscale;
import android.os.Bundle;

interface IFlyscaleManager {

    String getAccountInfoFromFile();

    int getPreferredNetworkType();

    int getStringLengthFromNV();

    int getButtonLightOffTimeOut();

    int getInternalPreferredNetworkTypeForPhone(int phoneId);

    int getNetworkMode(int phoneId);

    int getPTTState();

    int getPreferredNetworkModeForPhone(int phoneId);

    int getScreenOffTimeOut();

    String getUsbConfig();

    void goToSleep(long timeout);

    boolean isAdbEnabled();

    boolean isButtonVoiceEnable();

    boolean isScreenOn();

    boolean isTTSEnable();

    void lockNow(in Bundle bundle);

    void reboot(boolean confirm, String reason, boolean wait);

    void setAdbEnabled(boolean adbEnabled);

    void setButtonLightOffTimeOut(int timeout);

    void setButtonVoice(boolean open);

    void setLightColor(int lightByte, int color);

    String getLightColor(int lightByte);

    void setLightFlashing(int lightByte, int color, int onMS, int offMS);

    void setNetworkMode2(int value);

    void setNetworkMode(int defMode, int value);

    void setScreenOffTimeOut(int timout);

    void setSystemMuted(boolean muted);

    void setTTSEnable(boolean enable);

    void setUsbConfig(String config);

    void shutdown(boolean confirm, boolean wait);

    void turnOffLight(int lightByte);

    void setBrightness(int lightByte, int brightness);

    int getFlashlightBrightness();

    void setFlashlightEnabled(int brightness);

    void wakeUp(long lightByte);

    int getPrimaryCard();

    void setPrimaryCard(int phoneId);

    String getActiveSimInfos();

    void setMobileDataEnabledByPhoneId(int phoneId, boolean enabled);

    void setMobileDataEnabled(boolean enabled);

    boolean getMobileDataEnabledByPhoneId(int phoneId);

    boolean getMobileDataEnabled();

    int getPhoneCount();

    boolean isMultiSim();

    int getDefaultPhoneId();

    String getMuteState();

    void createHotspot(String ssid, String passwd, int safeMode);

    void closeHotSpot();

    int getHotspotState();

    void setExternalAlarm(int status);

    String getExternalAlarm();

    String getAdapterState();

    String getAlarmLedState();

    int getCurrentPhoneType();
}


