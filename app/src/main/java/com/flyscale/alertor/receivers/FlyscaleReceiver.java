package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.flyscale.alertor.base.BaseApplication;

/**
 * @author 高鹤泉
 * @TIME 2020/7/7 17:57
 * @DESCRIPTION 定制的广播 以后都放在这
 */
public class FlyscaleReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(TextUtils.equals(action,BRConstant.ACTION_USB_TOGGLE)){
            BaseApplication.sFlyscaleManager.setAdbEnabled(!BaseApplication.sFlyscaleManager.isAdbEnabled());
        }
    }
}
