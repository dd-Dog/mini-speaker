package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistPair;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.FMUtil;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.PhoneUtil;
import com.flyscale.alertor.media.MusicPlayer;

/**
 * @author 高鹤泉
 * @TIME 2020/7/7 17:57
 * @DESCRIPTION 定制的广播 以后都放在这
 */
public class FlyscaleReceiver extends BroadcastReceiver {

    String TAG = "FlyscaleReceiver";
    static long sStudyStart,sStudyEnd;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "onReceive: " + action);
        if(TextUtils.equals(action,BRConstant.ACTION_USB_TOGGLE)){
            BaseApplication.sFlyscaleManager.setAdbEnabled(!BaseApplication.sFlyscaleManager.isAdbEnabled());
        }else if(TextUtils.equals(action,BRConstant.ACTION_STUDY_DOWN)){
            sStudyStart = System.currentTimeMillis();
        }else if(TextUtils.equals(action,BRConstant.ACTION_STUDY_UP)){
            sStudyEnd = System.currentTimeMillis();
            long remain = sStudyEnd - sStudyStart;
            Log.i(TAG, "onReceive: 本次按压时间为 ----- " + remain);
            if(remain > 4500){
                MediaHelper.play(MediaHelper.PAIR_CLEAR,true);
                PersistPair.clearAll(true);
            }else if(1200 < remain && remain < 2500){
                MediaHelper.play(MediaHelper.PAIR_STUDY,true);
            }
        } else if (TextUtils.equals(action,BRConstant.ACTION_FUNCTION_KEY1_DOWN)) {
            String key1 = PersistConfig.findConfig().getKey1Num();
            DDLog.d(getClass() , "按下功能键1 , 拨号：" + key1);
            if (!TextUtils.equals(key1, "0")) PhoneUtil.call(context , key1);

        } else if (TextUtils.equals(action,BRConstant.ACTION_FUNCTION_KEY2_DOWN)) {
            String key2 = PersistConfig.findConfig().getKey2Num();
            DDLog.d(getClass() , "按下功能键2 , 拨号：" + key2);
            if (!TextUtils.equals(key2, "0")) PhoneUtil.call(context , key2);

        } else if (TextUtils.equals(action,BRConstant.ACTION_FUNCTION_KEY3_DOWN)) {
            String key3 = PersistConfig.findConfig().getKey3Num();
            DDLog.d(getClass() , "按下功能键3 , 拨号：" + key3);
            if (!TextUtils.equals(key3, "0")) PhoneUtil.call(context , key3);

        } else if (TextUtils.equals(action,BRConstant.ACTION_FUNCTION_KEY4_DOWN)) {
            String key4 = PersistConfig.findConfig().getKey4Num();
            DDLog.d(getClass() , "按下功能键4, 拨号：" + key4);
            if (!TextUtils.equals(key4, "0")) PhoneUtil.call(context , key4);
        } else if (TextUtils.equals(action , BRConstant.ACTION_FM_AND_MP3)) {
            //选择FM或者MP3切换
            int mode = PersistConfig.findConfig().getPlayMode();
            if (mode == 1) {
                //播放MP3
                PersistConfig.savePlayMode(0);
                FMUtil.stopFM(context);
                MusicPlayer.getInstance().playLocal();
            } else {
                //播放FM
                PersistConfig.savePlayMode(1);
                MusicPlayer.getInstance().pause(false);
                FMUtil.startFM(context);
                FMUtil.searchFM(context);
            }
        } else if (TextUtils.equals(action , BRConstant.ACTION_PREV)) {
            //上一首
            int mode = PersistConfig.findConfig().getPlayMode();
            if (mode == 0) {
                //播放上一首MP3
                MusicPlayer.getInstance().playLastMusic();
            } else {
                //播放上一个FM频道

            }
        } else if (TextUtils.equals(action , BRConstant.ACTION_STOP_AND_PLAY)) {
            //暂停和播放
            int mode = PersistConfig.findConfig().getPlayMode();
            if (mode == 0) {
                //暂停或者开始MP3
                if (MusicPlayer.getInstance().isPlaying()) {
                    MusicPlayer.getInstance().pause(true);
                } else {
                    MusicPlayer.getInstance().playNext();
                }
            } else {
                //暂停或者播放FM
                FMUtil.pauseFM(context);
            }
        } else if (TextUtils.equals(action , BRConstant.ACTION_NEXT)) {
            //下一首
            int mode = PersistConfig.findConfig().getPlayMode();
            if (mode == 0) {
                //播放下一首MP3
                MusicPlayer.getInstance().playNext();
            } else {
                //播放下一个FM频道
                FMUtil.adjustFM(context , 0);
            }

        } else if (TextUtils.equals(action , BRConstant.ACTION_PEOPLE_SERVICES)) {
            //民生服务

        }
    }
}
