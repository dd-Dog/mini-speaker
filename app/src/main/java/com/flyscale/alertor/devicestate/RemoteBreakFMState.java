package com.flyscale.alertor.devicestate;

public class RemoteBreakFMState implements IState {
    /**
     * 状态触发优先级
     */
    public static final int PRIORITY = 4;
    private StateManager stateManager;

    public RemoteBreakFMState(StateManager stateManager) {
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
