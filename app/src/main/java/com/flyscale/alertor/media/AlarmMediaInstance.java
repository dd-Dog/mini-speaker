package com.flyscale.alertor.media;

import android.media.MediaPlayer;

import com.flyscale.alertor.BuildConfig;
import com.flyscale.alertor.R;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.helper.SoundPoolHelper;

/**
 * @author 高鹤泉
 * @TIME 2020/6/18 19:20
 * @DESCRIPTION 暂无
 */
public class AlarmMediaInstance {
    private static final AlarmMediaInstance ourInstance = new AlarmMediaInstance();
    MediaPlayer mMediaPlayer = new MediaPlayer();

    public static AlarmMediaInstance getInstance() {
        return ourInstance;
    }

    private AlarmMediaInstance() {
    }

    /**
     * 循环播放报警音
     */
    public void playLoopAlarm(){
        SoundPoolHelper.getInstance().stopAudio();
        mMediaPlayer = MediaPlayer.create(BaseApplication.sContext, R.raw.alarm_ringing);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
//        if(BuildConfig.DEBUG){
//            mMediaPlayer.setVolume(0.1f,0.1f);
//        }
    }

    public void stopLoopAlarm(){
        mMediaPlayer.stop();
        SoundPoolHelper.getInstance().releaseAudio();
    }
}
