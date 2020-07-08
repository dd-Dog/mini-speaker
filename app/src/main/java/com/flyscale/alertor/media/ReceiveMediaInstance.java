package com.flyscale.alertor.media;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.flyscale.alertor.helper.SoundPoolHelper;
import com.flyscale.alertor.netty.AlarmHelper;

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
    public void finish(){
        destroyMedia();
    }

    public boolean isPlay(){
        if(mMediaPlayer != null){
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    /**
     * 播放语音
     */
    public void play(File file, final int playCount){
        SoundPoolHelper.getInstance().stopAudio();
        if(!file.exists()){
//            AlarmHelper.getInstance().alarmFinish();
            Log.i(TAG, "play: 文件不存在");
            return;
        }
        //播放语音之前 关闭声光报警
        AlarmHelper.getInstance().alarmFinish();
        mPlayCount = playCount;
        if(mMediaPlayer == null){
            mMediaPlayer = new MediaPlayer();
        }
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(file.getAbsolutePath());
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 通过异步的方式装载媒体资源
            mMediaPlayer.prepareAsync();
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
                        //语音报警结束打开声光报警
                        AlarmHelper.getInstance().alarmStart();
                        destroyMedia();
                    }
                    Log.i(TAG, "onCompletion: mPlayCount = " + mPlayCount);
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.i(TAG, "onError: " + what + " --- " + extra);
                    //onError返回值返回false会触发onCompletion，所以返回false，一般意味着会退出播放。
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            //语音报警结束打开声光报警
            AlarmHelper.getInstance().alarmStart();
        }
    }


    public void destroyMedia(){
        if(mMediaPlayer != null){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

}
