package com.flyscale.alertor.devicestate;

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
