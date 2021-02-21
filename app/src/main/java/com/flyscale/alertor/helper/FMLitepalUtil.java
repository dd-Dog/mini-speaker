package com.flyscale.alertor.helper;

import com.flyscale.alertor.data.packet.TcpPacketFactory;
import com.flyscale.alertor.data.persist.FMLitepalBean;

import org.litepal.crud.LitePalSupport;


public class FMLitepalUtil extends LitePalSupport {


    /**
     *
     * 初始化fm信息数据库
     * */
    public static void init(){
        int n = 1;
        for (long i = 0x00000100L; i <= 0x0000011FL; i++) {
                FMLitepalBean litepalBean = new FMLitepalBean();
                litepalBean.setName("FM" + n);
                litepalBean.setStartDate("0");
                litepalBean.setFreq("0.0");
                litepalBean.setStartTime("00:00");
                litepalBean.setEndTime("00:00");
                litepalBean.setVolume("0");
                litepalBean.save();
                n++;
        }
    }

    /**
     * 获取当前修改的行
     * */
    public static int getCorrectLine(long address){
        for (int i=1; i <= 32; i++){
            if (TcpPacketFactory.FM_SHOW_LIST.get(i-1) == address){
                return i;
            }
        }
        return 0;
    }

    /**
     * 修改指定行fm信息
     * */
    public static void updateLine(long address,String data){
        FMLitepalBean litepalBean = new FMLitepalBean();
        litepalBean.setFreq(getFreq(data));
        litepalBean.setStartDate(getWeeklyRecord(data));
        litepalBean.setStartTime(getStartFMTime(data));
        litepalBean.setEndTime(getEndFMTime(data));
        litepalBean.setVolume(getVolume(data));
        litepalBean.updateAll("name = ?","FM" + getCorrectLine(address));
    }


    /**
     * 获取所需频道
     * */
    public static String getFreq(String data){
        String freq = data.substring(7,14);
        return freq;
    }

    /**
     * 获取开始播放时间
     * */
    public static String getStartFMTime(String data){
        String startTime = data.substring(15,21);
        return startTime;
    }

    /**
     * 获取结束播放时间
     * */
    public static String getEndFMTime(String data){
        String endTime = data.substring(22,28);
        return endTime;
    }

    /**
     * 转化拼接字符串  调整时间表达
     * HH:mm:ss
     * */
    public static String splicingString(String string){
        String hour = string.substring(0,2);
        String min = string.substring(2,4);
        String second = string.substring(4);
        String time = hour + ":" + min + ":" + second;
        return  time;
    }

    /**
     * 获取单周播放信息
     * */
    public static String getWeeklyRecord(String data){
        String weeklyRecord = data.substring(0,3);
        return weeklyRecord;
    }

    /**
     * 获取播放音量
     * */
    public static String getVolume(String data){
        String volume = data.substring(29,30);
        return volume;
    }

}
