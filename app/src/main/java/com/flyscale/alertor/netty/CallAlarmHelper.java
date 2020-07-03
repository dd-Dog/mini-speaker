package com.flyscale.alertor.netty;

import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.NetHelper;
import com.flyscale.alertor.helper.PhoneUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 高鹤泉
 * @TIME 2020/6/19 16:05
 * @DESCRIPTION 语音报警
 */
public class CallAlarmHelper {
    private static final CallAlarmHelper ourInstance = new CallAlarmHelper();
    Timer mTimer;
    //报警结果
    AtomicBoolean mAlarmResult = new AtomicBoolean(false);
    String TAG = "CallAlarmHelper";
    //是否摘机
    boolean isOffhook = false;
    //是否正在报警
    boolean isAlarming = false;
    //拨打的电话
    private String mSendNumber;
    final int DEFAULT_POLLING_TIME = 25 * 1000;
    //是否执行timer内部的逻辑
    boolean isRunTimerFlag = true;

    public static CallAlarmHelper getInstance() {
        return ourInstance;
    }

    private CallAlarmHelper() {
    }

    public boolean isAlarming() {
        return isAlarming;
    }

    /**
     * 设置报警结果
     * @param result
     */
    public void setAlarmResult(boolean result){
        mAlarmResult.set(result);
    }

    public void setRunTimerFlag(boolean runTimerFlag) {
        isRunTimerFlag = runTimerFlag;
    }

    /**
     * 轮询 每隔20秒打一次电话 直到打通
     * @param callNumber
     * @param is110
     */
    public void polling(String callNumber, final boolean is110){
        //报警时，如果网络没有连通，要提示“网络连接失败”。
        if(!NetHelper.isNetworkConnected(BaseApplication.sContext)){
            MediaHelper.play(MediaHelper.WORK_WRONG,true);
            return;
        }
        //报警时，如果没有连接到服务器，要提示“连接服务器失败”。
        if(!NettyHelper.getInstance().isConnect()){
            MediaHelper.play(MediaHelper.CONNECT_FAIL,true);
            return;
        }
        AlarmHelper.getInstance().alarmStart();
        //当报警电话为空的时候
        //取默认110或者平台报警电话
        if(TextUtils.isEmpty(callNumber)){
            if(is110){
                mSendNumber = PersistConfig.findConfig().getSpecialNum();
            }else {
                mSendNumber = PersistConfig.findConfig().getAlarmNum();
            }
        }else {
            mSendNumber = callNumber;
        }
        cancelTimer();
        pollingInit();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isRunTimerFlag){
                    if(mAlarmResult.get()){
                        //报警成功
                        if(is110){
                            //110报警直接通话了 不需要播放 报警成功的音效
                            destroy(false,true,false);
                        }else {
                            destroy(false,true,false);
                        }
                    }else {
                        //报警失败
                        //这里打电话之前要先判断是否挂断电话
                        //如果挂断电话的状态直接打电话
                        //如果不是挂断的状态 ，则需要挂断电话。
                        //这个挂断是有过程的 大概一秒 然后才会变成挂断的状态
                        isOffhook = PhoneUtil.isOffhook(BaseApplication.sContext);
                        if(isOffhook){
                            //摘机状态 主动挂断
                            PhoneUtil.endCall(BaseApplication.sContext);
                            while (isOffhook){
                                //死循环查询 直到挂断
                                isOffhook = PhoneUtil.isOffhook(BaseApplication.sContext);
                            }
                        }
                        PhoneUtil.call(BaseApplication.sContext,mSendNumber);
                        Log.i(TAG, "run: 语音报警 mSendNumber = " + mSendNumber );
                    }
                }
            }
        },50,DEFAULT_POLLING_TIME);
    }

    /**
     * 清空timer并且重新注册
     */
    private void cancelTimer(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
    }

    /**
     * 轮询打电话之前 要把状态初始化
     * 状态为
     * 1.报警结果 失败
     * 2.正在报警
     * 3.允许执行循环报警
     */
    private void pollingInit(){
        mTimer = new Timer();
        mAlarmResult = new AtomicBoolean(false);
        isAlarming = true;
        isRunTimerFlag = true;
    }

    public void destroy(boolean playSuccess,boolean alarmResult,boolean isRunTimerFlag){
        destroy(playSuccess,alarmResult,isRunTimerFlag,false);
    }

    /**
     * 电话报警
     * 成功后结束报警 播放报警成功
     * 报警过程中主动结束  不播放报警成功
     * @param playSuccess  播放报警成功语音
     * @param alarmResult   报警结果
     * @param isRunTimerFlag  是否执行timer的标志
     * @param selfCancelBeforeActive  通话成功之前主动取消报警
     */
    public void destroy(boolean playSuccess,boolean alarmResult,boolean isRunTimerFlag,boolean selfCancelBeforeActive){
        //停止 报警音效和灯光
        setRunTimerFlag(isRunTimerFlag);
        isAlarming = false;
        mAlarmResult = new AtomicBoolean(alarmResult);
        cancelTimer();
        AlarmHelper.getInstance().alarmFinish();
        //通话成功之前主动取消报警
        if(selfCancelBeforeActive){
            if(PhoneUtil.isOffhook(BaseApplication.sContext)){
                PhoneUtil.endCall(BaseApplication.sContext);
            }
        }
        //语音报警不需要提示用户  报警成功
//        if(playSuccess){
//            MediaHelper.play(MediaHelper.ALARM_SUCCESS,true);
//        }
    }



}
