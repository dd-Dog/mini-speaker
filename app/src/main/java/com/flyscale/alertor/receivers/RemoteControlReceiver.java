package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistWhite;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.PhoneUtil;
import com.flyscale.alertor.helper.ThreadPool;
import com.flyscale.alertor.helper.UserActionHelper;
import com.flyscale.alertor.media.ReceiveMediaInstance;
import com.flyscale.alertor.netty.AlarmHelper;
import com.flyscale.alertor.netty.CallAlarmHelper;

/**
 * @author 高鹤泉
 * @TIME 2020/6/23 15:38
 * @DESCRIPTION 暂无
 */
public class RemoteControlReceiver extends BroadcastReceiver {

    final String ACTION = "flyscale.privkey.REMOTE_CONTROL";
    String TAG = "RemoteControlReceiver";

    @Override
    public void onReceive(Context context, final Intent intent) {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if(TextUtils.equals(intent.getAction(),ACTION)){
                    String status = intent.getStringExtra("status");
                    //A: 0100
                    //B: 0010
                    //C: 0001
                    //D: 1000
                    //门磁：0011
                    //红外：0101
                    //烟感：1001
                    //气感：1011
                    //按键和传感器的8中状态
                    Log.i(TAG, "onReceive: " + status);
                    if(status.equals("0001")){
                        //布防
                        PersistConfig.saveIsArming(true);
                    }else if(status.equals("1000")){
                        //撤防
                        PersistConfig.saveIsArming(false);
                    }

                    if(TextUtils.equals(status,"0011") || TextUtils.equals(status,"0101")){
                        //如果是门磁和红外 孟工说 红外的按照门磁的报警
                        if(PersistConfig.findConfig().isArming()){
                            //如果布防
                            cancelReceive();
                            AlarmHelper.getInstance().polling(null,BaseData.TYPE_DOOR_ALARM_U);
                        }
                    }


                    if(status.equals("0100")){
                        alarmOrReceive();
                    }else if(status.equals("0010")){
                        alarm110();
                    }else if(status.equals("1001")){
                        //烟感
                        cancelReceive();
                        AlarmHelper.getInstance().polling(null,BaseData.TYPE_SMOKE_ALARM_U);
                    }else if(status.equals("1011")){
                        cancelReceive();
                        AlarmHelper.getInstance().polling(null,BaseData.TYPE_GAS_ALARM_U);
                    }
                }
            }
        });

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



    /**
     * 取消正在接警报警等状态
     */
    public void cancelReceive(){
        if(ReceiveMediaInstance.getInstance().isPlay()){
            ReceiveMediaInstance.getInstance().finish();
        }
        if(AlarmHelper.getInstance().isSoundLightAlarming()){
            AlarmHelper.getInstance().alarmFinish();
        }
        if(MediaHelper.isPlayAlarmSuccessing){
            AlarmHelper.getInstance().stopAlarmSuccessAndFinishAlarm();
        }
        if(CallAlarmHelper.getInstance().isAlarming()){
            boolean alarmResult = CallAlarmHelper.getInstance().getAlarmResult();
            CallAlarmHelper.getInstance().destroy(alarmResult,false,true,false);
        }
    }
}
