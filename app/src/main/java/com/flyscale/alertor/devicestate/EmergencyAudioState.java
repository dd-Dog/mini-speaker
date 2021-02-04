package com.flyscale.alertor.devicestate;

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
