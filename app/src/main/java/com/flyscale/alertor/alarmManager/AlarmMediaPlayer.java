package com.flyscale.alertor.alarmManager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.flyscale.alertor.R;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.led.Constant;

/**
 * @author 高鹤泉
 * @TIME 2020/7/9 18:44
 * @DESCRIPTION 暂无
 */
@Deprecated
public class AlarmMediaPlayer {
    private static final AlarmMediaPlayer ourInstance = new AlarmMediaPlayer();
    MediaPlayer mMediaPlayer = new MediaPlayer();
    AudioManager mAudioManager;
    Context mContext;

    boolean isPlayLoopAlarm = false;
    boolean isPlayAlarmSuccess = false;

    public static AlarmMediaPlayer getInstance() {
        return ourInstance;
    }

    private AlarmMediaPlayer() {
        mAudioManager = (AudioManager) BaseApplication.sContext.getSystemService(Context.AUDIO_SERVICE);
        mContext = BaseApplication.sContext;
    }


    /**
     * 循环播放报警声
     */
    public void playLoopAlarm(){
        stopAudio();
        stopAlarmSuccess();
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.alarm_ringing);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
        isPlayLoopAlarm = true;
        BaseApplication.sFlyscaleManager.setExternalAlarmStatus(1);
    }

    /**
     * 暂停播放循环报警声
     */
    public void stopLoopAlarm(){
        if(isPlayLoopAlarm){
            mMediaPlayer.stop();
            isPlayLoopAlarm = false;
        }
        BaseApplication.sFlyscaleManager.setExternalAlarmStatus(0);
    }

    /**
     * 播放报警成功
     */
    public void playAlarmSuccess(final OnPlayFinishListener listener){
        stopLoopAlarm();
        mMediaPlayer = MediaPlayer.create(mContext,R.raw.v8_send_alarm_success);
        mMediaPlayer.setLooping(false);
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                listener.onPlayFinish();
                isPlayAlarmSuccess = false;
            }
        });
        isPlayAlarmSuccess = true;
    }

    /**
     * 停止播放报警成功
     */
    public void stopAlarmSuccess(){
        if(isPlayAlarmSuccess){
            mMediaPlayer.stop();
            isPlayAlarmSuccess =false;
        }
    }


    public boolean isPlayLoopAlarm() {
        return isPlayLoopAlarm;
    }

    public boolean isPlayAlarmSuccess() {
        return isPlayAlarmSuccess;
    }


    /**
     * 停止其他播放 抢占焦点
     */
    private void stopAudio(){
        //如果在播放音乐
        if(mAudioManager.isMusicActive()){
            //AudioManager.AUDIOFOCUS_GAIN_TRANSIENT 暂时获取焦点 适用于短暂的音频
            mAudioManager.requestAudioFocus(null,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
    }

    public interface OnPlayFinishListener{
        void onPlayFinish();
    }
}

