package com.flyscale.alertor.devicestate;

public class AlarmState implements IState {

    private StateManager stateManager;

    public AlarmState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    /**
     * 状态触发优先级
     */
    public static final int PRIORITY = 1;

    @Override
    public void start() {
        stop();
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
