package com.flyscale.alertor.media;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.MainActivity;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.packet.CMD;
import com.flyscale.alertor.data.packet.TcpPacket;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.DateHelper;
import com.flyscale.alertor.helper.FillZeroUtil;
import com.flyscale.alertor.helper.ThreadPool;
import com.flyscale.alertor.netty.NettyHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.flyscale.alertor.MainActivity.timer;
import static com.flyscale.alertor.helper.InternetUtil.TAG;

public class MusicPlayer {
    private final MediaPlayer mMediaPlayer;

    public static final String MEDIA_PATH = "/mnt/sdcard/flyscale/media/normal/";
    private static final String EMR_MEDIA_PATH = "/mnt/sdcard/flyscale/media/emr/";

    private final ArrayList<String> mNormalList;
    private final ArrayList<String> mEmrList;

    private ArrayList<String> mCurrentList;//当前正在播放的列表

    private Integer mLastNormalIndex = 0;   //记录上一首次播放的文件
    private Integer mLastEmrIndex = 0;

    private Integer mCurrentNormalPausePoint = 0;   //记录当前播放的停止位置，用于恢复播放
    private Integer mCurrentNormalIndex = 0;
    private Integer mCurrentEmrIndex = 0;
    private Integer mCurrentEmrPausePoint = 0;

    private PLAY_MODE mPlayMode = PLAY_MODE.LIST_LOOP;//默认列表循环播放
    private PLAY_TYPE mPlayType = PLAY_TYPE.NORMAL;//默认播放常规音频
    public static int mPlayCount;
    public static String music;

    private boolean mAutoPlayNext = true;//自动播放下一首，区别于手动播放下一首

    private MusicPlayer() {
        mMediaPlayer = new MediaPlayer();
        mNormalList = new ArrayList<>();
        mEmrList = new ArrayList<>();
        //获取曲目列表
        loadFiles();
    }

    private void loadFiles() {
        mNormalList.clear();
        mEmrList.clear();
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                File normal = new File(MEDIA_PATH);
                if (normal.exists() && normal.isDirectory()) {
                    File[] files = normal.listFiles();
                    for (File file : files) {
                        if (file.getName().endsWith(".mp3") || file.getName().endsWith(".MP3") ||
                                file.getName().endsWith(".amr") || file.getName().endsWith(".AMR")) {
                            if (!file.getName().equalsIgnoreCase("GUOGE.AMR")) {
                                mNormalList.add(file.getAbsolutePath());
                                DDLog.i(file.getAbsolutePath());
                            }
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


    private static class MusicPlayerSingle {
        public static MusicPlayer S_INSTANCE = new MusicPlayer();
    }

    public static MusicPlayer getInstance() {
        return MusicPlayerSingle.S_INSTANCE;
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
        switch (playType) {
            case NORMAL:
                this.mCurrentList = mNormalList;
                break;
            case EMERGENCY:
                this.mCurrentList = mEmrList;
                //如果当前正在播放其它东西，立即停止播放紧急音频
                if (mMediaPlayer.isPlaying()) {
                    pause(true);
                    playLocal();
                }
                break;
            default:
                return this;
        }
        this.mPlayType = playType;
        return this;
    }

    public PLAY_MODE getPlayMode() {
        return mPlayMode;
    }

    public MusicPlayer setPlayMode(PLAY_MODE playMode) {
        this.mPlayMode = playMode;
        return this;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public boolean isLooping() {
        return mMediaPlayer.isLooping();
    }

    /**
     * 播放一条音频消息
     *
     * @param path
     * @param enforce 如果当前正在播放，是否强制停止当前
     */
    public void playTip(final String path, boolean enforce, final int count) {
        mPlayCount = count;
        music = path;
        boolean play = false;
        setPlayType(PLAY_TYPE.EMERGENCY);
        if (mPlayCount == 0) {
            if (timer != null) {
                timer.cancel();
                play = true;
            }
        }
        if (TextUtils.isEmpty(path)) {
            DDLog.i("文件路径为空");
            return;
        }
        if (mMediaPlayer.isPlaying() && enforce) {
            pause(true);
        } else if (mMediaPlayer.isPlaying()) {
            DDLog.i("播放器正忙");
            return;
        }
        try {
            final Timer timer = new Timer();
            DDLog.i("playTip: " + path);
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mMediaPlayer.setLooping(mPlayMode == PLAY_MODE.SINGLE_LOOP);
            // 通过异步的方式装载媒体资源
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mPlayType == PLAY_TYPE.NORMAL && mCurrentNormalPausePoint > 0) {
                        mMediaPlayer.seekTo(mCurrentNormalPausePoint);
                        mCurrentNormalPausePoint = 0;
                    } else if (mPlayType == PLAY_TYPE.EMERGENCY && mCurrentEmrPausePoint > 0) {
                        mMediaPlayer.seekTo(mCurrentEmrPausePoint);
                        mCurrentEmrPausePoint = 0;
                    }
                    if (mPlayCount != 0) {
                        mp.start();
                        sendStartEmrBroadcast();
                    }
                }
            });
            final boolean finalPlay = play;
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(final MediaPlayer mp) {
                    mPlayCount--;
                    if (mPlayCount > 0) {
                        DDLog.i("播放次数" + mPlayCount);
                        if (mPlayCount == 1) {
                            mp.start();
                            DDLog.i("播放完成" + mPlayCount);
                        } else if (mPlayCount > 1) {
                            mp.start();
                            timer.schedule(new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    DDLog.i("播放完成" + mPlayCount);
                                }
                            }, 1000);
                        }
                    }
                    if (mPlayCount == 0) {
                        DDLog.i("onCompletion: " + mNormalList);
                        if (finalPlay) {
                            setPlayType(PLAY_TYPE.NORMAL);
                            playNext();
                        } else {
                            sendStopBroadcast();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放本地文件
     */
    public void playLocal() {
        DDLog.i("play");
        if (mCurrentList.size() <= 0) {
            DDLog.w("没有曲目可以播放！");
            return;
        }
        try {
            setIndexBeforePlay();
            mMediaPlayer.setDataSource(mCurrentList.get(mCurrentNormalIndex));

            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(mPlayMode == PLAY_MODE.SINGLE_LOOP);
            // 通过异步的方式装载媒体资源
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(mMediaPlayerPreparedListener);
            mMediaPlayer.setOnCompletionListener(mMediaPlayerCompleteListener);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 暂停播放
     *
     * @param reload 表示此次暂停将要重新加载文件
     */
    public void pause(boolean reload) {
        DDLog.i("pause,reload=" + reload);
        if (reload) {
            //分类型记录当前播放位置，因为两者都可以被中断
            if (mPlayType == PLAY_TYPE.NORMAL) {
                mCurrentNormalPausePoint = mMediaPlayer.getCurrentPosition();
                DDLog.i("mCurrentNormalPausePoint=" + mCurrentNormalPausePoint);
            } else {
                mCurrentEmrPausePoint = mMediaPlayer.getCurrentPosition();
            }
            mMediaPlayer.pause();
            mMediaPlayer.reset();
            if (timer != null) {
                timer.cancel();
            }
        } else {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                if (timer != null) {
                    timer.cancel();
                }
            } else {
                DDLog.w("当前没有播放，无法暂停！");
            }
        }
        Intent intent = new Intent("flyscale.music.pause");
        BaseApplication.sContext.sendBroadcast(intent);
    }

    /**
     * 播放前设置曲目下标
     */
    private void setIndexBeforePlay() {
        if (mPlayType == PLAY_TYPE.NORMAL) {
            mCurrentNormalIndex = mLastNormalIndex;
        } else {
            mCurrentEmrIndex = mLastEmrIndex;
        }
    }

    /**
     * 播放完成后设置曲目下标
     *
     * @param next true:下一首，false:上一首
     */
    private void setIndexAfterPlay(boolean next) {
        DDLog.i("setIndexAfterPlay,mPlayType=" + mPlayType);
        DDLog.i("setIndexAfterPlay: ");
        if (mPlayType == PLAY_TYPE.NORMAL) {
            if (mCurrentNormalPausePoint > 0) {
                //说明之前被中断而暂停，现在要恢复,所以不再更改当前曲目
                return;
            }
            DDLog.i("setIndexAfterPlay: 下一首之前的" + mCurrentNormalIndex);
            if (next)
                mLastNormalIndex = mCurrentNormalIndex;
            else {
                mLastNormalIndex = ((mCurrentNormalIndex - 2) + mCurrentList.size()) % mCurrentList.size();
            }
            switch (mPlayMode) {
                case RANDOM:
                    mCurrentNormalIndex = new Random().nextInt(mCurrentList.size() - 1) % mCurrentList.size();
                    break;
                case LIST_LOOP:
                    mCurrentNormalIndex = (mCurrentNormalIndex + (next ? 1 : -1) + mCurrentList.size()) % mCurrentList.size();
                    break;
                case SINGLE_LOOP:
                    //TODO 不做处理
                    break;
            }
            DDLog.i("setIndexAfterPlay,mode=" + mPlayMode + ",mCurrentNormalIndex=" + mCurrentNormalIndex);
        } else {
            if (mCurrentEmrPausePoint > 0) {
                //说明之前被中断而暂停，现在要恢复,所以不再更改当前曲目
                return;
            }
            mLastEmrIndex = mCurrentEmrIndex;
            switch (mPlayMode) {
                case RANDOM:
                    mCurrentEmrIndex = new Random().nextInt(mCurrentList.size() - 1);
                    break;
                case LIST_LOOP:
                    mCurrentEmrIndex = (mCurrentEmrIndex + 1) % mCurrentList.size();
                    break;
                case SINGLE_LOOP:
                    //TODO 不做处理
                    break;
            }
            DDLog.i("setIndexAfterPlay,mode=" + mPlayMode + ",mCurrentEmrIndex=" + mCurrentEmrIndex);
        }

        DDLog.i("setIndexAfterPlay: 下一首播放位置" + mCurrentNormalIndex);
    }

    private final MediaPlayer.OnCompletionListener mMediaPlayerCompleteListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            DDLog.i("onCompletion,播放下一首");
            if (mAutoPlayNext)
                playNext();
        }
    };


    /**
     * 手动播放下一首
     */
    public void playNextManual() {
        mAutoPlayNext = false;//手动播放了下一首
        playNext();//播放下一首
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //延时，避免播放回调监听读取到播放前的值
                mAutoPlayNext = true;//播放动作执行后，设置为自动播放下一首
            }
        }, 300);

    }

    /**
     * 播放下一首
     */
    public void playNext() {
        DDLog.i("playNext=" + mCurrentNormalIndex);
        mMediaPlayer.reset();
        setIndexAfterPlay(true);//确定下一首播放啥
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            String filePath = "";
            if (mPlayType == PLAY_TYPE.NORMAL) {
                filePath = mCurrentList.get(mCurrentNormalIndex);
            } else {
                filePath = mCurrentList.get(mCurrentEmrIndex);
            }
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.setLooping(mPlayMode == PLAY_MODE.SINGLE_LOOP);
            mMediaPlayer.setOnPreparedListener(mMediaPlayerPreparedListener);
            mMediaPlayer.setOnCompletionListener(mMediaPlayerCompleteListener);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final MediaPlayer.OnPreparedListener mMediaPlayerPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            // 装载完毕回调
            DDLog.i("mCurrentNormalPausePoint=" + mCurrentNormalPausePoint + ",mCurrentEmrPausePoint=" + mCurrentEmrPausePoint);
            if (mPlayType == PLAY_TYPE.NORMAL && mCurrentNormalPausePoint > 0) {
                mMediaPlayer.seekTo(mCurrentNormalPausePoint);
                mCurrentNormalPausePoint = 0;
            } else if (mPlayType == PLAY_TYPE.EMERGENCY && mCurrentEmrPausePoint > 0) {
                mMediaPlayer.seekTo(mCurrentEmrPausePoint);
                mCurrentEmrPausePoint = 0;
            }
            mMediaPlayer.start();
            music = mCurrentList.get(mCurrentNormalIndex);
            sendStartBroadcast();
        }
    };


    /**
     * 重置所有参数
     *
     * @param enforce 即使存在播放也要重置
     */
    public void reset(boolean enforce) {
        if (!enforce && mMediaPlayer.isPlaying()) {
            return;
        }
        mLastNormalIndex = 0;   //记录上一首次播放的文件
        mLastEmrIndex = 0;
        mCurrentNormalPausePoint = 0;   //记录当前播放的停止位置，用于恢复播放
        mCurrentNormalIndex = 0;
        mCurrentEmrIndex = 0;
        mCurrentEmrPausePoint = 0;

        loadFiles();
        mMediaPlayer.reset();
    }

    public void playBefore(final String path, boolean isPlay, final long address) {
        DDLog.i("playBefore" + isPlay);
        if (!isPlay) {
            DDLog.i("playBefore: 不播放前导音" + path);
            playNext(path, address);
        } else {
            try {
                String QDY = path.substring(0, 38) + "QDY.AMR";
                DDLog.i("playBefore: 播放前导音" + QDY);
                //如果前导音文件不存在，直接播放学习文件
                if (new File(QDY).exists()) {
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(QDY);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    // 通过异步的方式装载媒体资源
                    mMediaPlayer.prepareAsync();
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            if (mPlayType == PLAY_TYPE.NORMAL && mCurrentNormalPausePoint > 0) {
                                mMediaPlayer.seekTo(mCurrentNormalPausePoint);
                                mCurrentNormalPausePoint = 0;
                            } else if (mPlayType == PLAY_TYPE.EMERGENCY && mCurrentEmrPausePoint > 0) {
                                mMediaPlayer.seekTo(mCurrentEmrPausePoint);
                                mCurrentEmrPausePoint = 0;
                            }
                            mMediaPlayer.start();
                        }
                    });
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            DDLog.i("onCompletion: 播放正式音频文件");
                            playNext(path, address);
                        }
                    });
                } else {
                    if (mPlayType == PLAY_TYPE.NORMAL && mCurrentNormalPausePoint > 0) {
                        mMediaPlayer.seekTo(mCurrentNormalPausePoint);
                        mCurrentNormalPausePoint = 0;
                    } else if (mPlayType == PLAY_TYPE.EMERGENCY && mCurrentEmrPausePoint > 0) {
                        mMediaPlayer.seekTo(mCurrentEmrPausePoint);
                        mCurrentEmrPausePoint = 0;
                    }
                    DDLog.i("onCompletion: 播放正式音频文件");
                    playNext(path, address);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void playNext(String path, final long address) {
        DDLog.i("playNext");
        mMediaPlayer.reset();
        mPlayCount = 1;
        try {
            DDLog.i("playNext: " + path);
            if (new File(path).exists()) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setLooping(mPlayMode == PLAY_MODE.SINGLE_LOOP);
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mMediaPlayer.start();
                        sendStartBroadcast();
                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE, address,
                                FillZeroUtil.getString("0/", 32)));
                    }
                });
                final String finalPath = path;
                final long times = System.currentTimeMillis();
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        DDLog.i("播放完成");
                        mPlayCount--;
                        if (mPlayCount == 0) {
                            NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE, address,
                                    FillZeroUtil.getString(finalPath.substring(34) + "/" + Long.toHexString(address) +
                                            1 + "/" + DateHelper.longToString(times, DateHelper.yyMMddHHmmss), 32)));
                            sendStopBroadcast();
                        }
                        mp.start();
                    }
                });
            } else {
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE, address,
                        FillZeroUtil.getString("-100/", 32)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playNormal(String path, int playTimes) {
        try {
            mPlayCount = playTimes;
            music = path;
            if (mMediaPlayer.isPlaying()) {
                for (int i = 0; i < playTimes; i++) {
                    mNormalList.add(path);
                    DDLog.i("playNormal: " + mCurrentList);
                }
                return;
            }
            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepareAsync();
            final Timer timer = new Timer();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (mPlayCount != 0) {
                        if (mPlayType == PLAY_TYPE.NORMAL && mCurrentNormalPausePoint > 0) {
                            mMediaPlayer.seekTo(mCurrentNormalPausePoint);
                            mCurrentNormalPausePoint = 0;
                        } else if (mPlayType == PLAY_TYPE.EMERGENCY && mCurrentEmrPausePoint > 0) {
                            mMediaPlayer.seekTo(mCurrentEmrPausePoint);
                            mCurrentEmrPausePoint = 0;
                        }
                        mMediaPlayer.start();
                        sendStartBroadcast();
                    }
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(final MediaPlayer mp) {
                    mPlayCount--;
                    if (mPlayCount > 0) {
                        DDLog.i("播放次数" + mPlayCount);
                        if (mPlayCount == 1) {
                            mp.start();
                            DDLog.i("播放完成" + mPlayCount);
                        } else if (mPlayCount > 1) {
                            mp.start();
                            timer.schedule(new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    DDLog.i("播放完成" + mPlayCount);
                                }
                            }, 1000);
                        }
                    }
                    if (mPlayCount == 0) {
                        sendStopBroadcast();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendStartBroadcast() {
        Intent intent = new Intent("flyscale.music.start");
        BaseApplication.sContext.sendBroadcast(intent);
    }

    private void sendStopBroadcast() {
        Intent intent = new Intent("flyscale.music.stop");
        BaseApplication.sContext.sendBroadcast(intent);
    }

    private void sendStartEmrBroadcast() {
        Intent intent = new Intent("flyscale.emr.start");
        BaseApplication.sContext.sendBroadcast(intent);
    }

    private void sendStopEmrBroadcast() {
        Intent intent = new Intent("flyscale.emr.stop");
        BaseApplication.sContext.sendBroadcast(intent);
    }

    //播放上一首文件
    public void playLastMusic() {
        //分情况，如果播放的为第一首歌，直接播放最后一首；其余情况播放上一首
        DDLog.i("playLast: 本地播放上一首" + mNormalList.get(mLastNormalIndex));
        mAutoPlayNext = false;  //禁止自动播放下一首
        DDLog.i("playLastMusic: " + mCurrentNormalIndex);
        mMediaPlayer.reset();
        setIndexAfterPlay(false);//确定下一首播放啥
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            String filePath = "";
            if (mPlayType == PLAY_TYPE.NORMAL) {
                filePath = mCurrentList.get(mCurrentNormalIndex);
            } else {
                filePath = mCurrentList.get(mCurrentEmrIndex);
            }
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.setLooping(mPlayMode == PLAY_MODE.SINGLE_LOOP);
            mMediaPlayer.setOnPreparedListener(mMediaPlayerPreparedListener);
            mMediaPlayer.setOnCompletionListener(mMediaPlayerCompleteListener);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mAutoPlayNext = true;
                }
            }, 300);
        }
    }

    //播放下一首文件
    public void playNextMusic() {
        //分情况，如果播放的为最后一首歌，直接播放第一首；其余情况播放上一首
        DDLog.i("playNextMusic：本地播放下一首");
        mMediaPlayer.reset();
        setIndexAfterPlay(true);//确定下一首播放啥
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            String filePath = "";
            if (mPlayType == PLAY_TYPE.NORMAL) {
                filePath = mCurrentList.get(mCurrentNormalIndex);
            } else {
                filePath = mCurrentList.get(mCurrentEmrIndex);
            }
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.setLooping(mPlayMode == PLAY_MODE.SINGLE_LOOP);
            mMediaPlayer.setOnPreparedListener(mMediaPlayerPreparedListener);
            mMediaPlayer.setOnCompletionListener(mMediaPlayerCompleteListener);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTime() {
        return mMediaPlayer.getCurrentPosition();
    }

    public String getDuration() {
        return DateHelper.ssToMM(mMediaPlayer.getDuration());
    }
}
