package com.flyscale.alertor.devicestate;

public interface IState {
    /**
     * 根据状态优先级进行状态切换，1-10优先级降低
     */

    /**
     * 进入状态模式工作
     */
    void start();

    /**
     * 暂停当前状态下的工作，保存当前状态，下次触发start时恢复
     */
    void pause();

    /**
     * 完全停止当前状态的工作，下次触发start时重新初始化开始工作
     */
    void stop();

    int getPriority();
}
