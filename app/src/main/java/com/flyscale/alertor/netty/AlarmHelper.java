package com.flyscale.alertor.netty;

import android.app.AlarmManager;
import android.media.MediaPlayer;

import com.flyscale.alertor.BuildConfig;
import com.flyscale.alertor.R;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.up.UAlarm;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.SoundPoolHelper;
import com.flyscale.alertor.led.LedInstance;

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

    MediaPlayer mMediaPlayer = new MediaPlayer();

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
     * 轮询 每隔1秒发送一次报警信息
     */
    public void polling(final onAlarmFailListener listener){
        //闪灯和播放警铃
        //todo 需要静默开关控制
        LedInstance.getInstance().blinkAlarmLed();
        playLoopAlarm();
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
                    NettyHelper.getInstance().send(new UAlarm(mSendCount));
                    mSendCount ++;
                }else {
                    if(!mAlarmResult.get() && mSendCount> 3){
                        listener.onAlarmFail();
                    }
                    destroy();
                }
            }
        },50,1000);
    }

    public void destroy(){
        stopLoopAlarm();
        LedInstance.getInstance().cancelBlinkOffAlarmLed();
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        mSendCount = 1;
        mAlarmResult = new AtomicBoolean(false);
    }

    /**
     * 循环播放报警音
     */
    public void playLoopAlarm(){
        SoundPoolHelper.getInstance().stopAudio();
        mMediaPlayer = MediaPlayer.create(BaseApplication.sContext,R.raw.alarm_ringing);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
        if(BuildConfig.DEBUG){
            mMediaPlayer.setVolume(0.05f,0.05f);
        }
    }

    public void stopLoopAlarm(){
        mMediaPlayer.stop();
        SoundPoolHelper.getInstance().releaseAudio();
    }

    public interface onAlarmFailListener{
        void onAlarmFail();
    }

}
