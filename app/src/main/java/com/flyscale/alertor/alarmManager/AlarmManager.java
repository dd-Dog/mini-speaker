package com.flyscale.alertor.alarmManager;

import android.media.MediaPlayer;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.NetHelper;
import com.flyscale.alertor.led.LedInstance;
import com.flyscale.alertor.media.AlarmMediaInstance;
import com.flyscale.alertor.netty.NettyHelper;

/**
 * @author 高鹤泉
 * @TIME 2020/7/8 13:53
 * @DESCRIPTION 暂无
 */
@Deprecated
public class AlarmManager {

    static String TAG = "AlarmManager";
    public static final int STATUS_IP_ALARMING = 1;//ip正在报警
    public static final int STATUS_IP_PLAY_ALARM_SUCCESS = 2;//ip报警播放您的报警信息已发出
    public static final int STATUS_IP_ALARM_SUCCESS = 3;//ip报警成功 都结束了 算成功
    public static final int STATUS_IP_ALARM_FAIL = 4;//ip报警失败 此时会转语音报警


    /**
     * 报警
     */
    public static void pollingAlarming(){

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
        AlarmMediaPlayer.getInstance().playLoopAlarm();
        LedInstance.getInstance().blinkAlarmLed();
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
     * 网络和服务器是否正常
     * @return
     */
    public static boolean isFineNet(){
        //报警时，如果网络没有连通，要提示“网络连接失败”。
        if(!NetHelper.isNetworkConnected(BaseApplication.sContext)){
            MediaHelper.play(MediaHelper.NET_CONNECT_FAIL,true);
            return false;
        }
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
