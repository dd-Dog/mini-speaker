package com.flyscale.alertor.alarmManager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.flyscale.alertor.R;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.helper.SoundPoolHelper;
import com.flyscale.alertor.led.Constant;

import java.io.File;
import java.io.IOException;

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

    //播放次数
    int mPlayCount = 3;
    boolean isPlayLoopAlarm = false;
    boolean isPlayAlarmSuccess = false;
    boolean isPlayReceive = false;
    String TAG = "AlarmMediaPlayer";

    public static AlarmMediaPlayer getInstance() {
        return ourInstance;
    }

    private AlarmMediaPlayer() {
        mAudioManager = (AudioManager) BaseApplication.sContext.getSystemService(Context.AUDIO_SERVICE);
        mContext = BaseApplication.sContext;
    }

    /**
     * 播放接警信息
     * @param file
     * @param playCount
     */
    public void playReceive(File file,int playCount){
        stopAudio();
        if(!file.exists()){
            Log.i(TAG, "playReceive: voiceFile no exist");
            return;
        }
        //播放语音之前 关闭警报声
        stopLoopAlarm();
        isPlayReceive = true;
        mPlayCount = playCount;
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(file.getAbsolutePath());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 通过异步的方式装载媒体资源
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayCount--;
                    if(mPlayCount > 0){
                        mMediaPlayer.start();
                    }else {
                        //语音报警结束打开声警报声
                        stopReceive();
                        playLoopAlarm();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            playLoopAlarm();
            stopReceive();
        }
    }

    /**
     * 停止播放接警信息
     */
    public void stopReceive(){
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
        }
        isPlayReceive = false;
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
        isPlayAlarmSuccess = true;
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(listener != null){
                    listener.onPlayFinish();
                }
                isPlayAlarmSuccess = false;
            }
        });
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

    public boolean isPlayReceive() {
        return isPlayReceive;
    }

    /**
     * 是否正在播放其中一个
     * @return
     */
    public boolean isPlaySomeone(){
        return isPlayLoopAlarm || isPlayAlarmSuccess || isPlayReceive;
    }

    public void stopAll(){
        stopLoopAlarm();
        stopAlarmSuccess();
        stopReceive();
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

