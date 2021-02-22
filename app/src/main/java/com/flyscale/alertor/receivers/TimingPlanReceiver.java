package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.data.packet.CMD;
import com.flyscale.alertor.data.packet.TcpPacket;
import com.flyscale.alertor.helper.AlarmManagerUtil;
import com.flyscale.alertor.helper.FillZeroUtil;
import com.flyscale.alertor.media.MusicPlayer;
import com.flyscale.alertor.media.Player;
import com.flyscale.alertor.netty.NettyHelper;

import static android.media.AudioManager.FLAG_SHOW_UI;
import static android.media.AudioManager.STREAM_MUSIC;


/**
 * Created by liChang on 2021/2/8
 */
public class TimingPlanReceiver extends BroadcastReceiver {

    private static final String TAG = "TimingPlanReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.isEmpty(String.valueOf(intent.getIntExtra("week", 0)))) {
            return;
        }
        String start = intent.getStringExtra("start");
        String end = intent.getStringExtra("end");
        String fileName = intent.getStringExtra("fileName");
        String voice = intent.getStringExtra("voice");
        boolean beforePlay = intent.getBooleanExtra("beforePlay", true);
        int week = intent.getIntExtra("week", 0);
        int requestCode = intent.getIntExtra("requestCode", 0);
        String program = intent.getStringExtra("program");
        Log.i(TAG, "onReceive: 今天是周" + week);
        // TODO: 2021/2/20 这里写具体实现

        //设置音量
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(STREAM_MUSIC, Integer.parseInt(voice), FLAG_SHOW_UI);

        //播放文件
        Log.i(TAG, "onReceive: 播放文件, end时间结束");
        String MEDIA_PATH = "/mnt/sdcard/flyscale/media/normal/";
        MusicPlayer.getInstance().playBefore(MEDIA_PATH + fileName, beforePlay, end);

        if (true) {
            //播放正常
            NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE, 0x00000200L + Integer.parseInt(program),
                    FillZeroUtil.getString("0/", 32)));
        } else {
            //播放错误
            NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE, 0x00000200L + Integer.parseInt(program),
                    FillZeroUtil.getString("-100/", 32)));
        }

        AlarmManagerUtil.getInstance(context).AlarmManagerWorkOnOthers(requestCode, week, start, end, fileName, voice
                , beforePlay, program);
    }

}