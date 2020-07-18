package com.flyscale.alertor.alarmManager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.flyscale.alertor.R;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.helper.SoundPoolHelper;
import com.flyscale.alertor.helper.TimerTaskHelper;
import com.flyscale.alertor.helper.UserActionHelper;
import com.flyscale.alertor.led.Constant;

import java.io.File;
import java.io.IOException;
import java.util.TimerTask;

/**
 * @author 高鹤泉
 * @TIME 2020/7/9 18:44
 * @DESCRIPTION 暂无
 */
public class AlarmMediaPlayer {
    private static final AlarmMediaPlayer ourInstance = new AlarmMediaPlayer();
    MediaPlayer mMediaPlayer = new MediaPlayer();
    AudioManager mAudioManager;
    Context mContext;

    //播放次数
    int mPlayCount = 3;
    File mFile;
    boolean isPlayLoopAlarm = false;
    boolean isPlayAlarmSuccess = false;
    boolean isPlayReceive = false;
    boolean isWaitPlayReceive = false;//等待去播放接警信息
    String TAG = "AlarmMediaPlayer";
    TimerTaskHelper mTimerTaskHelper;

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
    public void playReceive(final File file, final int playCount){
        if(file.exists()){

            stopReceive();
            Log.i(TAG, "playReceive: 等10秒在播放");

            //文件下载成功之后再去响铃
            AlarmManager.startAlarmBlink(true);
            mFile = file;
            mPlayCount = playCount;
            isWaitPlayReceive = true;
            mTimerTaskHelper = new TimerTaskHelper(new TimerTask() {
                @Override
                public void run() {
                    playReceiveNow();
                }
            },-1);
            mTimerTaskHelper.start(9 * 1000);
        }
    }

    /**
     * 立即播放接警信息
     */
    public void playReceiveNow(){
        mTimerTaskHelper.stop();
        stopAudio();
        if(!mFile.exists()){
            Log.i(TAG, "playReceive: voiceFile no exist");
            //接警结束
            stopReceive();
            AlarmManager.finishAlarmBlink();
            return;
        }
        //播放语音之前 关闭警报声
        stopLoopAlarm();
        isPlayReceive = true;
        isWaitPlayReceive = false;
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
        }
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(mFile.getAbsolutePath());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 通过异步的方式装载媒体资源
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.i(TAG, "onPrepared: 准备播放");
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
                        //接警结束
                        stopReceive();
                        AlarmManager.finishAlarmBlink();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            //接警结束
            stopReceive();
            AlarmManager.finishAlarmBlink();
        }
    }

    /**
     * 停止播放接警信息
     */
    public void stopReceive(){
        if(mMediaPlayer != null){
            mMediaPlayer.stop();
        }
        if(mTimerTaskHelper != null){
            mTimerTaskHelper.stop();
        }
        isPlayReceive = false;
        isWaitPlayReceive = false;
    }


    /**
     * 循环播放报警声
     */
    public void playLoopAlarm(boolean isReceive){
        stopAudio();
        stopAlarmSuccess();
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.alarm_ringing);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
        isPlayLoopAlarm = true;
        if(UserActionHelper.isMute() && !isReceive){
            BaseApplication.sFlyscaleManager.setExternalAlarmStatus(0);
        }else {
            BaseApplication.sFlyscaleManager.setExternalAlarmStatus(1);
        }

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

