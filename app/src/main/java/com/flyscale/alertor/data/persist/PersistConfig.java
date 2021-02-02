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
    //报警灯常亮是否取反的标志  true 维持现状 false 取反
    boolean isDefaultAlarmOn = true;

    /*bianjb-服务器地址，端口信息--开始*/
    /******************飞图FTP测试服务器*******************/
    String ftpHostNameDebugFly = "";
    String ftpHostIPDebugFly = "192.168.1.104";
    int ftpHostPortDebugFly = 22;
    String ftpUsernameFly = "flyscale";
    String ftpPasswordFly = "fly123";

    /******************客户测试服务器*******************/
    String ftpHostNameDebug = "ftp3.xjxlb.com";
    String ftpHostIPDebug = "222.82.238.168";
    int ftpHostPortDebug = 13001;
    String ftpUsernameDebug = "";
    String ftpPasswordDebug = "";

    String tcpHostNameDebug1 = "xlb1.xjxlb.com";//两个域名使用同一IP，默认使用1
    String tcpHostNameDebug2 = "xlb1.xj-ict.com";
    String tcpHostIPDebug = "202.100.190.107";
    int tcpPortDebug = 58005;

    /******************客户正式服务器*******************/
    String ftpHostNameRelease = "ftp1.xjxlb.com";
    String ftpHostIPRelease = "202.100.190.107";
    int ftpHostPortRelease = 58000;
    String ftpUsernameRelease = "";
    String ftpPasswordRelease = "";

    String tcpHostNameRelease1 = "xlb3.xjxlb.com";//两个域名使用同一IP，默认使用1
    String tcpHostNameRelease2 = "xlb3.xj-ict.com";
    String tcpHostIPRelease = "47.104.3.240";
    int tcpPortRelease = 58005;

    public static PersistConfig saveFtpHostNameRelease(String hostName) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setFtpHostNameRelease(hostName);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveFtpHostIPRelease(String ip) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setFtpHostIPRelease(ip);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveFtpHostPortRelease(int port) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setFtpHostPortRelease(port);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveFtpUsernameRelease(String username) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setFtpUsernameRelease(username);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveFtpPasswordRelease(String password) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setFtpPasswordRelease(password);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveTcpHostNameRelease1(String tcpHostname) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setTcpHostNameRelease1(tcpHostname);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveTcpHostNameRelease2(String tcpHostname) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setTcpHostNameRelease2(tcpHostname);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveTcpHostIPRelease(String tcpHostIP) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setTcpHostIPRelease(tcpHostIP);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveTcpPortRelease(int tcpPort) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setTcpPortRelease(tcpPort);
        persistConfig.save();
        return persistConfig;
    }
    /*bianjb-服务器地址，端口信息--结束*/

    public boolean isDefaultAlarmOn() {
        return isDefaultAlarmOn;
    }

    public void setDefaultAlarmOn(boolean defaultAlarmOn) {
        isDefaultAlarmOn = defaultAlarmOn;
    }

    public boolean isAlarmOn() {
        if (isDefaultAlarmOn) {
            return isAlarmOn;
        } else {
            return !isAlarmOn;
        }
    }

    public String getTcpHostNameDebug1() {
        return tcpHostNameDebug1;
    }

    public PersistConfig setTcpHostNameDebug1(String tcpHostNameDebug1) {
        this.tcpHostNameDebug1 = tcpHostNameDebug1;
        return this;
    }

    public String getTcpHostNameDebug2() {
        return tcpHostNameDebug2;
    }

    public PersistConfig setTcpHostNameDebug2(String tcpHostNameDebug2) {
        this.tcpHostNameDebug2 = tcpHostNameDebug2;
        return this;
    }

    public String getTcpHostIPDebug() {
        return tcpHostIPDebug;
    }

    public PersistConfig setTcpHostIPDebug(String tcpHostIPDebug) {
        this.tcpHostIPDebug = tcpHostIPDebug;
        return this;
    }

    public int getTcpPortDebug() {
        return tcpPortDebug;
    }

    public PersistConfig setTcpPortDebug(int tcpPortDebug) {
        this.tcpPortDebug = tcpPortDebug;
        return this;
    }

    public String getTcpHostNameRelease1() {
        return tcpHostNameRelease1;
    }

    public PersistConfig setTcpHostNameRelease1(String tcpHostNameRelease1) {
        this.tcpHostNameRelease1 = tcpHostNameRelease1;
        return this;
    }

    public String getTcpHostNameRelease2() {
        return tcpHostNameRelease2;
    }

    public PersistConfig setTcpHostNameRelease2(String tcpHostNameRelease2) {
        this.tcpHostNameRelease2 = tcpHostNameRelease2;
        return this;
    }

    public String getTcpHostIPRelease() {
        return tcpHostIPRelease;
    }

    public PersistConfig setTcpHostIPRelease(String tcpHostIPRelease) {
        this.tcpHostIPRelease = tcpHostIPRelease;
        return this;
    }

    public int getTcpPortRelease() {
        return tcpPortRelease;
    }

    public PersistConfig setTcpPortRelease(int tcpPortRelease) {
        this.tcpPortRelease = tcpPortRelease;
        return this;
    }

    public String getFtpHostNameDebugFly() {
        return ftpHostNameDebugFly;
    }

    public PersistConfig setFtpHostNameDebugFly(String ftpHostNameDebugFly) {
        this.ftpHostNameDebugFly = ftpHostNameDebugFly;
        return this;
    }

    public String getFtpHostIPDebugFly() {
        return ftpHostIPDebugFly;
    }

    public PersistConfig setFtpHostIPDebugFly(String ftpHostIPDebugFly) {
        this.ftpHostIPDebugFly = ftpHostIPDebugFly;
        return this;
    }

    public int getFtpHostPortDebugFly() {
        return ftpHostPortDebugFly;
    }

    public PersistConfig setFtpHostPortDebugFly(int ftpHostPortDebugFly) {
        this.ftpHostPortDebugFly = ftpHostPortDebugFly;
        return this;
    }

    public String getFtpUsernameFly() {
        return ftpUsernameFly;
    }

    public PersistConfig setFtpUsernameFly(String ftpUsernameFly) {
        this.ftpUsernameFly = ftpUsernameFly;
        return this;
    }

    public String getFtpPasswordFly() {
        return ftpPasswordFly;
    }

    public PersistConfig setFtpPasswordFly(String ftpPasswordFly) {
        this.ftpPasswordFly = ftpPasswordFly;
        return this;
    }

    public String getFtpHostNameDebug() {
        return ftpHostNameDebug;
    }

    public PersistConfig setFtpHostNameDebug(String ftpHostNameDebug) {
        this.ftpHostNameDebug = ftpHostNameDebug;
        return this;
    }

    public String getFtpHostIPDebug() {
        return ftpHostIPDebug;
    }

    public PersistConfig setFtpHostIPDebug(String ftpHostIPDebug) {
        this.ftpHostIPDebug = ftpHostIPDebug;
        return this;
    }

    public int getFtpHostPortDebug() {
        return ftpHostPortDebug;
    }

    public PersistConfig setFtpHostPortDebug(int ftpHostPortDebug) {
        this.ftpHostPortDebug = ftpHostPortDebug;
        return this;
    }

    public String getFtpUsernameDebug() {
        return ftpUsernameDebug;
    }

    public PersistConfig setFtpUsernameDebug(String ftpUsernameDebug) {
        this.ftpUsernameDebug = ftpUsernameDebug;
        return this;
    }

    public String getFtpPasswordDebug() {
        return ftpPasswordDebug;
    }

    public PersistConfig setFtpPasswordDebug(String ftpPasswordDebug) {
        this.ftpPasswordDebug = ftpPasswordDebug;
        return this;
    }

    public String getFtpHostNameRelease() {
        return ftpHostNameRelease;
    }

    public PersistConfig setFtpHostNameRelease(String ftpHostNameRelease) {
        this.ftpHostNameRelease = ftpHostNameRelease;
        return this;
    }

    public String getFtpHostIPRelease() {
        return ftpHostIPRelease;
    }

    public PersistConfig setFtpHostIPRelease(String ftpHostIPRelease) {
        this.ftpHostIPRelease = ftpHostIPRelease;
        return this;
    }

    public int getFtpHostPortRelease() {
        return ftpHostPortRelease;
    }

    public PersistConfig setFtpHostPortRelease(int ftpHostPortRelease) {
        this.ftpHostPortRelease = ftpHostPortRelease;
        return this;
    }

    public String getFtpUsernameRelease() {
        return ftpUsernameRelease;
    }

    public PersistConfig setFtpUsernameRelease(String ftpUsernameRelease) {
        this.ftpUsernameRelease = ftpUsernameRelease;
        return this;
    }

    public String getFtpPasswordRelease() {
        return ftpPasswordRelease;
    }

    public PersistConfig setFtpPasswordRelease(String ftpPasswordRelease) {
        this.ftpPasswordRelease = ftpPasswordRelease;
        return this;
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
        if (!TextUtils.isEmpty(newIp)) {
            return newIp;
        }
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        if (newPort != -1) {
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

    public static PersistConfig findConfig() {
        PersistConfig persistConfig = LitePal.findFirst(PersistConfig.class);
        if (persistConfig == null) {
            persistConfig = new PersistConfig();
            persistConfig.save();
        }
        return persistConfig;
    }

    public static void saveFirstLoginTime(long time) {
        if (TextUtils.isEmpty(findConfig().getFirstLogin())) {
            String tempTime = DateHelper.longToString(time, DateHelper.yyyy_MM_dd_hh_mm_ss);
            PersistConfig persistConfig = findConfig();
            persistConfig.setFirstLogin(tempTime);
            persistConfig.save();
        }
    }

    public static void saveIsArming(boolean arming) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setArming(arming);
        persistConfig.save();
    }


    public static PersistConfig saveAlarmLedTime(String start, String end) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setAlarmLedOnTime(start);
        persistConfig.setAlarmLedOffTime(end);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveNewIp(String ip, int port) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setNewIp(ip);
        persistConfig.setNewPort(port);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveIp(String ip) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setIp(ip);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig savePort(int port) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setPort(port);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveAlarmNum(String num) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setAlarmNum(num);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveSpecialNum(String num) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setSpecialNum(num);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveIsAcceptOtherNum(boolean accept) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setAcceptOtherNum(accept);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveIsIpAlarmFirst(boolean ip) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setIpAlarmFirst(ip);
        persistConfig.save();
        return persistConfig;
    }

    public static void saveAlarmOn(boolean on) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setAlarmOn(on);
        persistConfig.save();
    }

    public static void saveDefaultAlarmOn(boolean on) {
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
                ", isAlarmOn=" + isAlarmOn +
                ", isDefaultAlarmOn=" + isDefaultAlarmOn +
                ", ftpHostNameDebugFly='" + ftpHostNameDebugFly + '\'' +
                ", ftpHostIPDebugFly='" + ftpHostIPDebugFly + '\'' +
                ", ftpHostPortDebugFly=" + ftpHostPortDebugFly +
                ", ftpUsernameFly='" + ftpUsernameFly + '\'' +
                ", ftpPasswordFly='" + ftpPasswordFly + '\'' +
                ", ftpHostNameDebug='" + ftpHostNameDebug + '\'' +
                ", ftpHostIPDebug='" + ftpHostIPDebug + '\'' +
                ", ftpHostPortDebug=" + ftpHostPortDebug +
                ", ftpUsernameDebug='" + ftpUsernameDebug + '\'' +
                ", ftpPasswordDebug='" + ftpPasswordDebug + '\'' +
                ", tcpHostNameDebug1='" + tcpHostNameDebug1 + '\'' +
                ", tcpHostNameDebug2='" + tcpHostNameDebug2 + '\'' +
                ", tcpHostIPDebug='" + tcpHostIPDebug + '\'' +
                ", tcpPortDebug=" + tcpPortDebug +
                ", ftpHostNameRelease='" + ftpHostNameRelease + '\'' +
                ", ftpHostIPRelease='" + ftpHostIPRelease + '\'' +
                ", ftpHostPortRelease=" + ftpHostPortRelease +
                ", ftpUsernameRelease='" + ftpUsernameRelease + '\'' +
                ", ftpPasswordRelease='" + ftpPasswordRelease + '\'' +
                ", tcpHostNameRelease1='" + tcpHostNameRelease1 + '\'' +
                ", tcpHostNameRelease2='" + tcpHostNameRelease2 + '\'' +
                ", tcpHostIPRelease='" + tcpHostIPRelease + '\'' +
                ", tcpPortRelease=" + tcpPortRelease +
                '}';
    }
}
