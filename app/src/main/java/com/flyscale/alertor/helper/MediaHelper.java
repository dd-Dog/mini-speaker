package com.flyscale.alertor.helper;

import android.content.Context;
import android.media.MediaPlayer;
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
    int[] mRawIds = {R.raw.v1_platform_disconnected,R.raw.v2_welcome,R.raw.v3_platform_connect_success,R.raw.v4_xinjiang_telecom_welcome
            ,R.raw.v5_working_state_wrong,R.raw.v6_battery_low,R.raw.v7_check_simcard_please,R.raw.v8_send_alarm_success
            ,R.raw.v9_platform_connect_success,R.raw.v10_platform_disconnected};

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
        }
        switch (type){
            case WELCOME_USE:
                play(BaseApplication.sContext,R.raw.v2_welcome);
                break;
            case CHECKOUT_SIM:
                play(BaseApplication.sContext,R.raw.v7_check_simcard_please);
                break;
            case WORK_WRONG:
                play(BaseApplication.sContext,R.raw.v5_working_state_wrong);
                break;
            case ALARM_SUCCESS:
                play(BaseApplication.sContext,R.raw.v8_send_alarm_success);
                break;
            case XINJIANG_WELCOME:
                play(BaseApplication.sContext,R.raw.v4_xinjiang_telecom_welcome);
                break;
            case CONNECT_SUCCESS:
                play(BaseApplication.sContext,R.raw.v9_platform_connect_success);
                break;
            case BATTERY_LOW:
                play(BaseApplication.sContext,R.raw.v6_battery_low);
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
    private static void play(Context context, int resId) {
        if(!isPlayInArray){
            isPlayInArray = true;
            mMediaPlayer = MediaPlayer.create(context, resId);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    sPlayTypeArray.remove(0);
                    isPlayInArray = false;
                    if(ListHelper.isValidCollection(sPlayTypeArray)){
                        play(sPlayTypeArray.get(0),false);
                    }
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

    public static void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }


    public static MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }
}
