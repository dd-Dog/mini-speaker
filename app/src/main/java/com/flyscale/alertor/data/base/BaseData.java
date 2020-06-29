package com.flyscale.alertor.data.base;

import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.netty.NettyHelper;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 11:39
 * @DESCRIPTION 暂无
 */
public abstract class BaseData {
    public static final String FLAG_START = "[";
    public static final String FLAG_END = "]";
    public static final String FLAG_SPLIT = ",";

    //心跳
    public static final int TYPE_HEART_U = 49;
    public static final int TYPE_HEART_D = 54;
    //报警
    public static final int TYPE_ALARM_U = 40;
    public static final int TYPE_ALARM_D = 41;
    //响铃
    public static final int TYPE_RING_U = 91;
    public static final int TYPE_RING_D = 90;
    //语音
    public static final int TYPE_VOICE_U = 58;
    public static final int TYPE_VOICE_D = 56;
    //修改心跳频率
    public static final int TYPE_CHANGE_HEART_U = 96;
    public static final int TYPE_CHANGE_HEART_D = 95;
    //删除语音存储
    public static final int TYPE_DELETE_VOICE_U = 98;
    public static final int TYPE_DELETE_VOICE_D = 97;
    //终端版本升级
    public static final int TYPE_UPDATE_VERSION_U = 101;
    public static final int TYPE_UPDATE_VERSION_D = 99;
    //修改报警拨叫的号码
    public static final int TYPE_CHANGE_ALARM_NUMBER_U = 102;
    public static final int TYPE_CHANGE_ALARM_NUMBER_D = 103;
    //修改测试键的拨叫测试号码
    public static final int TYPE_CHANGE_TEST_KEY_NUMBER_U = 104;
    public static final int TYPE_CHANGE_TEST_KEY_NUMBER_D = 105;
    //修改主动拨打的电话、及频率
    public static final int TYPE_CHANGE_CALL_HZ_U = 106;
    public static final int TYPE_CHANGE_CALL_HZ_D = 107;
    //修改IP地址及端口
    public static final int TYPE_CHANGE_IP_U = 108;
    public static final int TYPE_CHANGE_IP_D = 109;
    //添加、删除电话白名单
    public static final int TYPE_ADD_OR_DELETE_WHITE_LIST_U = 110;
    public static final int TYPE_ADD_OR_DELETE_WHITE_LIST_D = 111;
    //更新对应号码在终端的响铃铃声
    public static final int TYPE_UPDATE_NUMBER_RING_U = 114;
    public static final int TYPE_UPDATE_NUMBER_RING_D = 115;
    //移动终端报警
    public static final int TYPE_CLIENT_ALARM_U = 116;
    public static final int TYPE_CLIENT_ALARM_D = 117;
    //移动终端录音上传
    public static final int TYPE_CLIENT_UPDATE_RECORD_U = 118;
    public static final int TYPE_CLIENT_UPDATE_RECORD_D = 119;
    //2.16门磁报警
    public static final int TYPE_DOOR_ALARM_U = 120;
    public static final int TYPE_DOOR_ALARM_D = 121;
    //2.17烟感报警
    public static final int TYPE_SMOKE_ALARM_U = 122;
    public static final int TYPE_SMOKE_ALARM_D = 123;
    //2.18煤气报警
    public static final int TYPE_GAS_ALARM_U = 124;
    public static final int TYPE_GAS_ALARM_D = 125;
    //2.19终端更换CA证书
    public static final int TYPE_CHANGE_CLIENT_CA_U = 126;
    public static final int TYPE_CHANGE_CLIENT_CA_D = 127;


    //设备的IMEI ，终端业务平台的上行报文加入标识字段
    String imei;
    //交易流水号 时间格式+4位循环 时间格式：yyyyMMddHHmmss
    //4位循环数：范围0-9999，从0开始，递增赋值，步长为1，增加到9999后，再从0开始
    public String tradeNum;
    //设备号码
    String iccid;
    //市电 是否接入220V电源，0接入，1未接入
    int AC;
    //电量百分比
    String batteryLevel;
    //基站信息  STRING[10]  基站1@基站2@基站3；按基站距离排序由近到远
    String stationInfo;
    //GPS状态	STRING[10]	0--关闭  1-开启
    int gpsStatus;
    //终端类型	STRING[10]	001TCL，002卡尔
    String terminalType;
    //网络制式	STRING
    String netType;
    //型号	STRING	厂家定义终端型号
    String terminalModel;
    //终端第一次登录时间	STRING	时间格式：yyyy-MM-dd hh:mm:ss，记录终端第一次登录平台的时间，永久不变
    String FirstLoginTime;
    //报警时间	STRING[10]	时间格式：yyyyMMddHHmm
    String currentTime;
    //重复发送序号	STRING[1]	1或2或3
    public int sendCount;
    //报警结果	STRING[1]	0表示正常接收，1没有正常接收，重发
    String alarmResult;
    //电话号码 	STRING[11]
    String mobileNum;
    //报文体 	STRING[0-128]	总包数@包序号@设置状态@终端录音存储状态
    //设置状态:0表示成功，1表示失败
    //总包数:语音报分包总数
    //包序号:包序号,比如是第几个包
    //终端录音存储状态：0表示空间满，1表示空间不满，可以继续接受录音
    public String messageBody;
    //是否压缩	STRING[1]	0 表示不压缩，1表示压缩，压缩算法默认是gzip
    String isCompress;
    //5	总的包数	STRING[2]	音频分包的总数
    String totalPacket;
    //包的序号	STRING[2]	分包的序号
    String packetNum;
    //传输的数据长度	STRING[4]	16进制保存，高位在前，低位在后，长度包含校验位
    String messageSize;
    //语音播报汉字 STRING[1-128]	Unicode编码的字符串
    String voiceText;
    //报文体	STRING[N]	十六进制字符串,音频格式是AMR
    String messageBodyResp;
    //修改结果	STRING[1] 	1成功；0失败；格式1@；0@失败原因
    public String changeResult;
    //心跳频率	Integer	发送频率
    int heartHZ;
    //厂家代码	STRING[1]	001是TCL，002是卡尔
    String factoryCode;
    //报警拨叫号码 	STRING[11]	报警拨叫号码
    String alarmNum;
    //测试号码 	STRING[11]	测试号码
    String testNumber;
    //主动拨打电话号码 	STRING[11-55]	主动拨打电话号码，1-5个电话号码，最少1个，最多5个号码，电话号码之间以封号”;”分割
    String callNumber;
    //频率	Integer	单位：天
    int dayHZ;
    //平台IP地址 	STRING[7-15]	平台新的IP地址
    String ipAddress;
    //平台端口	Integer	平台端口
    String ipPort;
    //添加、删除标识	STRING[1]0添加1删除	添加/删除标识
    String addOrDeleteFlag;
    //白名单号码 	STRING[11+]	添加或删除的白名单号码，最少1个，最多无限制，电话号码之间以封号”;”分割
    String whiteList;
    //需要更新白名单号码对应铃声序号	Integer
    int whiteListRingNum;
    //经度	STRING[10]
    String lon;
    //纬度	STRING[10]
    String lat;
    //文件序号	STRING[10]
    public String fileNum;
    //报警流水号	STRING[50]
    public String alarmSerNum;
    //客户端证书报文体	STRING[N]	十六进制字符串
    String clientCaMessage;
    //客户端秘钥报文体	STRING[N]	十六进制字符串
    String clientPwdMessage;
    //根证书报文体	STRING[N]	十六进制字符串
    String rootCaMessage;

    String extra1 = "";
    String extra2 = "";
    String extra3 = "";
    String extra4 = "";
    String extra5 = "";

    public String getClientCaMessage() {
        return clientCaMessage;
    }

    public void setClientCaMessage(String clientCaMessage) {
        this.clientCaMessage = clientCaMessage;
    }

    public String getClientPwdMessage() {
        return clientPwdMessage;
    }

    public void setClientPwdMessage(String clientPwdMessage) {
        this.clientPwdMessage = clientPwdMessage;
    }

    public String getRootCaMessage() {
        return rootCaMessage;
    }

    public void setRootCaMessage(String rootCaMessage) {
        this.rootCaMessage = rootCaMessage;
    }

    public String getAlarmSerNum() {
        return alarmSerNum;
    }

    public void setAlarmSerNum(String alarmSerNum) {
        this.alarmSerNum = alarmSerNum;
    }

    public String getFileNum() {
        return fileNum;
    }

    public void setFileNum(String fileNum) {
        this.fileNum = fileNum;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public int getWhiteListRingNum() {
        return whiteListRingNum;
    }

    public void setWhiteListRingNum(int whiteListRingNum) {
        this.whiteListRingNum = whiteListRingNum;
    }

    public String getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(String whiteList) {
        this.whiteList = whiteList;
    }

    public String getAddOrDeleteFlag() {
        return addOrDeleteFlag;
    }

    public void setAddOrDeleteFlag(String addOrDeleteFlag) {
        this.addOrDeleteFlag = addOrDeleteFlag;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpPort() {
        return ipPort;
    }

    public void setIpPort(String ipPort) {
        this.ipPort = ipPort;
    }

    public int getDayHZ() {
        return dayHZ;
    }

    public void setDayHZ(int dayHZ) {
        this.dayHZ = dayHZ;
    }

    public String getCallNumber() {
        return callNumber;
    }

    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }

    public String getTestNumber() {
        return testNumber;
    }

    public void setTestNumber(String testNumber) {
        this.testNumber = testNumber;
    }

    public String getAlarmNum() {
        return alarmNum;
    }

    public void setAlarmNum(String alarmNum) {
        this.alarmNum = alarmNum;
    }

    public String getFactoryCode() {
        return factoryCode;
    }

    public void setFactoryCode(String factoryCode) {
        this.factoryCode = factoryCode;
    }

    public int getHeartHZ() {
        return heartHZ;
    }

    public void setHeartHZ(int heartHZ) {
        this.heartHZ = heartHZ;
    }

    public String getChangeResult() {
        return changeResult;
    }

    public void setChangeResult(String changeResult) {
        this.changeResult = changeResult;
    }

    public String getMessageBodyResp() {
        return messageBodyResp;
    }

    public void setMessageBodyResp(String messageBodyResp) {
        this.messageBodyResp = messageBodyResp;
    }

    public String getVoiceText() {
        return voiceText;
    }

    public void setVoiceText(String voiceText) {
        this.voiceText = voiceText;
    }

    public String getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(String messageSize) {
        this.messageSize = messageSize;
    }

    public String getPacketNum() {
        return packetNum;
    }

    public void setPacketNum(String packetNum) {
        this.packetNum = packetNum;
    }

    public String getTotalPacket() {
        return totalPacket;
    }

    public void setTotalPacket(String totalPacket) {
        this.totalPacket = totalPacket;
    }

    public String getIsCompress() {
        return isCompress;
    }

    public void setIsCompress(String isCompress) {
        this.isCompress = isCompress;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public String getAlarmResult() {
        return alarmResult;
    }

    public void setAlarmResult(String alarmResult) {
        this.alarmResult = alarmResult;
    }

    public int getSendCount() {
        return sendCount;
    }

    public void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }

    public String getExtra1() {
        return extra1;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }

    public String getExtra2() {
        return extra2;
    }

    public void setExtra2(String extra2) {
        this.extra2 = extra2;
    }

    public String getExtra3() {
        return extra3;
    }

    public void setExtra3(String extra3) {
        this.extra3 = extra3;
    }

    public String getExtra4() {
        return extra4;
    }

    public void setExtra4(String extra4) {
        this.extra4 = extra4;
    }

    public String getExtra5() {
        return extra5;
    }

    public void setExtra5(String extra5) {
        this.extra5 = extra5;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(String tradeNum) {
        this.tradeNum = tradeNum;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public int getAC() {
        return AC;
    }

    public void setAC(int AC) {
        this.AC = AC;
    }

    public String getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String getStationInfo() {
        return stationInfo;
    }

    public void setStationInfo(String stationInfo) {
        this.stationInfo = stationInfo;
    }

    public int getGpsStatus() {
        return gpsStatus;
    }

    public void setGpsStatus(int gpsStatus) {
        this.gpsStatus = gpsStatus;
    }

    public String getTerminalType() {
        return terminalType;
    }

    public void setTerminalType(String terminalType) {
        this.terminalType = terminalType;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getTerminalModel() {
        return terminalModel;
    }

    public void setTerminalModel(String terminalModel) {
        this.terminalModel = terminalModel;
    }

    public String getFirstLoginTime() {
        return FirstLoginTime;
    }

    public void setFirstLoginTime(String firstLoginTime) {
        FirstLoginTime = firstLoginTime;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    /**
     * @param args
     * @return  格式化发送的报文
     */
    public String formatToString(Object... args){
        StringBuilder stringBuilder = new StringBuilder();
        for(Object item : args){
            stringBuilder.append(item).append(FLAG_SPLIT);
        }
        String result = stringBuilder.toString();
        if(result.endsWith(FLAG_SPLIT)){
            result = result.substring(0,result.length()-1);
        }
        result = FLAG_START + result + FLAG_END;
        Log.i(NettyHelper.TAG, "formatToString: 上行报文 = " + result);
        return result;
    }

    /**
     * @param result
     * @return  格式化 返回的响应报文
     */
    public String[] formatToArray(String result){
        result = result.replace(FLAG_START,"");
        result = result.replace(FLAG_END,"");
        String[] array = TextUtils.split(result, FLAG_SPLIT);
        return array;
    }


    public abstract int getType();
    public abstract String formatToString();
    public abstract BaseData formatToObject(String result);

}
