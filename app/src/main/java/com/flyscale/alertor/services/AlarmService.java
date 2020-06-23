package com.flyscale.alertor.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.abupdate.fota_demo_iot.data.remote.NewVersionInfo;
import com.flyscale.alertor.FotaAction;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.base.BaseService;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.helper.FileHelper;
import com.flyscale.alertor.helper.FotaHelper;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.netty.NettyHelper;
import com.flyscale.alertor.receivers.AlarmLedReceiver;
import com.flyscale.alertor.receivers.BatteryReceiver;
import com.flyscale.alertor.receivers.CallPhoneReceiver;
import com.flyscale.alertor.receivers.KeyReceiver;
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
    KeyReceiver mKeyReceiver;
    CallPhoneReceiver mCallPhoneReceiver;
    FotaHelper mFotaHelper;

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
        if(!file.exists()){
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
        mKeyReceiver = new KeyReceiver();
        mKeyReceiver.register();
        //拨打电话广播
        mCallPhoneReceiver = new CallPhoneReceiver();
        mCallPhoneReceiver.register();
        //警报灯常亮广播
        AlarmLedReceiver.sendRepeatAlarmBroadcast(PersistConfig.findConfig().getAlarmLedOnTime(),PersistConfig.findConfig().getAlarmOffOffTime());
        //fota升级 todo 没有完成呢！！
//        mFotaHelper = new FotaHelper(this,new FotaAction());
//        mFotaHelper.checkVersion();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();
        mTimer.schedule(new CheckStateTask(),2000,CHECK_STATE_PERIOD);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NettyHelper.getInstance().unRegister();
        mStateManagerReceiver.destroy();
        mBatteryReceiver.unRegister();
        mKeyReceiver.unRegister();
        mTelephonyStateReceiver.destroy();
        mCallPhoneReceiver.unRegister();
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void setState(int state) {
        mState = state;
    }

    /**
     * 根据不同的state操作
     */
    public void actionByState(){
        if(mState == STATE_NO_SIM){
            MediaHelper.play(MediaHelper.CHECKOUT_SIM,true);
        }else if(mState == STATE_NO_NET){
            MediaHelper.play(MediaHelper.WORK_WRONG,true);
        }else if(mState == STATE_SIM_NET_SUCCESS){
            if(mState != mLastState){
                MediaHelper.play(MediaHelper.XINJIANG_WELCOME,true);
            }
        }
        mLastState = mState;
    }

    /**
     * 定时检查工作状态
     */
    private class CheckStateTask extends TimerTask{
        @Override
        public void run() {
            if(mState != -1){
                actionByState();
            }
        }
    }
}
