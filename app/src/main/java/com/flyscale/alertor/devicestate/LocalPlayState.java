package com.flyscale.alertor.devicestate;

import android.content.Intent;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.FMUtil;
import com.flyscale.alertor.media.MusicPlayer;

import java.util.Timer;
import java.util.TimerTask;

import static com.flyscale.alertor.MainActivity.timer;

public class LocalPlayState implements IState {
    /**
     * 状态触发优先级
     */
    public static final int PRIORITY = 6;
    private StateManager stateManager;
    boolean fm = false;

    public LocalPlayState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void start() {
        /*  先判断模式，在判断状态*/
        int mode = PersistConfig.findConfig().getPlayMode();
        boolean isPaused = PersistConfig.findConfig().isPauseLocal();
        int localPlay = PersistConfig.findConfig().getLocalPlay();

        Log.i("TAG", "start: 开始播放本地MP3或者FM" + mode);
        Log.i("TAG", "start: 是否暂停" + isPaused);

        final MusicPlayer musicPlayer = MusicPlayer.getInstance();
        if (mode == 0) {
            //播放MP3
            if (isPaused) {
                DDLog.i("恢复本地播放");
                musicPlayer.playNext(true);
                PersistConfig.savePaused(false);
            } else {
                if (localPlay == 1) {
                    DDLog.i("待机状态");
                    PersistConfig.savePaused(false);
                    stop();
                } else if (!musicPlayer.isPlaying()){
                    DDLog.i("开始新的本地播放");
                    FMUtil.stopFM(BaseApplication.sContext);
                    musicPlayer.reset(false);
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            musicPlayer.playLocal();
                        }
                    }, 200);
                    PersistConfig.saveLocalPaused(2);
                    PersistConfig.savePaused(false);
                } else {
                    musicPlayer.pause(true);
                    PersistConfig.savePaused(true);
                    DDLog.i("保存暂停状态");
                }
            }
        } else {
            //播放FM
            PersistConfig.savePaused(false);
            if (timer != null) {
                timer.cancel();
            }
            MusicPlayer.getInstance().pause(false);
            Intent intent = new Intent("flyscale.fm.start");
            BaseApplication.sContext.sendBroadcast(intent);
            if (fm) {
                DDLog.i("暂停");
                FMUtil.pauseFM(BaseApplication.sContext);
            } else {
                DDLog.i("开始新的FM");
                fm = true;
                FMUtil.startFM(BaseApplication.sContext);
                FMUtil.informationFM(BaseApplication.sContext);
            }
        }
    }

    @Override
    public void pause() {
        DDLog.i("优先级为六的暂停方法");
        PersistConfig.savePaused(true);
        MusicPlayer.getInstance().pause(true);
        Log.i("TAG", "pause: 保存本地播放状态" + PersistConfig.findConfig().isPauseLocal());
    }

    @Override
    public void stop() {
        stateManager.setStateByPriority(PRIORITY + 1, false);
    }
    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
