package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.Constants;
import com.flyscale.alertor.helper.AlarmManagerUtil;
import com.flyscale.alertor.helper.DateHelper;
import com.flyscale.alertor.media.MusicPlayer;

import java.util.Timer;
import java.util.TimerTask;

import static android.media.AudioManager.FLAG_SHOW_UI;
import static android.media.AudioManager.STREAM_MUSIC;


/**
 * Created by liChang on 2021/2/8
 */
public class TimingPlanReceiver extends BroadcastReceiver {

    private static final String TAG = "TimingPlanReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("FLYSCALE_MUSIC_STOP")) {
            MusicPlayer.getInstance().reset(true);
            return;
        }
        Log.i(TAG, "onReceive: 开始定时任务...");
        if (TextUtils.isEmpty(String.valueOf(intent.getIntExtra("week", 0)))) {
            return;
        }
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

        Log.i(TAG, "onReceive: address=" + address);
        //设置音量
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(STREAM_MUSIC, Integer.parseInt(voice), FLAG_SHOW_UI);

        //播放文件
        Log.i(TAG, "onReceive: 播放文件, end时间结束");
        String path = Constants.FilePath.FILE_NORMAL;
        Log.i(TAG, "onReceive: 播放" + (path + fileName));
        MusicPlayer.getInstance().playBefore(path + fileName, beforePlay, end, address);

        String time = DateHelper.StringTimeHms();
        long persist = DateHelper.getFMDuration(time, end);

//        Intent intent1 = new Intent("FLYSCALE_MUSIC_STOP");
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//
//            }
//        }, persist);
        AlarmManagerUtil.getInstance(context).AlarmManagerWorkOnOthers(requestCode, week, start, end, fileName, voice
                , beforePlay, address);
    }

}