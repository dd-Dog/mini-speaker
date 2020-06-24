package com.flyscale.alertor.receivers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.helper.InternetUtil;
import com.flyscale.alertor.led.LedInstance;

/**
 * @author 高鹤泉
 * @TIME 2020/6/18 9:29
 * @DESCRIPTION 通讯状态
 */
public class TelephonyStateReceiver {

    String TAG = "TelephonyStateReceiver";

    TelephonyManager mTelephonyManager;
    MyPhoneStateListener mMyPhoneStateListener;
    private Context mContext;
    private int signalLevel = 0;
    private boolean inService = false;
    private boolean dataConnected = false;

    public TelephonyStateReceiver(Context context) {
        mTelephonyManager = (TelephonyManager) BaseApplication.sContext.getSystemService(Context.TELEPHONY_SERVICE);
        mMyPhoneStateListener = new MyPhoneStateListener();
        mContext = context;
    }

    /**
     * 监听信号强度
     */
    public void listenStrengths() {
        mTelephonyManager.listen(mMyPhoneStateListener,
                PhoneStateListener.LISTEN_SIGNAL_STRENGTHS |
                        PhoneStateListener.LISTEN_SERVICE_STATE |
                        PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
    }

    /**
     * 取消监听
     */
    public void destroy() {
        mTelephonyManager.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    private class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);
            Log.i(TAG, "onServiceStateChanged: serviceState=" + serviceState.getState());
            switch (serviceState.getState()) {
                case ServiceState.STATE_OUT_OF_SERVICE:
                case ServiceState.STATE_POWER_OFF:
                case ServiceState.STATE_EMERGENCY_ONLY:
                    inService = false;
                    break;
                case ServiceState.STATE_IN_SERVICE:
                    inService = true;
                    break;
            }
            updateSignalState();
        }

        @Override
        public void onDataConnectionStateChanged(int state, int networkType) {
            super.onDataConnectionStateChanged(state, networkType);
            switch (state) {
                case TelephonyManager.DATA_DISCONNECTED:
                case TelephonyManager.DATA_SUSPENDED:
                case TelephonyManager.DATA_CONNECTING:
                    dataConnected = false;
                    break;
                case TelephonyManager.DATA_CONNECTED:
                    dataConnected = true;
                    break;
            }
            updateSignalState();
        }

        @SuppressLint("DiscouragedPrivateApi")
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            Log.i(TAG, "onSignalStrengthsChanged: " + signalStrength.toString());

            String getLevelByType = null;
            int networkType = InternetUtil.getNetworkState(mContext);
            Log.i(TAG, "onSignalStrengthsChanged: network type=" + networkType);
            switch (networkType) {
                case InternetUtil.NETWORK_4G:
                    getLevelByType = "getLteLevel";
                    break;
                case InternetUtil.NETWORK_2G:
                case InternetUtil.NETWORK_3G:
                case InternetUtil.NETWORK_MOBILE:
                    getLevelByType = "getLevel";
                    break;
                case InternetUtil.NETWORK_NONE:
                    Log.w(TAG, "onSignalStrengthsChanged: 当前无网络");
                    setNoSignal();
                    return;
                default:
                    Log.e(TAG, "onSignalStrengthsChanged: 不支持的网络类型！");
                    setNoSignal();
                    return;
            }
            try {
                signalLevel = (Integer) signalStrength.getClass().getDeclaredMethod(getLevelByType).invoke(signalStrength);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(TAG, "onSignalStrengthsChanged: signalLevel=" + signalLevel);
            updateSignalState();

            /*int dbm = signalStrength.getCdmaDbm();
            int asu = signalStrength.getGsmSignalStrength();
            boolean isGsm = signalStrength.isGsm();
            //       int asu = getGsmSignalStrength();
            //        if (asu <= 2 || asu == 99) level = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
            //        else if (asu >= 12) level = SIGNAL_STRENGTH_GREAT;
            //        else if (asu >= 8)  level = SIGNAL_STRENGTH_GOOD;
            //        else if (asu >= 5)  level = SIGNAL_STRENGTH_MODERATE;
            //        else level = SIGNAL_STRENGTH_POOR;
            if (isGsm) {
                if (asu <= 2 || asu == 99) {
                    //没有信号
                    LedInstance.getInstance().cancelBlinkOffSignalLed();
                } else if (asu >= 8) {
                    //信号好
                    LedInstance.getInstance().cancelBlinkShowSignalLed();
                } else {
                    //信号差
                    LedInstance.getInstance().blinkSignalLed();
                }
            } else {
                //        if (cdmaDbm >= -75) levelDbm = SIGNAL_STRENGTH_GREAT;
                //        else if (cdmaDbm >= -85) levelDbm = SIGNAL_STRENGTH_GOOD;
                //        else if (cdmaDbm >= -95) levelDbm = SIGNAL_STRENGTH_MODERATE;
                //        else if (cdmaDbm >= -100) levelDbm = SIGNAL_STRENGTH_POOR;
                //        else levelDbm = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
                if (dbm >= -85) {
                    //信号好
                    LedInstance.getInstance().cancelBlinkShowSignalLed();
                } else if (dbm >= -100) {
                    //信号差
                    LedInstance.getInstance().blinkSignalLed();
                } else {
                    //没信号
                    LedInstance.getInstance().cancelBlinkOffSignalLed();
                }
            }*/
        }
    }

    private void updateSignalState() {
        Log.i(TAG, "updateSignalState: inService=" + inService + ",dataConnected=" + dataConnected + ",signalLevel=" + signalLevel);
        if (inService && dataConnected) {
            if (signalLevel >= 1) {
                setNormal();
            } else {
                setPoor();
            }
        } else {
            setNoSignal();
        }
    }

    private void setNormal() {
        LedInstance.getInstance().cancelBlinkShowSignalLed();
    }

    private void setPoor() {
        LedInstance.getInstance().blinkSignalLed();
    }

    private void setNoSignal() {
        LedInstance.getInstance().cancelBlinkOffSignalLed();
    }
}

