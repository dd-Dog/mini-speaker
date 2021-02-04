package com.flyscale.alertor.devicestate;

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

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }
    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
