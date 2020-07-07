package com.flyscale.alertor.netty;

import android.app.AlarmManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.flyscale.alertor.BuildConfig;
import com.flyscale.alertor.R;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.up.UAlarm;
import com.flyscale.alertor.data.up.UDoorAlarm;
import com.flyscale.alertor.data.up.UGasAlarm;
import com.flyscale.alertor.data.up.USmokeAlarm;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.NetHelper;
import com.flyscale.alertor.helper.SoundPoolHelper;
import com.flyscale.alertor.led.LedInstance;
import com.flyscale.alertor.media.AlarmMediaInstance;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 高鹤泉
 * @TIME 2020/6/15 15:50
 * @DESCRIPTION ip报警
 */
public class AlarmHelper {


    private static final AlarmHelper ourInstance = new AlarmHelper();
    Timer mTimer;
    AtomicBoolean mAlarmResult = new AtomicBoolean(false);
    int mSendCount = 1;
    String TAG = "AlarmHelper";


    public static AlarmHelper getInstance() {
        return ourInstance;
    }

    private AlarmHelper() {

    }

    /**
     * 设置报警结果
     * @param result
     */
    public void setAlarmResult(boolean result){
        mAlarmResult.set(result);
    }


    /**
     *  轮询 每隔1秒发送一次报警信息
     * @param type
     */
    public void polling(final onAlarmFailListener listener,final int type){
        //报警时，如果网络没有连通，要提示“网络连接失败”。
        boolean fail = false;
        if(!NetHelper.isNetworkConnected(BaseApplication.sContext)){
//            MediaHelper.play(MediaHelper.WORK_WRONG,true);
            MediaHelper.play(MediaHelper.NET_CONNECT_FAIL,true);
            fail = true;
        }
        //报警时，如果没有连接到服务器，要提示“连接服务器失败”。
        if(!NettyHelper.getInstance().isConnect()){
//            MediaHelper.play(MediaHelper.CONNECT_FAIL,true);
            MediaHelper.play(MediaHelper.SERVER_CONNECT_FAIL,true);
            fail = true;
        }
        if(fail){
            return;
        }
        Log.i(TAG, "polling: ok reday");
        if(PersistConfig.findConfig().isIpAlarmFirst()){
            Log.i(TAG, "polling: ip报警优先 type = " + type);
            //ip报警优先
            //闪灯和播放警铃
            alarmStart();
            if(mTimer != null){
                mTimer.cancel();
                mTimer.purge();
            }
            mTimer = new Timer();
            mSendCount = 1;
            mAlarmResult = new AtomicBoolean(false);
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(!mAlarmResult.get() && mSendCount <= 3){
                        if(type == BaseData.TYPE_ALARM_U){
                            NettyHelper.getInstance().send(new UAlarm(mSendCount));
                        }else if(type == BaseData.TYPE_DOOR_ALARM_U){
                            NettyHelper.getInstance().send(new UDoorAlarm(mSendCount));
                        }else if(type == BaseData.TYPE_SMOKE_ALARM_U){
                            NettyHelper.getInstance().send(new USmokeAlarm(mSendCount));
                        }else if(type == BaseData.TYPE_GAS_ALARM_U){
                            NettyHelper.getInstance().send(new UGasAlarm(mSendCount));
                        }
                        mSendCount ++;
                    }else {
                        destroy();
                        if(!mAlarmResult.get() && mSendCount> 3){
                            if(listener != null){
                                listener.onAlarmFail();
                            }
                            CallAlarmHelper.getInstance().polling(null,false);
                        }
                    }
                }
            },50,1000);
        }else {
            Log.i(TAG, "polling: 语音报警");
            CallAlarmHelper.getInstance().polling(null,false);
        }


    }

    /**
     * 轮询 每隔1秒发送一次报警信息
     */
    public void polling(final onAlarmFailListener listener){
        polling(listener,BaseData.TYPE_ALARM_U);
    }

    /**
     * ip报警结束  不代表一定成功
     */
    public void destroy(){
        alarmFinish();
        if(mAlarmResult.get()){
            MediaHelper.play(MediaHelper.ALARM_SUCCESS,true);
        }
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        mSendCount = 1;
    }



    public interface onAlarmFailListener{
        void onAlarmFail();
    }

    /**
     * 报警结束
     * 关闭闪光灯 关闭报警音
     */
    public void alarmFinish(){
        AlarmMediaInstance.getInstance().stopLoopAlarm();
        Log.i(TAG, "alarmFinish: 报警灯开关状态  -----  " + LedInstance.getInstance().isAlarmOnStatus());
        if(LedInstance.getInstance().isAlarmOnStatus()){
            LedInstance.getInstance().cancelBlinkShowAlarmLed();
        }else {
            LedInstance.getInstance().cancelBlinkOffAlarmLed();
        }
    }

    /**
     * 开始报警
     * 闪灯 响铃
     */
    public void alarmStart(){
        alarmStart(false);
    }

    public void alarmStart(boolean isReceive){
        boolean isMute = PersistConfig.findConfig().isMute();
        Log.i(TAG, "alarmStart: isMute == " + isMute);
        if(isReceive){
            //接警方 必须响警报
            isMute = false;
        }
        if(isMute){
            return;
        }
        if(!AlarmMediaInstance.getInstance().isPlaying()){
            AlarmMediaInstance.getInstance().playLoopAlarm();
            LedInstance.getInstance().blinkAlarmLed();
        }
    }
}
