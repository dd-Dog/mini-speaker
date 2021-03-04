package com.flyscale.alertor.devicestate;

import android.util.Log;

import com.flyscale.alertor.Constants;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.media.MusicPlayer;
import com.flyscale.alertor.netty.NettyHandler;

public class EmergencyAudioState implements IState {
    /**
     * 状态触发优先级
     */
    public static final int PRIORITY = 2;

    private static StateManager stateManager;

    public EmergencyAudioState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    @Override
    public void start() {
        Log.i("TAG", "start: 播放紧急语音");
        int state = PersistConfig.findConfig().getRemote();
        if (state == 2) {
            String fileName = PersistConfig.findConfig().getFileName();
            long size = PersistConfig.findConfig().getSize();
            int playTimes = PersistConfig.findConfig().getPlayTimes();
            String type = PersistConfig.findConfig().getType();
            NettyHandler.DownLoadAmr(fileName, size, playTimes, type);
        } else stop();
    }

    @Override
    public void pause() {
        DDLog.i("优先级为二的暂停方法");
        MusicPlayer.getInstance().pause(true);
    }

    @Override
    public void stop() {
        stateManager.setStateByPriority(PRIORITY + 1, false);
    }

    public static void stops() {
        stateManager.setStateByPriority(PRIORITY + 1, false);
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
