package com.flyscale.alertor.devicestate;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.flyscale.alertor.Constants;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.helper.AlarmManagerUtil;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.DateHelper;
import com.flyscale.alertor.media.MusicPlayer;

import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import static android.media.AudioManager.FLAG_SHOW_UI;
import static android.media.AudioManager.STREAM_MUSIC;
import static com.flyscale.alertor.netty.NettyHandler.DownLoadAndDelete;

public class RemotePlayMP3State implements IState {
    /**
     * 状态触发优先级
     */
    public static final int PRIORITY = 3;
    private static StateManager stateManager;

    public RemotePlayMP3State(StateManager stateManager) {
        RemotePlayMP3State.stateManager = stateManager;
    }

    @Override
    public void start() {
        String fileName = PersistConfig.findConfig().getFileName();
        long size = PersistConfig.findConfig().getSize();
        int playTimes = PersistConfig.findConfig().getPlayTimes();
        int remote = PersistConfig.findConfig().getRemote();

        boolean beforePlay = PersistConfig.findConfig().isBeforePlay();
        long address = PersistConfig.findConfig().getAddress();
        String voice = PersistConfig.findConfig().getVoice();
        String end = PersistConfig.findConfig().getEnd();

        if (remote == 3) {
            DDLog.i("优先级为三 " + fileName);
            DownLoadAndDelete(fileName, size, playTimes);
        } else if (remote == 4) {
            String time = DateHelper.StringTimeHms();
            long persist = DateHelper.getFMDuration(time, end);
            //设置音量
            AudioManager am = (AudioManager) BaseApplication.sContext.getSystemService(Context.AUDIO_SERVICE);
            am.setStreamVolume(STREAM_MUSIC, Integer.parseInt(voice), FLAG_SHOW_UI);
            //播放文件
            String path = Constants.FilePath.FILE_NORMAL;
            Log.i("TAG", "onReceive: 播放" + (path + fileName));
            MusicPlayer.getInstance().playBefore(path + fileName, beforePlay, address);
            //持续persist时间
            AlarmManagerUtil.getInstance(BaseApplication.sContext).cancelMusic(persist);
        } else stops();
    }

    @Override
    public void pause() {
        DDLog.i("优先级为三的暂停方法");
        MusicPlayer.getInstance().pause(true);

    }

    @Override
    public void stop() {
        stateManager.setStateByPriority(PRIORITY + 1, false);
    }

    public static void stops() {
        stateManager.setStateByPriority(PRIORITY + 1, false);
    }

    @Override
    public int getPriority() {
        return PRIORITY;
    }
}
