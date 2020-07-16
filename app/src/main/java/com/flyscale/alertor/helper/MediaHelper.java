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
    public final static int PAIR_REMOTE_CONTROL = 15;//遥控器配对
    public final static int PAIR_INFRARED = 16;//红外配对
    public final static int PAIR_DOOR = 17;//门磁配对
    public final static int PAIR_SMOKE = 18;//烟感
    public final static int PAIR_GAS = 19;//
    public final static int PAIR_ARMING = 20;//布防
    public final static int PAIR_DISARMING = 21;//撤防
    public final static int PAIR_STUDY = 22;//开始学习
    public final static int PAIR_CLEAR = 23;//解除配对


    static List<Integer> sPlayTypeArray = new ArrayList<>();

    static String TAG = "MediaHelper";
    //是否正在播放数组里的音频
    private static boolean isPlayInArray = false;

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
//            case ALARM_SUCCESS:
//                play(BaseApplication.sContext,R.raw.v8_send_alarm_success);
//                break;
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
            case PAIR_REMOTE_CONTROL:
                play(BaseApplication.sContext,R.raw.p_control);
                break;
            case PAIR_INFRARED:
                play(BaseApplication.sContext,R.raw.p_infrared);
                break;
            case PAIR_DOOR:
                play(BaseApplication.sContext,R.raw.p_door);
                break;
            case PAIR_SMOKE:
                play(BaseApplication.sContext,R.raw.p_smoke);
                break;
            case PAIR_GAS:
                play(BaseApplication.sContext,R.raw.p_gas);
                break;
            case PAIR_ARMING:
                play(BaseApplication.sContext,R.raw.p_arming);
                break;
            case PAIR_DISARMING:
                play(BaseApplication.sContext,R.raw.p_disarming);
                break;
            case PAIR_STUDY:
                play(BaseApplication.sContext,R.raw.p_study);
                break;
            case PAIR_CLEAR:
                play(BaseApplication.sContext,R.raw.p_clear);
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
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(ListHelper.isValidCollection(sPlayTypeArray)){
                        sPlayTypeArray.remove(0);
                    }
                    isPlayInArray = false;
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


    public static void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }


    public static MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
}
