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
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.PhoneUtil;

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

        }
    }
}
