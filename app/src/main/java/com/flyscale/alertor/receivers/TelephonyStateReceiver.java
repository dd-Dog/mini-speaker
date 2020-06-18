package com.flyscale.alertor.receivers;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
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
    public TelephonyStateReceiver() {
        mTelephonyManager = (TelephonyManager) BaseApplication.sContext.getSystemService(Context.TELEPHONY_SERVICE);
        mMyPhoneStateListener = new MyPhoneStateListener();
    }

    /**
     * 监听信号强度
     */
    public void listenStrengths(){
        mTelephonyManager.listen(mMyPhoneStateListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    /**
     * 取消监听
     */
    public void destroy(){
        mTelephonyManager.listen(mMyPhoneStateListener,PhoneStateListener.LISTEN_NONE);
    }

    private class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            Log.i(TAG, "onSignalStrengthsChanged: " + signalStrength.toString());
            int dbm = signalStrength.getCdmaDbm();
            int asu = signalStrength.getGsmSignalStrength();
            boolean isGsm = signalStrength.isGsm();
            //       int asu = getGsmSignalStrength();
            //        if (asu <= 2 || asu == 99) level = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
            //        else if (asu >= 12) level = SIGNAL_STRENGTH_GREAT;
            //        else if (asu >= 8)  level = SIGNAL_STRENGTH_GOOD;
            //        else if (asu >= 5)  level = SIGNAL_STRENGTH_MODERATE;
            //        else level = SIGNAL_STRENGTH_POOR;
            if(isGsm){
                if(asu <= 2 || asu == 99){
                    //没有信号
                    LedInstance.getInstance().cancelBlinkOffSignalLed();
                }else if(asu >= 8){
                    //信号好
                    LedInstance.getInstance().cancelBlinkShowSignalLed();
                }else{
                    //信号差
                    LedInstance.getInstance().blinkSignalLed();
                }
            }else {
                //        if (cdmaDbm >= -75) levelDbm = SIGNAL_STRENGTH_GREAT;
                //        else if (cdmaDbm >= -85) levelDbm = SIGNAL_STRENGTH_GOOD;
                //        else if (cdmaDbm >= -95) levelDbm = SIGNAL_STRENGTH_MODERATE;
                //        else if (cdmaDbm >= -100) levelDbm = SIGNAL_STRENGTH_POOR;
                //        else levelDbm = SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
                if(dbm >= -85){
                    //信号好
                    LedInstance.getInstance().cancelBlinkShowSignalLed();
                }else if(dbm >= -100){
                    //信号差
                    LedInstance.getInstance().blinkSignalLed();
                }else {
                    //没信号
                    LedInstance.getInstance().cancelBlinkOffSignalLed();
                }
            }
        }
    }
}
