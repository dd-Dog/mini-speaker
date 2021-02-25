package com.flyscale.alertor.data.persist;


import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * Created by liChang on 2021/2/20
 */
public class PersistClock extends LitePalSupport {

    public int week, requestCode;
    String startTime, endTime, voice;
    long address;
    boolean before;

    public long getAddress() {
        return address;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public void setAddress(long address) {
        this.address = address;
    }

    public boolean isBefore() {
        return before;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public void setBefore(boolean before) {
        this.before = before;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public int getWeek() {
        return week;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getVoice() {
        return voice;
    }

    /**
     * 保存定时播放音频（根据周几播放）
     *
     * @param week
     * @param startTime
     * @param endTime
     * @param voice
     * @param before
     * @return
     */
    public static PersistClock saveAlarm(int week, String startTime, String endTime, String voice, boolean before,
                                         long address, int requestCode) {
        PersistClock persistClock = new PersistClock();
        //避免插入重复数据
        List<PersistClock> songs = LitePal.where("week = ? and address = ?" ,
                String.valueOf(week), String.valueOf(address)).find(PersistClock.class);
        if (songs == null || songs.size() == 0) {
        } else {
            for (int i = 0; i < songs.size(); i++) {
                songs.get(i).delete();
            }
        }
        persistClock.setWeek(week);
        persistClock.setStartTime(startTime);
        persistClock.setEndTime(endTime);
        persistClock.setVoice(voice);
        persistClock.setBefore(before);
        persistClock.setAddress(address);
        persistClock.setRequestCode(requestCode);
        persistClock.save();
        return persistClock;
    }


}
