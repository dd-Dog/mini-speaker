package com.flyscale.alertor.helper;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.flyscale.alertor.R;
import com.flyscale.alertor.base.BaseApplication;

import java.util.ArrayList;
import java.util.List;


public class MediaHelper {

    private static MediaPlayer mMediaPlayer;
    public final static int WELCOME_USE = 2;
    public final static int CHECKOUT_SIM = 7;
    public final static int WORK_WRONG = 5;
    public final static int ALARM_SUCCESS = 8;
    public final static int XINJIANG_WELCOME = 4;
    public final static int CONNECT_SUCCESS = 9;
    public final static int BATTERY_LOW = 6;
    public final static int CONNECT_FAIL = 1;
    public final static int NET_CONNECT_SUCCESS = 10;//网络连接成功
    public final static int NET_CONNECT_FAIL = 11;//网络连接失败
    public final static int SERVER_CONNECT_SUCCESS = 12;//连接服务器成功
    public final static int SERVER_CONNECT_FAIL = 13;//连接服务器失败
    public final static int BATTERY_LOW_CHARGE = 14;//电量低请充电
    int[] mRawIds = {R.raw.v1_platform_disconnected,R.raw.v2_welcome,R.raw.v3_platform_connect_success,R.raw.v4_xinjiang_telecom_welcome
            ,R.raw.v5_working_state_wrong,R.raw.v6_battery_low,R.raw.v7_check_simcard_please,R.raw.v8_send_alarm_success
            ,R.raw.v9_platform_connect_success};

    static List<Integer> sPlayTypeArray = new ArrayList<>();

    static String TAG = "MediaHelper";
    //是否正在播放数组里的音频
    private static boolean isPlayInArray = false;
    //是否正在播放 您的报警信息已发出
    public static boolean isPlayAlarmSuccessing = false;

    /**
     *
     * @param type
     * @param isAddTypeToReadyPlayList  是否添加这个播放类型去准备播放的列表
     */
    public static void play(int type,boolean isAddTypeToReadyPlayList){
        if(isAddTypeToReadyPlayList){
            sPlayTypeArray.add(type);
            Log.i(TAG, "play: sPlayTypeArray.size() = " + sPlayTypeArray.size());
        }
        Log.i(TAG, "play: " + type);

        switch (type){
            case WELCOME_USE:
                play(BaseApplication.sContext,R.raw.v2_welcome);
                break;
            case CHECKOUT_SIM:
                play(BaseApplication.sContext,R.raw.v7_check_simcard_please);
                break;
//            case WORK_WRONG:
//                play(BaseApplication.sContext,R.raw.v5_working_state_wrong);
//                break;
            case ALARM_SUCCESS:
                play(BaseApplication.sContext,R.raw.v8_send_alarm_success);
                break;
            case XINJIANG_WELCOME:
                play(BaseApplication.sContext,R.raw.v4_xinjiang_telecom_welcome);
                break;
//            case CONNECT_SUCCESS:
//                play(BaseApplication.sContext,R.raw.v9_platform_connect_success);
//                break;
//            case BATTERY_LOW:
//                play(BaseApplication.sContext,R.raw.v6_battery_low);
//                break;
            case BATTERY_LOW_CHARGE:
                play(BaseApplication.sContext,R.raw.battery_low_charge);
                break;
//            case CONNECT_FAIL:
//                play(BaseApplication.sContext,R.raw.v1_platform_disconnected);
//                break;
            case NET_CONNECT_SUCCESS:
                play(BaseApplication.sContext,R.raw.net_connect_success);
                break;
            case NET_CONNECT_FAIL:
                play(BaseApplication.sContext,R.raw.net_connect_fail);
                break;
            case SERVER_CONNECT_SUCCESS:
                play(BaseApplication.sContext,R.raw.server_connect_success);
                break;
            case SERVER_CONNECT_FAIL:
                play(BaseApplication.sContext,R.raw.server_connect_fail);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    /**
     * 根据音频焦点的控制 顺序播放 不会同时播放出现抢焦点的情况
     * @param context
     * @param resId
     */
    private static void play(Context context, final int resId) {
        if(!isPlayInArray){
            isPlayInArray = true;
            mMediaPlayer = MediaPlayer.create(context, resId);
            Log.i(TAG, "play: mMediaPlayer");
            if(resId == R.raw.v8_send_alarm_success){
                isPlayAlarmSuccessing = true;
            }
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    sPlayTypeArray.remove(0);
                    isPlayInArray = false;
                    if(resId == R.raw.v8_send_alarm_success){
                        isPlayAlarmSuccessing = false;
                    }
                    if(ListHelper.isValidCollection(sPlayTypeArray)){
                        play(sPlayTypeArray.get(0),false);
                    }
                    Log.i(TAG, "onCompletion: ");
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.i(TAG, "onError: ");
                    return false;
                }
            });
            mMediaPlayer.start();
        }
    }

    public static void playNormal(Context context, int resId) {
        mMediaPlayer = MediaPlayer.create(context, resId);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
        mMediaPlayer.start();
    }


    public static void stopAlarmSuccess(){
        if(mMediaPlayer != null){
            mMediaPlayer.pause();
        }
        isPlayAlarmSuccessing = false;
        isPlayInArray = false;
        Log.i(TAG, "stopAlarmSuccess: ");
        if(ListHelper.isValidCollection(sPlayTypeArray)){
            if(sPlayTypeArray.get(0) == ALARM_SUCCESS){
                sPlayTypeArray.remove(0);
                Log.i(TAG, "stopAlarmSuccess: remove");
            }
        }
    }

    public static void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }


    public static MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
}
