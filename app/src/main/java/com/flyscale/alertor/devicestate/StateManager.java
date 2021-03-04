package com.flyscale.alertor.devicestate;

import android.util.Log;

import com.flyscale.alertor.helper.DDLog;

import java.util.HashMap;

/**
 * 小喇叭状态管理器
 * 用于进行设备状态的切换，不同状下如何工作
 */
public class StateManager {
    private IState mState;
    private static final String TAG = "StateManager";
    private final HashMap<Integer, IState> stateList;


    public StateManager() {
        stateList = new HashMap<>();
        stateList.put(AlarmState.PRIORITY, new AlarmState(this));
        stateList.put(EmergencyAudioState.PRIORITY, new EmergencyAudioState(this));
        stateList.put(RemotePlayMP3State.PRIORITY, new RemotePlayMP3State(this));
        stateList.put(RemoteBreakFMState.PRIORITY, new RemoteBreakFMState(this));
        stateList.put(RemotePlayFMState.PRIORITY, new RemotePlayFMState(this));
        stateList.put(LocalPlayState.PRIORITY, new LocalPlayState(this));
        stateList.put(IdleState.PRIORITY, new IdleState(this));

        setStateByPriority(IdleState.PRIORITY, false);
    }

    public IState getState() {
        return mState;
    }

    /**
     * 根据优先级切换状态
     *
     * @param priority  优先级
     * @param saveState 是否要保存原有的状态用于恢复
     *                  一般被中断的状态需要保存，状态结束后返回不需要保存
     */
    public void setStateByPriority(int priority, boolean saveState) {
        if (mState != null && mState.getPriority() == priority) {
            DDLog.i("setStateByPriority，已经在这个状态了呢。priority=" + priority);
            mState.start();
            return;
        }
        IState state = stateList.get(priority);
        Log.i(TAG, "setStateByPriority: " + state.getPriority());

        if (state == null) {
            DDLog.e("状态转换异常，未知的优先级");
        } else {
            if (saveState) {
                Log.i(TAG, "setStateByPriority: 是否走暂停的方法" + saveState);
                Log.i(TAG, "setStateByPriority: " + this.mState.getPriority());
                this.mState.pause();
            }
            this.mState = state;
            mState.start();
        }
    }

    public void start() {
        this.mState.start();
    }

    public void pause() {
        this.mState.pause();
    }

    public void stop() {
        this.mState.stop();
    }
}
