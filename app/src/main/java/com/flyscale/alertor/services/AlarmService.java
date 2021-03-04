package com.flyscale.alertor.services;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.flyscale.alertor.alarmManager.CallPhoneReceiver2;
import com.flyscale.alertor.base.BaseService;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistWhite;
import com.flyscale.alertor.devicestate.AlarmState;
import com.flyscale.alertor.devicestate.EmergencyAudioState;
import com.flyscale.alertor.devicestate.IdleState;
import com.flyscale.alertor.devicestate.LocalPlayState;
import com.flyscale.alertor.devicestate.RemoteBreakFMState;
import com.flyscale.alertor.devicestate.RemotePlayFMState;
import com.flyscale.alertor.devicestate.RemotePlayMP3State;
import com.flyscale.alertor.devicestate.StateManager;
import com.flyscale.alertor.eventBusManager.EventMessage;
import com.flyscale.alertor.eventBusManager.EventType;
import com.flyscale.alertor.helper.FileHelper;
import com.flyscale.alertor.helper.FotaHelper;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.netty.NettyHelper;
import com.flyscale.alertor.receivers.AlarmLedReceiver;
import com.flyscale.alertor.receivers.BatteryReceiver;
import com.flyscale.alertor.receivers.CallPhoneReceiver;
import com.flyscale.alertor.receivers.StateManagerReceiver;
import com.flyscale.alertor.receivers.TelephonyStateReceiver;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmService extends BaseService {
    public static final int STATE_NO_SIM = 1;
    public static final int STATE_NO_NET = 2;
    public static final int STATE_SIM_NET_SUCCESS = 3;

    int mState = -1;
    int mLastState = -2;
    final int CHECK_STATE_PERIOD = 60 * 1000;
    String TAG = "AlarmService";

    Timer mTimer;
    StateManagerReceiver mStateManagerReceiver;
    BatteryReceiver mBatteryReceiver;
    TelephonyStateReceiver mTelephonyStateReceiver;
    //    CallPhoneReceiver mCallPhoneReceiver;
    CallPhoneReceiver2 mCallPhoneReceiver2;
    public static StateManager mStateManager;

    public AlarmService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        //注册socket
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: start = " + System.currentTimeMillis());
                NettyHelper.getInstance().register();
                NettyHelper.getInstance().connect();
                Log.i(TAG, "run: end = " + System.currentTimeMillis());
            }
        }).start();

        //初始化程序默认文件夹
        File file = new File(FileHelper.getBasePath());
        if (!file.exists()) {
            file.mkdirs();
        }
        //sim卡 网络状态广播
        mStateManagerReceiver = new StateManagerReceiver(this);
        //电池广播
        mBatteryReceiver = new BatteryReceiver();
        mBatteryReceiver.register();
        //手机信号广播
        mTelephonyStateReceiver = new TelephonyStateReceiver(this);
        mTelephonyStateReceiver.listenStrengths();
        //按键广播
//        mKeyReceiver = new KeyReceiver();
//        mKeyReceiver.register();
        //拨打电话广播
        mCallPhoneReceiver2 = new CallPhoneReceiver2();
        mCallPhoneReceiver2.register();
//        mCallPhoneReceiver = new CallPhoneReceiver();
//        mCallPhoneReceiver.register();
        //警报灯常亮广播
        AlarmLedReceiver.sendRepeatAlarmBroadcast(PersistConfig.findConfig().getAlarmLedOnTime(), PersistConfig.findConfig().getAlarmLedOffTime());

        //小喇叭状态管理器
        mStateManager = new StateManager();
    }

    //报警
    public void alarm() {
        mStateManager.setStateByPriority(AlarmState.PRIORITY, true);
    }

    //紧急语音广播
    public static void emergencyAudio() {
        mStateManager.setStateByPriority(EmergencyAudioState.PRIORITY, true);
    }

    //平台控制播放MP3
    public static void remotePlayMP3() {
        mStateManager.setStateByPriority(RemotePlayMP3State.PRIORITY, true);
    }

    //平台插播FM
    public void remoteBreakFM() {
        mStateManager.setStateByPriority(RemoteBreakFMState.PRIORITY, true);
    }

    //平台播放FM
    public void remotePlayFM() {
        mStateManager.setStateByPriority(RemotePlayFMState.PRIORITY, true);
    }

    //本地播放MP3或者FM
    public static void localPlay() {
        mStateManager.setStateByPriority(LocalPlayState.PRIORITY, true);
    }

    //待机
    public void idle() {
        mStateManager.setStateByPriority(IdleState.PRIORITY, true);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();
        mTimer.schedule(new CheckStateTask(), 2000, CHECK_STATE_PERIOD);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        NettyHelper.getInstance().unRegister();
        mStateManagerReceiver.destroy();
        mBatteryReceiver.unRegister();
//        mKeyReceiver.unRegister();
        mTelephonyStateReceiver.destroy();
//        mCallPhoneReceiver.unRegister();
        mCallPhoneReceiver2.unRegister();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onEventBusMain(EventMessage message) {
        super.onEventBusMain(message);
    }

    public void setState(int state) {
        mState = state;
    }

    /**
     * 根据不同的state操作
     */
    public void actionByState() {
        if (mState == STATE_NO_SIM) {
            MediaHelper.play(MediaHelper.CHECKOUT_SIM, true);
        } else if (mState == STATE_NO_NET) {
            MediaHelper.play(MediaHelper.NET_CONNECT_FAIL, true);
        } else if (mState == STATE_SIM_NET_SUCCESS) {

        }
        mLastState = mState;
    }

    /**
     * 定时检查工作状态
     */
    private class CheckStateTask extends TimerTask {
        @Override
        public void run() {
            if (mState != -1) {
                actionByState();
            }
        }
    }
}
