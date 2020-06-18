package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.R;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.PersistDataHelper;
import com.flyscale.alertor.netty.AlarmHelper;
import com.flyscale.alertor.netty.CallAlarmHelper;
/**
 * @author 高鹤泉
 * @TIME 2020/6/18 11:28
 * @DESCRIPTION 按键广播
 */
public class KeyReceiver extends BroadcastReceiver {

    String TAG = "KeyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "onReceive: " + action);
        if(TextUtils.equals(action,"flyscale.privkey.ALARM.down")){
            //按下报警键
            AlarmHelper.getInstance().polling(new AlarmHelper.onAlarmFailListener() {
                @Override
                public void onAlarmFail() {
                    CallAlarmHelper.getInstance().polling(PersistDataHelper.getAlarmNumber());
                }
            });
//            CallAlarmHelper.getInstance().polling("15902227963");
        }else if(TextUtils.equals(action,"flyscale.privkey.EMERGENCY.down")){
            //110报警

        }else if(TextUtils.equals(action,"flyscale.privkey.EMERGENCY.up")){

        }
    }


    public void register(KeyReceiver receiver){
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
        BaseApplication.sContext.registerReceiver(receiver,filter);
    }

    public void unRegister(KeyReceiver receiver){
        BaseApplication.sContext.unregisterReceiver(receiver);
    }
}
