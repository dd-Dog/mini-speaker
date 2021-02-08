package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by liChang on 2021/2/8
 */
public class TimingPlanReceiver extends BroadcastReceiver {

    private static final String TAG = "TimingPlanReceiver";
    private static final int CMD_DEVICE_VOLUME = 0;
    private static final int CMD_DEVICE_BRIGHTNESS = 1;
    private static final int CMD_DEVICE_POWER = 2;

    private static CallBackExecuteCmd callBackExecuteCmd;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.isEmpty(intent.getStringExtra("param"))) {
            return;
        }
        switch (intent.getIntExtra("cmd", 0)) {
            case CMD_DEVICE_VOLUME:
                String volume = intent.getStringExtra("param");
//                if (callBackExecuteCmd != null) {
//                    callBackExecuteCmd.callbackTimingVolume(volume);
//                    Log.i(TAG, "onReceive: " + volume);
//                }
                Log.i(TAG, "onReceive: ******" + volume);
                break;
            case CMD_DEVICE_BRIGHTNESS:
                int brightness = Integer.parseInt(intent.getStringExtra("param"));
                if (callBackExecuteCmd != null) {
                    callBackExecuteCmd.callbackTimingBrightness(brightness);
                    Log.i(TAG, "亮度定时方案执行成功param：" + brightness);
                }
                break;
            case CMD_DEVICE_POWER:
                String power = intent.getStringExtra("param");
//                if (callBackExecuteCmd != null) {
//                    callBackExecuteCmd.callbackTimingPowerSwitch(power);
//                    Log.i(TAG, "电源定时方案执行成功param：" + power);
//                }
                Log.i(TAG, "onReceive: *******" + power);
                break;
        }
    }

    public interface CallBackExecuteCmd {

        void callbackTimingVolume(String param);

        void callbackTimingBrightness(int param);

        void callbackTimingPowerSwitch(String param);

    }

    /**
     * 给接口赋初始值
     *
     * @param executeCmd 接口初始值
     */
    public static void setOnCallBackExecuteCmd(CallBackExecuteCmd executeCmd) {
        callBackExecuteCmd = executeCmd;
    }

}