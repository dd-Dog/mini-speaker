package com.flyscale.alertor.alarmManager;

import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.NetHelper;
import com.flyscale.alertor.helper.PhoneUtil;
import com.flyscale.alertor.helper.UserActionHelper;
import com.flyscale.alertor.led.LedInstance;
import com.flyscale.alertor.netty.NettyHelper;

/**
 * @author 高鹤泉
 * @TIME 2020/7/8 13:53
 * @DESCRIPTION 暂无
 */
@Deprecated
public class AlarmManager {

    static String TAG = "AlarmManager";

    /**
     * 报警
     */
    public static void pollingAlarm(int type, boolean is110){
        if(isIpAlarmFirst()){
            //ip优先
            if(isNetConnect() && isServiceConnect()){
                //网络和服务器连接正常
                IpAlarmInstance.getInstance().polling(type);
            }else if(isNetConnect() && !isServiceConnect()){
                //网络正常 服务器连接不正常
                CallAlarmInstance.getInstance().polling(is110);
            }
        }else {
            //语音优先
            CallAlarmInstance.getInstance().polling(is110);
        }
    }

    /**
     * 按下报警键 有可能报警 也有可能接警
     */
    public static void pressAlarmKey(){
        if(UserActionHelper.isFastClick()){
            return;
        }
        //来电
        if(CallPhoneReceiver2.sPhoneState == CallPhoneReceiver2.STATE_RECEIVE){
            PhoneUtil.answerCall(BaseApplication.sContext);
        }else {//去电或者闲置
            int ipStatus = IpAlarmInstance.getInstance().getStatus();
            int callStatus = CallAlarmInstance.getInstance().getStatus();
            if(ipStatus == IpAlarmInstance.STATUS_ALARMING || ipStatus == IpAlarmInstance.STATUS_ALARM_SUCCESS){
                //1.正在ip报警
                Log.i(TAG, "pressAlarmKey: 正在ip报警");
                IpAlarmInstance.getInstance().setStatus(IpAlarmInstance.STATUS_ALARM_FINISH);
            }else if(callStatus == CallAlarmInstance.STATUS_ALARMING || callStatus == CallAlarmInstance.STATUS_ALARM_SUCCESS){
                //2.正在语音报警
                Log.i(TAG, "pressAlarmKey: 正在语音报警");
                CallAlarmInstance.getInstance().setStatus(CallAlarmInstance.STATUS_ALARM_FINISH);
            }else if(AlarmMediaPlayer.getInstance().isPlaySomeone()){
                Log.i(TAG, "pressAlarmKey: loop -- success -- receive 其中一个正在播放");
                AlarmMediaPlayer.getInstance().stopAll();
                finishAlarmBlink();
            }else {
                pollingAlarm(BaseData.TYPE_ALARM_U,false);
            }
        }
    }

    /**
     * 按下110按键 有可能报警
     */
    public static void press110Key(){
        if(UserActionHelper.isFastClick()){
            return;
        }
        int callStatus = CallAlarmInstance.getInstance().getStatus();
        if(callStatus == CallAlarmInstance.STATUS_ALARMING || callStatus == CallAlarmInstance.STATUS_ALARM_SUCCESS){
            CallAlarmInstance.getInstance().setStatus(CallAlarmInstance.STATUS_ALARM_FINISH);
        }else if(AlarmMediaPlayer.getInstance().isPlaySomeone()){
            AlarmMediaPlayer.getInstance().stopAll();
            CallAlarmInstance.getInstance().polling(true);
        }else {
            CallAlarmInstance.getInstance().polling(true);
        }
    }

    /**
     * 结束上一次报警或者接警
     */
    public static void finishLastAlarmOrReceive(){
        int ipStatus = IpAlarmInstance.getInstance().getStatus();
        int callStatus = CallAlarmInstance.getInstance().getStatus();
        if(ipStatus == IpAlarmInstance.STATUS_ALARMING || ipStatus == IpAlarmInstance.STATUS_ALARM_SUCCESS){
            IpAlarmInstance.getInstance().setStatus(IpAlarmInstance.STATUS_ALARM_FINISH);
        }
        if(callStatus == CallAlarmInstance.STATUS_ALARMING || callStatus == CallAlarmInstance.STATUS_ALARM_SUCCESS){
            CallAlarmInstance.getInstance().setStatus(CallAlarmInstance.STATUS_ALARM_FINISH);
        }
        if(AlarmMediaPlayer.getInstance().isPlaySomeone()){
            AlarmMediaPlayer.getInstance().stopAll();
        }
        if(PhoneUtil.isOffhook(BaseApplication.sContext)){
            PhoneUtil.endCall(BaseApplication.sContext);
        }
    }

    /**
     * 开始声光警报
     * @param isReceive
     */
    public static void startAlarmBlink(boolean isReceive){
        boolean isMute = BaseApplication.sFlyscaleManager.getMuteState().equals("1");
        Log.i(TAG, "startAlarmBlink: isMute(静默) ---> " + isMute);
        if(isReceive){
            isMute = false;
        }
        if(isMute){
            return;
        }
        if(!isSoundAlarming()){
            AlarmMediaPlayer.getInstance().playLoopAlarm();
        }
        if(!LedInstance.getInstance().isBlinkAlarmFlag()){
            LedInstance.getInstance().blinkAlarmLed();
        }
    }

    /**
     * 关闭声光警报
     */
    public static void finishAlarmBlink(){
        AlarmMediaPlayer.getInstance().stopLoopAlarm();
        boolean alarmLedOn = LedInstance.getInstance().isAlarmOnStatus();
        Log.i(TAG, "finishAlarmBlink: 报警灯默认的开关状态 ---> " + alarmLedOn);
        if(alarmLedOn){
            LedInstance.getInstance().cancelBlinkShowAlarmLed();
        }else {
            LedInstance.getInstance().cancelBlinkOffAlarmLed();
        }
    }
    /**
     * 网络连接
     * @return
     */
    public static boolean isNetConnect(){
        //报警时，如果网络没有连通，要提示“网络连接失败”。
        if(!NetHelper.isNetworkConnected(BaseApplication.sContext)){
            MediaHelper.play(MediaHelper.NET_CONNECT_FAIL,true);
            return false;
        }
        return true;
    }

    /**
     * 服务器连接
     * @return
     */
    public static boolean isServiceConnect(){
        //报警时，如果没有连接到服务器，要提示“连接服务器失败”。
        if(!NettyHelper.getInstance().isConnect()){
            MediaHelper.play(MediaHelper.SERVER_CONNECT_FAIL,true);
            return false;
        }
        return true;
    }


    /**
     * 是否正在声音报警
     * @return
     */
    public static boolean isSoundAlarming(){
        return AlarmMediaPlayer.getInstance().isPlayLoopAlarm();
    }

    /**
     * 是否正在闪灯报警
     * @return
     */
    public static boolean isLightAlarming(){
        return LedInstance.getInstance().isBlinkAlarmFlag();
    }

    /**
     * 是否IP报警优先
     * @return
     */
    public static boolean isIpAlarmFirst(){
        return PersistConfig.findConfig().isIpAlarmFirst();
    }
}
