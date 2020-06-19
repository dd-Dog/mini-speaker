package com.flyscale.alertor.netty;

import android.app.AlarmManager;
import android.media.MediaPlayer;

import com.flyscale.alertor.BuildConfig;
import com.flyscale.alertor.R;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.up.UAlarm;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.NetHelper;
import com.flyscale.alertor.helper.SoundPoolHelper;
import com.flyscale.alertor.led.LedInstance;
import com.flyscale.alertor.media.AlarmMediaInstance;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 高鹤泉
 * @TIME 2020/6/15 15:50
 * @DESCRIPTION ip报警
 */
public class AlarmHelper {


    private static final AlarmHelper ourInstance = new AlarmHelper();
    Timer mTimer;
    AtomicBoolean mAlarmResult = new AtomicBoolean(false);
    int mSendCount = 1;


    public static AlarmHelper getInstance() {
        return ourInstance;
    }

    private AlarmHelper() {

    }

    /**
     * 设置报警结果
     * @param result
     */
    public void setAlarmResult(boolean result){
        mAlarmResult.set(result);
    }

    /**
     * 轮询 每隔1秒发送一次报警信息
     */
    public void polling(final onAlarmFailListener listener){
        //报警时，如果网络没有连通，要提示“网络连接失败”。
        if(!NetHelper.isNetworkConnected(BaseApplication.sContext)){
            MediaHelper.play(MediaHelper.WORK_WRONG,true);
            return;
        }
        //报警时，如果没有连接到服务器，要提示“连接服务器失败”。
        if(!NettyHelper.getInstance().isConnect()){
            MediaHelper.play(MediaHelper.CONNECT_FAIL,true);
        }
        //闪灯和播放警铃
        alarmStart();
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
        }
        mTimer = new Timer();
        mSendCount = 1;
        mAlarmResult = new AtomicBoolean(false);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!mAlarmResult.get() && mSendCount <= 3){
                    NettyHelper.getInstance().send(new UAlarm(mSendCount));
                    mSendCount ++;
                }else {
                    if(!mAlarmResult.get() && mSendCount> 3){
                        listener.onAlarmFail();
                    }else {
                        //IP报警成功
                        alarmFinish();
                        MediaHelper.play(MediaHelper.ALARM_SUCCESS,true);
                    }
                    destroy();
                }
            }
        },50,1000);
    }

    /**
     * ip报警结束  不代表一定成功
     */
    public void destroy(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        mSendCount = 1;
        mAlarmResult = new AtomicBoolean(false);
    }



    public interface onAlarmFailListener{
        void onAlarmFail();
    }

    /**
     * 报警结束
     * 关闭闪光灯 关闭报警音
     */
    public void alarmFinish(){
        AlarmMediaInstance.getInstance().stopLoopAlarm();
        LedInstance.getInstance().cancelBlinkOffAlarmLed();
    }

    /**
     * 开始报警
     * 闪灯 响铃
     */
    //todo 需要静默开关控制
    public void alarmStart(){
        AlarmMediaInstance.getInstance().playLoopAlarm();
        LedInstance.getInstance().blinkAlarmLed();
    }

}
