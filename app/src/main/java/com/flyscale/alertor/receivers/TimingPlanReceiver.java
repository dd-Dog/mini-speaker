package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.flyscale.alertor.Constants;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.helper.AlarmManagerUtil;
import com.flyscale.alertor.helper.DateHelper;
import com.flyscale.alertor.helper.DateUtil;
import com.flyscale.alertor.media.MusicPlayer;
import com.flyscale.alertor.services.AlarmService;

import java.util.Timer;

import static android.media.AudioManager.FLAG_SHOW_UI;
import static android.media.AudioManager.STREAM_MUSIC;


/**
 * Created by liChang on 2021/2/8
 */
public class TimingPlanReceiver extends BroadcastReceiver {

    private static final String TAG = "TimingPlanReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: 周几" + intent.getIntExtra("week", 0));
        Log.i(TAG, "onReceive: 取消" + intent.getIntExtra("cancel", 0));
        if (intent.getIntExtra("cancel", 0) != 0) {
            Log.i(TAG, "onReceive: 取消music");
            MusicPlayer.getInstance().reset(false);
            PersistConfig.saveTiming("", 0, true, "", "");//重置
        } else if (intent.getIntExtra("week", 0) != 0) {
            Log.i(TAG, "onReceive: 开始定时任务...");
            final String start = intent.getStringExtra("start");
            String end = intent.getStringExtra("end");
            String fileName = intent.getStringExtra("fileName");
            String voice = intent.getStringExtra("voice");
            boolean beforePlay = intent.getBooleanExtra("beforePlay", true);
            int week = intent.getIntExtra("week", 0);
            int requestCode = intent.getIntExtra("requestCode", 0);
            long address = intent.getLongExtra("address", 0);
            Log.i(TAG, "onReceive: 今天是周" + week);
            // TODO: 2021/2/20 这里写具体实现
            //重复定时任务
            AlarmManagerUtil.getInstance(BaseApplication.sContext).AlarmManagerWorkOnOthers(requestCode, week, start, end,
                    fileName, voice, beforePlay, address);
            Log.i(TAG, "onReceive: address=" + address);

            String time = DateHelper.StringTimeHms();
            long persist = DateHelper.getFMDuration(time, end);
            Log.i(TAG, "onReceive: 播放文件, time当前时间" + time);
            Log.i(TAG, "onReceive: 播放文件, persist时间结束" + persist);
            if (persist > 0 && week == DateUtil.getDayOfWeek()) {
                PersistConfig.saveTiming(fileName, address, beforePlay, voice, end);
                AlarmService.remotePlayMP3();
            } else {
                Log.i(TAG, "onReceive: 星期" + week + "当前时间为" + time);
                Log.i(TAG, "onReceive: 星期" + week + "开始时间为" + start);
            }
        }
    }
}
