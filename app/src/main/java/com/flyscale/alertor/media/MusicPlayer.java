package com.flyscale.alertor.media;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.ThreadPool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MusicPlayer {
    private MediaPlayer mMediaPlayer;
    private static MusicPlayer mInstance;
    private static final String MEDIA_PATH = "/mnt/sdcard/flyscale/media/normal/";
    private static final String EMR_MEDIA_PATH = "/mnt/sdcard/flyscale/media/emr/";

    private ArrayList<String> mNormalList;
    private ArrayList<String> mEmrList;

    private ArrayList<String> mCurrentList;//当前正在播放的列表

    private Integer mLastNormalIndex = 0;
    private Integer mLastEmrIndex = 0;
    private Integer mCurrentNormalIndex = 0;
    private Integer mCurrentEmrIndex = 0;

    private PLAY_MODE mPlayMode = PLAY_MODE.LIST_LOOP;//默认列表循环播放
    private PLAY_TYPE mPlayType = PLAY_TYPE.NORMAL;//默认播放常规音频

    private MusicPlayer() {
        mMediaPlayer = new MediaPlayer();
        //获取曲目列表
        mNormalList = new ArrayList<>();
        mEmrList = new ArrayList<>();
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                File normal = new File(MEDIA_PATH);
                if (normal.exists() && normal.isDirectory()) {
                    File[] files = normal.listFiles();
                    for (File file : files) {
                        if (file.getName().endsWith(".mp3") || file.getName().endsWith(".MP3") ||
                                file.getName().endsWith(".amr") || file.getName().endsWith(".AMR")) {
                            mNormalList.add(file.getAbsolutePath());
                            DDLog.i(file.getAbsolutePath());
                        }
                    }
                } else {
                    DDLog.w("查找音乐曲目失败，没有可以播放的曲目！");
                }
                File emr = new File(EMR_MEDIA_PATH);
                if (emr.exists() && emr.isDirectory()) {
                    File[] files = emr.listFiles();
                    for (File file : files) {
                        if (file.getName().endsWith(".mp3") || file.getName().endsWith(".MP3") ||
                                file.getName().endsWith(".amr") || file.getName().endsWith(".AMR")) {
                            mEmrList.add(file.getAbsolutePath());
                            DDLog.i(file.getAbsolutePath());
                        }
                    }
                } else {
                    DDLog.w("查找紧急广播音频失败，没有可以播放的曲目！");
                }
                mCurrentList = mNormalList;
            }
        });
    }

    public static MusicPlayer getInstance() {
        if (mInstance == null) {
            synchronized (MusicPlayer.class) {
                if (mInstance == null) {
                    mInstance = new MusicPlayer();
                }
            }
        }
        return mInstance;
    }

    //播放模式
    public enum PLAY_MODE {
        SINGLE_LOOP, LIST_LOOP, RANDOM
    }

    //播放音频类型
    public enum PLAY_TYPE {
        NORMAL, //常规音频
        EMERGENCY//紧急广播
    }

    public PLAY_TYPE getPlayType() {
        return mPlayType;
    }

    public MusicPlayer setPlayType(PLAY_TYPE playType) {
        this.mPlayType = playType;
        switch (mPlayType) {
            case NORMAL:
                this.mCurrentList = mNormalList;
                break;
            case EMERGENCY:
                this.mCurrentList = mEmrList;
                //如果当前正在播放其它东西，立即停止播放紧急音频
                if (mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                    play();
                }
                break;
        }
        return this;
    }

    public PLAY_MODE getPlayMode() {
        return mPlayMode;
    }

    public MusicPlayer setPlayMode(PLAY_MODE playMode) {
        this.mPlayMode = playMode;
        return this;
    }

    public void play() {
        DDLog.i("play");
        if (mCurrentList.size() <= 0) {
            DDLog.w("没有曲目可以播放！");
            return;
        }
        try {
            setIndexBeforePlay();
            mMediaPlayer.setDataSource(mCurrentList.get(mCurrentNormalIndex));

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (mPlayMode == PLAY_MODE.SINGLE_LOOP) {
                mMediaPlayer.setLooping(true);
            } else {
                mMediaPlayer.setLooping(false);
            }
            // 通过异步的方式装载媒体资源
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(mMediaPlayerPreparedListener);
            mMediaPlayer.setOnCompletionListener(mMediaPlayerCompleteListener);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        } else {
            DDLog.w("当前没有播放，无法暂停！");
        }
    }

    /**
     * 播放前设置曲目下标
     */
    private void setIndexBeforePlay() {
        mCurrentNormalIndex = mLastNormalIndex;
    }

    /**
     * 播放完成后设置曲目下标
     */
    private void setIndexAfterPlay() {
        mLastNormalIndex = mCurrentNormalIndex;
        switch (mPlayMode) {
            case RANDOM:
                mCurrentNormalIndex = new Random().nextInt(mCurrentList.size() - 1);
                break;
            case LIST_LOOP:
                mCurrentNormalIndex = (mCurrentNormalIndex + 1) % mCurrentList.size();
                break;
            case SINGLE_LOOP:
                //TODO 不做处理
                break;
        }
        DDLog.i("setIndexAfterPlay,mode=" + mPlayMode + ",mCurrentNormalIndex=" + mCurrentNormalIndex);
    }

    private MediaPlayer.OnCompletionListener mMediaPlayerCompleteListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            playNext();
        }
    };

    /**
     * 播放下一首
     */
    private void playNext() {
        DDLog.i("playNext");
        mMediaPlayer.reset();
        setIndexAfterPlay();//确定下一首播放啥

        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(mCurrentList.get(mCurrentNormalIndex));
            if (mPlayMode == PLAY_MODE.SINGLE_LOOP) {
                mMediaPlayer.setLooping(true);
            } else {
                mMediaPlayer.setLooping(false);
            }
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaPlayer.OnPreparedListener mMediaPlayerPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            // 装载完毕回调
            mMediaPlayer.start();
        }
    };
}
