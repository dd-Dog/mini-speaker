package com.flyscale.alertor.alarmManager;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

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


    public static AlarmMediaPlayer getInstance() {
        return ourInstance;
    }

    private AlarmMediaPlayer() {
        mAudioManager = (AudioManager) BaseApplication.sContext.getSystemService(Context.AUDIO_SERVICE);
    }


    public void playLoopAlarm(){

    }

    private void stopAudio(){
        //如果在播放音乐
        if(mAudioManager.isMusicActive()){
            //AudioManager.AUDIOFOCUS_GAIN_TRANSIENT 暂时获取焦点 适用于短暂的音频
            mAudioManager.requestAudioFocus(null,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
    }

    private void releaseAudio(){

    }
}
