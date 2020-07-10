package com.flyscale.alertor.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.flyscale.FlyscaleManager;
import android.util.Log;

import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistWhite;
import com.flyscale.alertor.helper.SoundPoolHelper;
import com.flyscale.alertor.led.LedInstance;
import com.flyscale.alertor.netty.AlarmHelper;

import org.litepal.LitePal;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 9:28
 * @DESCRIPTION 暂无
 */
public class BaseApplication extends Application {

    public static Context sContext;
    public static FlyscaleManager sFlyscaleManager;

    @SuppressLint("WrongConstant")
    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;

        SoundPoolHelper.getInstance().init(this);
        LitePal.initialize(this);
        AlarmHelper.getInstance();

        sFlyscaleManager = (FlyscaleManager) getSystemService(FlyscaleManager.FLYSCALE_SERVICE);
        sFlyscaleManager.setExternalAlarmStatus(0);
        if(sFlyscaleManager.getAdapterState().equals("1")){
            LedInstance.getInstance().showChargeLed();
        }else {
            LedInstance.getInstance().offChargeLed();
        }
        if(sFlyscaleManager.getAlarmLedState().equals("0")){
            LedInstance.getInstance().cancelBlinkOffAlarmLed();
        }else {
            LedInstance.getInstance().cancelBlinkShowAlarmLed();
        }

        //初始化白名单
        PersistWhite.findList();
    }
}
