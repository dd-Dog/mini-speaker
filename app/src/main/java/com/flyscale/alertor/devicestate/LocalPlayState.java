package com.flyscale.alertor.devicestate;

import android.content.Intent;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.PersistConfig;
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

    public LocalPlayState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void start() {
        int mode = PersistConfig.findConfig().getPlayMode();

        Log.i("TAG", "start: 开始播放本地MP3或者FM" + mode);
        if (mode == 1) {
            //播放MP3
            PersistConfig.savePlayMode(0);
            FMUtil.stopFM(BaseApplication.sContext);
            final MusicPlayer musicPlayer = MusicPlayer.getInstance();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    musicPlayer.playLocal();
                }
            }, 200);
        } else {
            //播放FM
            if (timer != null) {
                timer.cancel();
            }
            Intent intent = new Intent("flyscale.fm.start");
            BaseApplication.sContext.sendBroadcast(intent);
            PersistConfig.savePlayMode(1);
            MusicPlayer.getInstance().pause(false);
            FMUtil.startFM(BaseApplication.sContext);
            FMUtil.searchFM(BaseApplication.sContext);
            FMUtil.informationFM(BaseApplication.sContext);
        }
    }

    @Override
    public void pause() {

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
