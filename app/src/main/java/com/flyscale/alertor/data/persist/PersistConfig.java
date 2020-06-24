package com.flyscale.alertor.data.persist;

import android.flyscale.FlyscaleManager;
import android.text.TextUtils;

import com.flyscale.alertor.base.BaseApplication;
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
    String alarmNum = "16886119";
    String specialNum = "110";
    //是否接受其他号码呼入
    boolean isAcceptOtherNum = false;
    //语音报警优先，IP报警优先
    boolean isIpAlarmFirst = true;
    String newIp = null;
    int newPort = -1;
    boolean isMute = BaseApplication.sFlyscaleManager.getMuteState().equals("1");
    //修改报警灯常亮时间：    IPALARMLED=08:30,20:30;            --  设为00:00,00:00 表示常亮
    String alarmLedOnTime = "00:00";
    String alarmOffOffTime = "00:00";
    String firstLogin = "";
    //是否可以报警
    boolean isCanAlarm = true;


    public boolean isCanAlarm() {
        return isCanAlarm;
    }

    public void setCanAlarm(boolean canAlarm) {
        isCanAlarm = canAlarm;
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

    public String getAlarmOffOffTime() {
        return alarmOffOffTime;
    }

    public void setAlarmOffOffTime(String alarmOffOffTime) {
        this.alarmOffOffTime = alarmOffOffTime;
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

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
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

    public static void saveCanAlarm(boolean can){
        PersistConfig persistConfig = findConfig();
        persistConfig.setCanAlarm(can);
        persistConfig.save();
    }


    public static PersistConfig saveAlarmLedTime(String start,String end){
        PersistConfig persistConfig = findConfig();
        persistConfig.setAlarmLedOnTime(start);
        persistConfig.setAlarmOffOffTime(end);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveIsMute(boolean isMute){
        PersistConfig persistConfig = findConfig();
        persistConfig.setMute(isMute);
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

    public static PersistConfig saveIsAccpetOtherNum(boolean accept){
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


}
