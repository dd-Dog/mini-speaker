package com.flyscale.alertor.base;

import android.app.Application;
import android.content.Context;

import com.flyscale.alertor.helper.SoundPoolHelper;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 9:28
 * @DESCRIPTION 暂无
 */
public class BaseApplication extends Application {

    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;

        SoundPoolHelper.getInstance().init(this);
    }
}
