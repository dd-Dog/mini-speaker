package com.flyscale.alertor.data.persist;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

/**
 * @author 高鹤泉
 * @TIME 2020/6/19 18:05
 * @DESCRIPTION 暂无
 */
public class PersistConfig extends LitePalSupport {
    String ip = "202.100.190.14";
    int port = 9988;
    String alarmNum = "16886119";
    String specialNum = "110";

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getAlarmNum() {
        return alarmNum;
    }

    public void setAlarmNum(String alarmNum) {
        this.alarmNum = alarmNum;
    }

    public String getSpecialNum() {
        return specialNum;
    }

    public void setSpecialNum(String specialNum) {
        this.specialNum = specialNum;
    }


    public static PersistConfig findConfig(){
        PersistConfig persistConfig = LitePal.findFirst(PersistConfig.class);
        if(persistConfig == null){
            persistConfig = new PersistConfig();
            persistConfig.save();
        }
        return persistConfig;
    }

    public static PersistConfig saveIp(String ip){
        PersistConfig persistConfig = findConfig();
        persistConfig.setIp(ip);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig savePort(int port){
        PersistConfig persistConfig = findConfig();
        persistConfig.setPort(port);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveAlarmNum(String num){
        PersistConfig persistConfig = findConfig();
        persistConfig.setAlarmNum(num);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveSpecialNum(String num){
        PersistConfig persistConfig = findConfig();
        persistConfig.setSpecialNum(num);
        persistConfig.save();
        return persistConfig;
    }

}
