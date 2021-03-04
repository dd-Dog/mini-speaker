package com.flyscale.alertor.devicestate;

public class RemotePlayFMState implements IState {
    /**
     * 状态触发优先级
     */
    public static final int PRIORITY = 5;
    private StateManager stateManager;

    public RemotePlayFMState(StateManager stateManager) {
        this.stateManager = stateManager;
    }

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
