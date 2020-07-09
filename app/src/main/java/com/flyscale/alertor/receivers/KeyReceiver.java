package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistWhite;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.UserActionHelper;
import com.flyscale.alertor.helper.PhoneUtil;
import com.flyscale.alertor.media.ReceiveMediaInstance;
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
            alarmOrReceive();
        }else if(TextUtils.equals(action,"flyscale.privkey.EMERGENCY.down")){
            //110报警
            //测试更换ip
//            PersistConfig.saveNewIp("192.168.1.252", 1111);
//            NettyHelper.getInstance().disconnectByChangeIp("123456789");
            alarm110();
        }else if(TextUtils.equals(action,"flyscale.privkey.EMERGENCY.up")){

        }else if(TextUtils.equals(action,"flyscale.privkey.MUTE.down")){

        }else if(TextUtils.equals(action,"flyscale.privkey.MUTE.up")){

        }else if(TextUtils.equals(action,BRConstant.ACTION_ALARM_LED_STATUS)){
            //报警灯常亮常闭
            AlarmLedReceiver.sendRepeatAlarmBroadcastBySwitch();
        }
    }

    /**
     * 110报警
     */
    public void alarm110(){
        if(UserActionHelper.isFastClick()){
            return;
        }
        if(ReceiveMediaInstance.getInstance().isPlay()) {
            ReceiveMediaInstance.getInstance().finish();
            Log.i(TAG, "alarm110: ip接警正在播放 --> 取消播放");
        }else {
            if(CallAlarmHelper.getInstance().isAlarming()){
                boolean alarmResult = CallAlarmHelper.getInstance().getAlarmResult();
                CallAlarmHelper.getInstance().destroy(alarmResult,false,true,false);
            }else {
                CallAlarmHelper.getInstance().polling(null,true);
            }
        }
    }


    /**
     * 报警时 按下报警键报警 再次按下报警键挂机
     * 接警时 按下报警键接听
     */
    public void alarmOrReceive(){
        if(UserActionHelper.isFastClick()){
            return;
        }
        //正在响铃  并且来电是接警电话
        //接警
        if(CallPhoneReceiver.getCallState() == CallPhoneReceiver.INCOMING){
            //来电时候 如果接受其他号码 或者 白名单包含此号码
            if(PersistConfig.findConfig().isAcceptOtherNum()
                || PersistWhite.isContains(CallPhoneReceiver.getReceiveNum())){
                if(PhoneUtil.isOffhook(BaseApplication.sContext)){
                    PhoneUtil.endCall(BaseApplication.sContext);
                    Log.i(TAG, "alarmOrReceive: 接警中主动挂断" );
                }else {
                    PhoneUtil.answerCall(BaseApplication.sContext);
                    Log.i(TAG, "alarmOrReceive: 接警成功");
                }
            }
        }else {
            if(AlarmHelper.getInstance().isSoundLightAlarming()){
                AlarmHelper.getInstance().alarmFinish();
            }else {
                //正在播放接警信息
                if(ReceiveMediaInstance.getInstance().isPlay()){
                    ReceiveMediaInstance.getInstance().finish();
                    AlarmHelper.getInstance().alarmFinish();
                    Log.i(TAG, "alarmOrReceive: ip接警正在播放 --> 取消播放");
                }else {
                    //正在ip报警
                    if(AlarmHelper.getInstance().isAlarming()){
                        AlarmHelper.getInstance().setAlarming(false);
                        AlarmHelper.getInstance().destroy();
                        Log.i(TAG, "alarmOrReceive: ip接警正在报警 --> 然后取消");
                    }else {
                        //正在语音报警
                        if(CallAlarmHelper.getInstance().isAlarming()){
                            boolean alarmResult = CallAlarmHelper.getInstance().getAlarmResult();
                            CallAlarmHelper.getInstance().destroy(alarmResult,false,true,false);
                            Log.i(TAG, "alarmOrReceive: 语音报警正在报警 --> 取消报警");
                        }else {
                            if(MediaHelper.isPlayAlarmSuccessing){
                                AlarmHelper.getInstance().stopAlarmSuccessAndFinishAlarm();
                            }else {
                                AlarmHelper.getInstance().polling(null);
                                Log.i(TAG, "alarmOrReceive: 开始报警");
                            }
                        }
                    }
                }
            }
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
