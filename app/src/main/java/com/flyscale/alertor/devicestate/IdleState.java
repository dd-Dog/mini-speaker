package com.flyscale.alertor.devicestate;

import android.util.Log;

import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.media.MusicPlayer;

public class IdleState implements IState {
    private StateManager stateManager;

    public IdleState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    /**
     * 状态触发优先级
     */
    public static final int PRIORITY = 7;

    public IdleState() {
    }

    @Override
    public void start() {
        DDLog.i("我是待机");
    }

    @Override
    public void pause() {
        DDLog.i("优先级为七的暂停方法");
    }

    @Override
    public void stop() {
//        stateManager.setStateByPriority(1, false);
    }
    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
