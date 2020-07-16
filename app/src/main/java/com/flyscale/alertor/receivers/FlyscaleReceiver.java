package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.PersistPair;
import com.flyscale.alertor.helper.MediaHelper;

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
            if(remain > 4500){
                MediaHelper.play(MediaHelper.PAIR_CLEAR,true);
                PersistPair.clearAll(true);
            }else if(1200 < remain && remain < 2500){
                MediaHelper.play(MediaHelper.PAIR_STUDY,true);
            }
        }
    }
}
