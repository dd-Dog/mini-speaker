package com.flyscale.alertor.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.eventBusManager.EventMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 16:36
 * @DESCRIPTION 暂无
 */
public class BaseService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusMain(EventMessage message){

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventBusBack(EventMessage message){

    }
}
