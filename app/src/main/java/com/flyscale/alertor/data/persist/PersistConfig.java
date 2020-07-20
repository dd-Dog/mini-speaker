package com.flyscale.alertor.data.persist;

import android.text.TextUtils;
import com.flyscale.alertor.helper.DateHelper;

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
    String alarmNum = "099116886119";
    String specialNum = "110";
    //是否接受其他号码呼入
    boolean isAcceptOtherNum = false;
    //语音报警优先，IP报警优先
    boolean isIpAlarmFirst = true;
    String newIp = null;
    int newPort = -1;
    //修改报警灯常亮时间：    IPALARMLED=08:30,20:30;            --  设为00:00,00:00 表示常亮
    String alarmLedOnTime = "00:00";
    String alarmLedOffTime = "00:00";
    String firstLogin = "";
    //是否布防 布防状态门磁和红外
    boolean isArming = true;
    //报警灯常亮
    boolean isAlarmOn = true;
    boolean isDefaultAlarmOn = true;

    public boolean isDefaultAlarmOn() {
        return isDefaultAlarmOn;
    }

    public void setDefaultAlarmOn(boolean defaultAlarmOn) {
        isDefaultAlarmOn = defaultAlarmOn;
    }

    public boolean isAlarmOn() {
        if(isDefaultAlarmOn){
            return isAlarmOn;
        }else {
            return !isAlarmOn;
        }
    }

    public void setAlarmOn(boolean alarmOn) {
        isAlarmOn = alarmOn;
    }

    public boolean isArming() {
        return isArming;
    }

    public void setArming(boolean arming) {
        isArming = arming;
    }

    public String getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(String firstLogin) {
        this.firstLogin = firstLogin;
    }

    public boolean isAcceptOtherNum() {
        return isAcceptOtherNum;
    }

    public void setAcceptOtherNum(boolean acceptOtherNum) {
        isAcceptOtherNum = acceptOtherNum;
    }

    public String getAlarmLedOnTime() {
        return alarmLedOnTime;
    }

    public void setAlarmLedOnTime(String alarmLedOnTime) {
        this.alarmLedOnTime = alarmLedOnTime;
    }

    public String getAlarmLedOffTime() {
        return alarmLedOffTime;
    }

    public void setAlarmLedOffTime(String alarmLedOffTime) {
        this.alarmLedOffTime = alarmLedOffTime;
    }

    public String getIp() {
        if(!TextUtils.isEmpty(newIp)){
            return newIp;
        }
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        if(newPort != -1){
            return newPort;
        }
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isIpAlarmFirst() {
        return isIpAlarmFirst;
    }

    public void setIpAlarmFirst(boolean ipAlarmFirst) {
        isIpAlarmFirst = ipAlarmFirst;
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

    public String getNewIp() {
        return newIp;
    }

    public void setNewIp(String newIp) {
        this.newIp = newIp;
    }

    public int getNewPort() {
        return newPort;
    }

    public void setNewPort(int newPort) {
        this.newPort = newPort;
    }

    public static PersistConfig findConfig(){
        PersistConfig persistConfig = LitePal.findFirst(PersistConfig.class);
        if(persistConfig == null){
            persistConfig = new PersistConfig();
            persistConfig.save();
        }
        return persistConfig;
    }

    public static void saveFirstLoginTime(long time){
        if(TextUtils.isEmpty(findConfig().getFirstLogin())){
            String tempTime = DateHelper.longToString(time,DateHelper.yyyy_MM_dd_hh_mm_ss);
            PersistConfig persistConfig = findConfig();
            persistConfig.setFirstLogin(tempTime);
            persistConfig.save();
        }
    }

    public static void saveIsArming(boolean arming){
        PersistConfig persistConfig = findConfig();
        persistConfig.setArming(arming);
        persistConfig.save();
    }


    public static PersistConfig saveAlarmLedTime(String start,String end){
        PersistConfig persistConfig = findConfig();
        persistConfig.setAlarmLedOnTime(start);
        persistConfig.setAlarmLedOffTime(end);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveNewIp(String ip,int port){
        PersistConfig persistConfig = findConfig();
        persistConfig.setNewIp(ip);
        persistConfig.setNewPort(port);
        persistConfig.save();
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

    public static PersistConfig saveIsAcceptOtherNum(boolean accept){
        PersistConfig persistConfig = findConfig();
        persistConfig.setAcceptOtherNum(accept);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveIsIpAlarmFirst(boolean ip){
        PersistConfig persistConfig = findConfig();
        persistConfig.setIpAlarmFirst(ip);
        persistConfig.save();
        return persistConfig;
    }

    public static void saveAlarmOn(boolean on){
        PersistConfig persistConfig = findConfig();
        persistConfig.setAlarmOn(on);
        persistConfig.save();
    }

    public static void saveDefaultAlarmOn(boolean on){
        PersistConfig persistConfig = findConfig();
        persistConfig.setDefaultAlarmOn(on);
        persistConfig.save();
    }


    @Override
    public String toString() {
        return "PersistConfig{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", alarmNum='" + alarmNum + '\'' +
                ", specialNum='" + specialNum + '\'' +
                ", isAcceptOtherNum=" + isAcceptOtherNum +
                ", isIpAlarmFirst=" + isIpAlarmFirst +
                ", newIp='" + newIp + '\'' +
                ", newPort=" + newPort +
                ", alarmLedOnTime='" + alarmLedOnTime + '\'' +
                ", alarmLedOffTime='" + alarmLedOffTime + '\'' +
                ", firstLogin='" + firstLogin + '\'' +
                ", isArming=" + isArming +
                '}';
    }
}
