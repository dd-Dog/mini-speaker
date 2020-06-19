package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.PersistWhite;
import com.flyscale.alertor.helper.AppActionHelper;
import com.flyscale.alertor.helper.PhoneUtil;
import com.flyscale.alertor.netty.AlarmHelper;
import com.flyscale.alertor.netty.CallAlarmHelper;

/**
 * @author 高鹤泉
 * @TIME 2020/6/18 11:28
 * @DESCRIPTION 按键广播
 */
public class KeyReceiver extends BroadcastReceiver{

    String TAG = "KeyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "onReceive: " + action);
        if(TextUtils.equals(action,"flyscale.privkey.ALARM.down")){
//            PhoneUtil.call(BaseApplication.sContext,"15902227963");
            alarmOrReceive();
        }else if(TextUtils.equals(action,"flyscale.privkey.EMERGENCY.down")){
            //110报警
            alarm110();
        }else if(TextUtils.equals(action,"flyscale.privkey.EMERGENCY.up")){

        }
    }

    /**
     * 110报警
     */
    public void alarm110(){
        if(CallAlarmHelper.getInstance().isAlarming()){
            CallAlarmHelper.getInstance().destroy(false,false,false);
        }else {
            CallAlarmHelper.getInstance().polling(null,true);
        }

    }


    /**
     * 报警时 按下报警键报警 再次按下报警键挂机
     * 接警时 按下报警键接听
     */
    public void alarmOrReceive(){
        if(AppActionHelper.isFastClick()){
            return;
        }
        //正在响铃  并且来电是接警电话
        //接警
        if(CallPhoneReceiver.isRinging() && PersistWhite.isContains(CallPhoneReceiver.getReceiveNum())){
            PhoneUtil.answerCall(BaseApplication.sContext);
            Log.i(TAG, "alarmOrReceive: 接警成功");
        }else {
            //报警
            if(CallAlarmHelper.getInstance().isAlarming()){
                CallAlarmHelper.getInstance().destroy(false,false,false);
                Log.i(TAG, "alarmOrReceive: 取消报警");
            }else {
                Log.i(TAG, "alarmOrReceive: 开始报警");
                AlarmHelper.getInstance().polling(new AlarmHelper.onAlarmFailListener() {
                    @Override
                    public void onAlarmFail() {
                        CallAlarmHelper.getInstance().polling(null,false);
                    }
                });
            }
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
