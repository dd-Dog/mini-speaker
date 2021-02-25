package com.flyscale.alertor.data.persist;

import android.app.PendingIntent;
import android.text.TextUtils;

import com.flyscale.alertor.helper.DateHelper;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * @author 高鹤泉
 * @TIME 2020/6/19 18:05
 * @DESCRIPTION 暂无
 */
public class PersistConfig extends LitePalSupport {
    @Deprecated
    String ip = "bltst2.xjxlb.com";
    @Deprecated
    int port = 50074;
    //通用文件路径
    public static final String COMMON_FILE_PATH = "/mnt/sdcard/flyscale/common/";
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
    String tcpHostNameDebug1 = "bltst2.xjxlb.com";//两个域名使用同一IP，默认使用1
    String tcpHostNameDebug2 = "bltst2.xjxlb.com";
    String tcpHostIPDebug = "";
    int tcpPortDebug = 50074;

    /******************客户正式服务器*******************/
    String ftpHostNameRelease = "ftp3.xjxlb.com";
    String ftpHostIPRelease = "202.100.190.107";
    int ftpHostPortRelease = 58000;
    String ftpUsernameRelease = "";
    String ftpPasswordRelease = "";

    //文件下载地址，优先HTTP，
    String httpDownloadUrl = "http://http1.xjxlb.com:58003";

    String tcpHostNameRelease1 = "xlb1.xjxlb.com";//两个域名使用同一IP，默认使用1
    String tcpHostNameRelease2 = "xlb1.xj-ict.com";
    String tcpHostIPRelease = "202.100.190.107";
    int tcpPortRelease = 58005;


    /****************短链接参数***********************/
    String linkType = "1";              //链接类型(0短链接；1长链接)
    String shortLinkSleepTime = "10";   //短链接休眠时长（秒）
    String shortLinkDelay = "10";       //短链接工作等待延迟（秒）


    /*************文件下载模式参数1(可读可写)*************/
    String downloadMode = "1";          //下载模式：0 ftp模式；1 http下载模式
    String httpAccount = "ostar";       //http下载账户
    String httpPwd = "ostar";           //http下载密码


    /******************音量(可读可写) ******************/
    //音量0-b分12档（0挡为最低档没有声音，其余挡位逐渐加大）
    String normalFmEnabled = "1";       //FM普通广播使能标志：1 使能，0 禁止
    String insertFmEnabled = "1";       //FM插播广播使能标志：1 使能，0 禁止


    /****************升级文件FTP目录*****************/
    String ftpFotafilePath = "/china_telcom/3gdev";   //升级目录


    /***************音频文件FTP目录******************/
    String ftpAudioFilepath = "/china_telcom/dev1";   //音频文件FTP目录


    /*************长链接心跳间隔(秒)*********/
    String longLinkHeartbeat = "20";      //长连接心跳间隔(秒)
    String longLinkSignDelay = "60";      //长连接登录延迟(秒)
    //长链接短链接选择 (固定为0:短连接 ;固定为1:长连接)
    //String linkType = "1";
    String platformSmsNum = "0";          //平台短信号码


    /**************拨打电话指令参数 *************/
    String phoneNum = "0";        //拨打的电话号码
    String callTime = "20";        //每次通话时长（秒）
    String callDate = "202101010000";  //拨打电话的时间（年月日时分）
    String times = "03";           //拨打失败后的重试次数


    /*************平台电话号码*****************/
    String platformNum = "0";


    /*********** 4个功能键对应的电话号码 ****************/
    String key1Num = "0";
    String key2Num = "0";
    String key3Num = "0";
    String key4Num = "0";


    /**************终端个性化功能***************/
    String emrPlayMode = "0";       //0 APP紧急语音单次播放; 1：APP紧急语音循环播放
    String moveSwitch = "0";     //0防移开关启用; 1防移开关禁用
    String alarmMode = "0";      // 0一键报警普通模式（十户联防号码呼入：用户按键接听）;
                                // 1一键报警奎屯模式（十户联防号码呼入后：响3声报警音，再自动接听，播放完成报警信息后，用户挂断）
                                //2：一键报警沙湾模式（十户联防号码呼入后：不响报警音，自动接听，播放完成报警信息后，用户挂断；）

    String callEnable = "0";     //1就是只能拨打报警与快捷键; 0可以拨打所有电话
    String channelSelect = "0";  //频选”参数 ：0 默认; 1 4G-800M优选;  2:4G-1800M优选
    String wifiSwitch = "1";     //“WIFI开关”参数 ：0 开通; 1 关闭

    //DES随机密钥，只有登录成功后才能使用
    private String randomKey;
    private boolean login = false;

    //闹钟列表
    List<PendingIntent> list;

    //电池电量
    private int battery;

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public static PersistConfig saveBattery(int battery) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setBattery(battery);
        persistConfig.save();
        return persistConfig;
    }

    public void setArrayList(List<PendingIntent> list) {
        this.list = list;
    }

    public List<PendingIntent> getList() {
        return list;
    }

    public boolean isLogin() {
        return login;
    }

    public PersistConfig setLogin(boolean login) {
        this.login = login;
        return this;
    }

    public String getRandomKey() {
        return randomKey;
    }

    public PersistConfig setRandomKey(String randomKey) {
        this.randomKey = randomKey;
        return this;
    }

    public static PersistConfig saveLogin(boolean login) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setLogin(login);
        persistConfig.save();
        return persistConfig;
    }
    public static PersistConfig saveRandomKey(String randomKey) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setRandomKey(randomKey);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveHttpDownloadUrl(String httpDownloadUrl) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setHttpDownloadUrl(httpDownloadUrl);
        persistConfig.save();
        return persistConfig;
    }

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

    public String getHttpDownloadUrl() {
        return httpDownloadUrl;
    }

    public PersistConfig setHttpDownloadUrl(String httpDownloadUrl) {
        this.httpDownloadUrl = httpDownloadUrl;
        return this;
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

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getShortLinkSleepTime() {
        return shortLinkSleepTime;
    }

    public void setShortLinkSleepTime(String shortLinkSleepTime) {
        this.shortLinkSleepTime = shortLinkSleepTime;
    }

    public String getShortLinkDelay() {
        return shortLinkDelay;
    }

    public void setShortLinkDelay(String shortLinkDelay) {
        this.shortLinkDelay = shortLinkDelay;
    }

    public String getDownloadMode() {
        return downloadMode;
    }

    public void setDownloadMode(String downloadMode) {
        this.downloadMode = downloadMode;
    }

    public String getHttpAccount() {
        return httpAccount;
    }

    public void setHttpAccount(String httpAccount) {
        this.httpAccount = httpAccount;
    }

    public String getHttpPwd() {
        return httpPwd;
    }

    public void setHttpPwd(String httpPwd) {
        this.httpPwd = httpPwd;
    }

    public String getNormalFmEnabled() {
        return normalFmEnabled;
    }

    public void setNormalFmEnabled(String normalFmEnabled) {
        this.normalFmEnabled = normalFmEnabled;
    }

    public String getInsertFmEnabled() {
        return insertFmEnabled;
    }

    public void setInsertFmEnabled(String insertFmEnabled) {
        this.insertFmEnabled = insertFmEnabled;
    }

    public String getFtpFotafilePath() {
        return ftpFotafilePath;
    }

    public void setFtpFotafilePath(String ftpFotafilePath) {
        this.ftpFotafilePath = ftpFotafilePath;
    }

    public String getFtpAudioFilepath() {
        return ftpAudioFilepath;
    }

    public void setFtpAudioFilepath(String ftpAudioFilepath) {
        this.ftpAudioFilepath = ftpAudioFilepath;
    }

    public String getLongLinkHeartbeat() {
        return longLinkHeartbeat;
    }

    public void setLongLinkHeartbeat(String longLinkHeartbeat) {
        this.longLinkHeartbeat = longLinkHeartbeat;
    }

    public String getLongLinkSignDelay() {
        return longLinkSignDelay;
    }

    public void setLongLinkSignDelay(String longLinkSignDelay) {
        this.longLinkSignDelay = longLinkSignDelay;
    }

    public String getPlatformSmsNum() {
        return platformSmsNum;
    }

    public void setPlatformSmsNum(String platformSmsNum) {
        this.platformSmsNum = platformSmsNum;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getCallDate() {
        return callDate;
    }

    public void setCallDate(String callDate) {
        this.callDate = callDate;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getPlatformNum() {
        return platformNum;
    }

    public void setPlatformNum(String platformNum) {
        this.platformNum = platformNum;
    }

    public String getKey1Num() {
        return key1Num;
    }

    public void setKey1Num(String key1Num) {
        this.key1Num = key1Num;
    }

    public String getKey2Num() {
        return key2Num;
    }

    public void setKey2Num(String key2Num) {
        this.key2Num = key2Num;
    }

    public String getKey3Num() {
        return key3Num;
    }

    public void setKey3Num(String key3Num) {
        this.key3Num = key3Num;
    }

    public String getKey4Num() {
        return key4Num;
    }

    public void setKey4Num(String key4Num) {
        this.key4Num = key4Num;
    }

    public String getEmrPlayMode() {
        return emrPlayMode;
    }

    public void setEmrPlayMode(String emrPlayMode) {
        this.emrPlayMode = emrPlayMode;
    }

    public String getMoveSwitch() {
        return moveSwitch;
    }

    public void setMoveSwitch(String moveSwitch) {
        this.moveSwitch = moveSwitch;
    }

    public String getAlarmMode() {
        return alarmMode;
    }

    public void setAlarmMode(String alarmMode) {
        this.alarmMode = alarmMode;
    }

    public String getCallEnable() {
        return callEnable;
    }

    public void setCallEnable(String callEnable) {
        this.callEnable = callEnable;
    }

    public String getChannelSelect() {
        return channelSelect;
    }

    public void setChannelSelect(String channelSelect) {
        this.channelSelect = channelSelect;
    }

    public String getWifiSwitch() {
        return wifiSwitch;
    }

    public void setWifiSwitch(String wifiSwitch) {
        this.wifiSwitch = wifiSwitch;
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

    public static void saveAlarmManager(List<PendingIntent> list) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setArrayList(list);
        persistConfig.save();
    }

    public static PersistConfig savePlatformNum(String num) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setPlatformNum(num);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveLinkType(String type) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setLinkType(type);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveShortLinkSleepTime(String time) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setShortLinkSleepTime(time);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveShortLinkDelay(String time) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setShortLinkDelay(time);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveDownloadMode(String mode) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setDownloadMode(mode);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveHttpAccount(String account) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setHttpAccount(account);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveHttpPwd(String pwd) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setHttpPwd(pwd);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveNormalFmEnabled(String enabled) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setNormalFmEnabled(enabled);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveInsertFmEnabled(String enabled) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setInsertFmEnabled(enabled);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveFtpFotafilePath(String path) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setFtpFotafilePath(path);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveFtpAudioFilepath(String path) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setFtpAudioFilepath(path);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveLongLinkHeartbeat(String time) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setLongLinkHeartbeat(time);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveLongLinkSignDelay(String time) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setLongLinkSignDelay(time);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig savePlatformSmsNum(String num) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setPlatformSmsNum(num);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig savePhoneNum(String num) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setPhoneNum(num);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveCallTime(String time) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setCallTime(time);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveCallDate(String date) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setCallDate(date);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveCallTimes(String times) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setTimes(times);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveKey1Num(String num) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setKey1Num(num);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveKey2Num(String num) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setKey2Num(num);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveKey3Num(String num) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setKey3Num(num);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveKey4Num(String num) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setKey4Num(num);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveEmrPlayMode(String mode) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setEmrPlayMode(mode);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveMoveSwitch(String moveSwitch) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setMoveSwitch(moveSwitch);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveAlarmMode(String mode) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setAlarmMode(mode);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveCallEnabled(String enabled) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setCallEnable(enabled);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveChannelSelect(String channel) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setChannelSelect(channel);
        persistConfig.save();
        return persistConfig;
    }

    public static PersistConfig saveWifiSwitch(String enabled) {
        PersistConfig persistConfig = findConfig();
        persistConfig.setWifiSwitch(enabled);
        persistConfig.save();
        return persistConfig;
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
