package com.flyscale.alertor.media;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.flyscale.alertor.helper.SoundPoolHelper;

import java.io.File;
import java.io.IOException;

/**
 * @author 高鹤泉
 * @TIME 2020/6/16 14:20
 * @DESCRIPTION 播放单例 可以限制次数等
 */
public class ReceiveMediaInstance {
    String TAG = "MediaInstance";
    MediaPlayer mMediaPlayer = new MediaPlayer();
    int mPlayCount = 3;

    private static final ReceiveMediaInstance ourInstance = new ReceiveMediaInstance();

    public static ReceiveMediaInstance getInstance() {
        return ourInstance;
    }

    private ReceiveMediaInstance() {

    }

    /**
     * 暂停播放
     */
    public void stop(){
        mMediaPlayer.stop();
    }

    /**
     * 播放语音
     */
    public void play(File file, final int playCount){
        SoundPoolHelper.getInstance().stopAudio();
        mPlayCount = playCount;
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(file.getAbsolutePath());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayCount --;
                    if(mPlayCount > 0){
                        mp.start();
                    }else {
                        SoundPoolHelper.getInstance().releaseAudio();
                        mp.reset();
                    }

                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.i(TAG, "onError: " + what + " --- " + extra);
                    return true;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
