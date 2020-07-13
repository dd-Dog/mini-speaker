package com.flyscale.alertor.alarmManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.receivers.AlarmLedReceiver;
import com.flyscale.alertor.receivers.BRConstant;

/**
 * @author 高鹤泉
 * @TIME 2020/7/10 17:44
 * @DESCRIPTION 暂无
 */
@Deprecated
public class KeyReceiver2 extends BroadcastReceiver {
    String TAG = "KeyReceiver2";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "onReceive: action --- " + action );
        if(TextUtils.equals(action,"flyscale.privkey.ALARM.down")){
            //按下报警键
            AlarmManager.pressAlarmKey();
        }else if(TextUtils.equals(action,"flyscale.privkey.EMERGENCY.down")){
            AlarmManager.press110Key();
        }else if(TextUtils.equals(action,BRConstant.ACTION_ALARM_LED_STATUS)){
            //报警灯常亮常闭
            AlarmLedReceiver.sendRepeatAlarmBroadcastBySwitch();
        }
    }



    public void register(){
        IntentFilter filter = new IntentFilter();
        //报警键
        filter.addAction("flyscale.privkey.ALARM.down");
        filter.addAction("flyscale.privkey.ALARM.long");
        filter.addAction("flyscale.privkey.ALARM.up");
        //110报警键
        filter.addAction("flyscale.privkey.EMERGENCY.down");
        filter.addAction("flyscale.privkey.EMERGENCY.long");
        filter.addAction("flyscale.privkey.EMERGENCY.up");
        //外接警号键
        filter.addAction("flyscale.privkey.EXTRA_SPEAKER.down");
        filter.addAction("flyscale.privkey.EXTRA_SPEAKER.long");
        filter.addAction("flyscale.privkey.EXTRA_SPEAKER.up");
        //静默报警开关键
        filter.addAction("flyscale.privkey.MUTE.down");
        filter.addAction("flyscale.privkey.MUTE.long");
        filter.addAction("flyscale.privkey.MUTE.up");
        //报警灯开关
        filter.addAction(BRConstant.ACTION_ALARM_LED_STATUS);
        BaseApplication.sContext.registerReceiver(this,filter);
    }

    public void unRegister(){
        BaseApplication.sContext.unregisterReceiver(this);
    }
}
