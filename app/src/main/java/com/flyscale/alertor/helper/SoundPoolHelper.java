package com.flyscale.alertor.helper;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.flyscale.alertor.R;

/**
 * 作者 ： 高鹤泉
 * 时间 ： 2018/10/25 下午3:07
 * 1.SoundPool适合 短且对反应速度比较高 的情况（游戏音效或按键声等），文件大小一般控制在几十K到几百K，最好不超过1M，
 * 2.SoundPool 可以与MediaPlayer同时播放，SoundPool也可以同时播放多个声音；
 * 3.SoundPool 最终编解码实现与MediaPlayer相同；
 * 4.MediaPlayer只能同时播放一个声音，加载文件有一定的时间，适合文件比较大，响应时间要是那种不是非常高的场景
 */
public class SoundPoolHelper {

    private static final SoundPoolHelper ourInstance = new SoundPoolHelper();
    SoundPool mSoundPool;
    AudioManager mAudioManager;

    public static int WELCOME_USE;
    public static int CHECKOUT_SIM;
    public static int WORK_WRONG;
    boolean isPauseMusic = false;
    int[] mRawIds = {R.raw.v1_platform_disconnected,R.raw.v2_welcome,R.raw.v3_platform_connect_success,R.raw.v4_xinjiang_telecom_welcome
                    ,R.raw.v5_working_state_wrong,R.raw.v6_battery_low,R.raw.v7_check_simcard_please,R.raw.v8_send_alarm_success
                    ,R.raw.v9_platform_connect_success};



    public static SoundPoolHelper getInstance() {
        return ourInstance;
    }

    private SoundPoolHelper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder builder;
            builder = new SoundPool.Builder();
            builder.setMaxStreams(mRawIds.length);
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            builder.setAudioAttributes(attrBuilder.build());
            mSoundPool = builder.build();
        }else {
            //第一个参数：int maxStreams：SoundPool对象的最大并发流数
            //第二个参数：int streamType：AudioManager中描述的音频流类型
            //第三个参数：int srcQuality：采样率转换器的质量。 目前没有效果。 使用0作为默认值。
            mSoundPool = new SoundPool(mRawIds.length,AudioManager.STREAM_MUSIC,0);
        }
    }

    /**
     * application 初始化的时候使用
     */
    public void init(Context context){
        WELCOME_USE = mSoundPool.load(context, R.raw.v2_welcome,1);
        CHECKOUT_SIM = mSoundPool.load(context,R.raw.v7_check_simcard_please,1);
        WORK_WRONG = mSoundPool.load(context,R.raw.v5_working_state_wrong,1);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 播放内置的音频
     * @param type
     */
    public void play(int type){
        //第一个参数soundID
        //第二个参数leftVolume为左侧音量值（范围= 0.0到1.0）
        //第三个参数rightVolume为右的音量值（范围= 0.0到1.0）
        //第四个参数priority 为流的优先级，值越大优先级高，影响当同时播放数量超出了最大支持数时SoundPool对该流的处理
        //第五个参数loop 为音频重复播放次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次
        //第六个参数 rate为播放的速率，范围0.5-2.0(0.5为一半速率，1.0为正常速率，2.0为两倍速率)
        mSoundPool.play(type,1,1,10,0,1);
    }


    public SoundPool getSoundPool() {
        return mSoundPool;
    }

    //如果是播放状态，抢焦点，音乐播放就会暂停
    public void stopAudio(){
        if(mAudioManager.isMusicActive()){
            mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            isPauseMusic = true;
        }
    }

    //
    //不需要时释放焦点，音乐播放就会继续
    public void releaseAudio(){
        if(isPauseMusic){
            mAudioManager.abandonAudioFocus(null);
            isPauseMusic = false;
        }
    }

    /**
     * 是否正在播放
     * @return
     */
    public boolean isActive(){
        return mAudioManager.isMusicActive();
    }
}
