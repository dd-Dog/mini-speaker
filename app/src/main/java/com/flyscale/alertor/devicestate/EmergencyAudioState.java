package com.flyscale.alertor.devicestate;

import android.util.Log;

import com.flyscale.alertor.Constants;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.media.MusicPlayer;
import com.flyscale.alertor.netty.NettyHandler;

public class EmergencyAudioState implements IState {
    /**
     * 状态触发优先级
     */
    public static final int PRIORITY = 2;

    private StateManager stateManager;

    public EmergencyAudioState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void start() {
        Log.i("TAG", "start: 播放紧急语音");
//        MusicPlayer.getInstance().playTip(Constants.FilePath.FILE_EMR + "JINJIMP3.AMR", true,
//                PersistConfig.findConfig().getPlayTimes());
        String fileName = PersistConfig.findConfig().getFileName();
        long size = PersistConfig.findConfig().getSize();
        int playTimes = PersistConfig.findConfig().getPlayTimes();
        String type = PersistConfig.findConfig().getType();

        NettyHandler.DownLoadAmr(fileName, size, playTimes, type);

        if (!MusicPlayer.getInstance().isPlaying()) {
            stop();
        }
    }

    @Override
    public void pause() {
        MusicPlayer.getInstance().pause(true);
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
