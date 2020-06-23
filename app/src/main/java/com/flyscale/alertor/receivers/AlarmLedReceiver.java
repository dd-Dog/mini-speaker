package com.flyscale.alertor.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.helper.DateHelper;
import com.flyscale.alertor.led.LedInstance;

/**
 * @author 高鹤泉
 * @TIME 2020/6/22 17:57
 * @DESCRIPTION 定时开关报警灯
 */
public class AlarmLedReceiver extends BroadcastReceiver {

    static PendingIntent mOnPendingIntent = null,mOffPendingIntent = null;

    final static String ACTION_ALARM_LED_ON = "ACTION_ALARM_LED_ON";
    final static String ACTION_ALARM_LED_OFF = "ACTION_ALARM_LED_OFF";

    static String TAG = "AlarmLedReceiver";

    public AlarmLedReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(TextUtils.equals(action,ACTION_ALARM_LED_ON)){
            LedInstance.getInstance().cancelBlinkShowAlarmLed();
            getAlarmManager().setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + DateHelper.ONE_DAY_MSEC,getOnPending());
        }else if(TextUtils.equals(action,ACTION_ALARM_LED_OFF)){
            LedInstance.getInstance().cancelBlinkOffAlarmLed();
            getAlarmManager().setExact(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + DateHelper.ONE_DAY_MSEC,getOffPending());
        }
        Log.i(TAG, "onReceive: " + action);
    }

    private static PendingIntent getOnPending(){
        if(mOnPendingIntent != null){
            return mOnPendingIntent;
        }
        Intent intentOn = new Intent(BaseApplication.sContext,AlarmLedReceiver.class);
        intentOn.setAction(ACTION_ALARM_LED_ON);
        return mOnPendingIntent = PendingIntent.getBroadcast(BaseApplication.sContext,1003,intentOn,0);
    }

    private static PendingIntent getOffPending(){
        if(mOffPendingIntent != null){
            return mOffPendingIntent;
        }
        Intent intentOff = new Intent(BaseApplication.sContext,AlarmLedReceiver.class);
        intentOff.setAction(ACTION_ALARM_LED_OFF);
        return mOffPendingIntent = PendingIntent.getBroadcast(BaseApplication.sContext,1004,intentOff,0);
    }

    public static void sendRepeatAlarmBroadcast(String onTime,String offTime){
        cancelAlarmBroadcast();
        if(onTime.equals("00:00") && offTime.equals("00:00")){
            LedInstance.getInstance().cancelBlinkShowAlarmLed();
        }else {
            String currentTime = DateHelper.longToString(DateHelper.yyyyMMdd);
            String tempOn = currentTime + onTime;
            String tempOff = currentTime + offTime;
            long on = DateHelper.stringToLong(tempOn,DateHelper.yyyyMMddHH_mm);
            long off = DateHelper.stringToLong(tempOff,DateHelper.yyyyMMddHH_mm);
            Log.i(TAG, "sendRepeatAlarmBroadcast: on = " + on);
            Log.i(TAG, "sendRepeatAlarmBroadcast: off = " + off);
            getAlarmManager().setExact(AlarmManager.RTC_WAKEUP,on,getOnPending());
            getAlarmManager().setExact(AlarmManager.RTC_WAKEUP,off,getOffPending());
        }
    }

    private static void cancelAlarmBroadcast(){
        getAlarmManager().cancel(getOnPending());
        getAlarmManager().cancel(getOffPending());
    }

    public static AlarmManager getAlarmManager(){
        return (AlarmManager) BaseApplication.sContext.getSystemService(Context.ALARM_SERVICE);
    }
}
