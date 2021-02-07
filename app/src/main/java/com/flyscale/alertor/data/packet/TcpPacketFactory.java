package com.flyscale.alertor.data.packet;

import android.content.Context;
import android.text.TextUtils;

import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.PhoneManagerUtil;
import com.flyscale.alertor.helper.PhoneUtil;

import java.net.ContentHandler;
import java.util.ArrayList;

public class TcpPacketFactory {

    /*7.3.1 数据心跳消息*/
    public static final long HEARTBEAT_DATA = 0x00000010L;

    /*7.3.1b 空白心跳消息*/
    public static final long HEARTBEAT_BLANK = 0;

    /*7.3.2a下传紧急广播AMR文件（内容是MP3格式，文件名后缀是amr）*/
    public static final long EMR_BROADCAST_MP3_FTP = 0x01000000L;

    /*7.3.2b下传紧急广播AMR文件播放反馈（内容是MP3格式，文件名后缀是amr）*/
    public static final long EMR_BROADCAST_MP3 = 0x01000081L;

    /*7.3.2c下传紧急广播AMR文件（内容是amr格式，文件名后缀是amr）*/
    public static final long EMR_BROADCAST_AMR_FTP = 0x01000009L;

    /*7.3.2d下传紧急广播AMR文件播放反馈（内容是amr格式，文件名后缀是amr）*/
    public static final long EMR_BROADCAST_AMR = 0x01000082L;

    /*7.3.3下传和删除普通AMR格式音频文件*/
    public static final long EMR_AMR_FILE_OPERATION = 0x01000001L;

    /*7.3.3a 获取AMR格式音频文件大小*/
    public static final long GET_FILE_SIZE = 0x01000002L;

    /*7.3.3b设置音频文件播放节目*/
    public static final ArrayList<Long> MUSIC_SHOW_LIST = new ArrayList<Long>();

    static {
        for (long i = 0x00000200L; i <= 0x0000020FL; i++) {
            MUSIC_SHOW_LIST.add(i);
        }
    }

    /*清除所有音频节目列表*/
    public static final long CLEAR_ALL_MUSIC_SHOW = 0x0000027FL;

    /*7.3.3bc 终端接收完AMR音频文件后反馈*/
    public static final long DOWNLOAD_AMR = 0X01000004L;

    /*7.3.3bd普通AMR文件播放反馈 ok*/
    public static final long PLAY_AMR = 0x01000083L;

    /*7.3.4下传升级文件*/
    public static final long DOWNLOAD_UPDATE_PATCH = 0x02000000L;

    /*7.3.5 终端接收完升级文件后反馈*/
    public static final long UPDATE_SYSTEM = 0x01000004L;

    /*7.3.6通用文件下载和删除*/
    public static final long COMMON_FILE_OPERATION_FTP = 0x02000005L;

    /*7.3.7 通用文件下载反馈*/
    public static final long COMMON_FILE_OPERATION = 0x02000006L;

    /*7.3.7b 获取通用文件大小及校验码*/
    public static final long GET_COMMON_FILE_INFO = 0x02000007L;

    /*7.3.8下传文件备份指令*/
    public static final long BACKUP1 = 0x02000001L;

    /*7.3.9 终端备份文件系统结束后反馈*/
    public static final long BACKUP2 = 0x02000002L;

    /*设置FM普通广播节目*/
    public static final ArrayList<Long> FM_SHOW_LIST = new ArrayList<Long>();

    static {
        for (long i = 0x00000100L; i <= 0x0000011FL; i++) {
            FM_SHOW_LIST.add(i);
        }
    }

    /*清除所有FM节目列表*/
    public static final long CLEAR_ALL_FM_SHOW = 0x0000017FL;

    /*7.3.11b FM播放反馈*/
    public static final long PLAY_FM = 0x01000085L;

    /*7.3.12设置FM插播广播节目*/
    /*设置FM普通广播节目*/
    public static final ArrayList<Long> BREAKING_FM_SHOW_LIST = new ArrayList<Long>();

    /*7.3.16 系统变量*/
    /*7.3.16.1 设备出厂编号*/
    public static final long DEVICE_ID = 0x00000009L;

    /*7.3.16.2 短连接参数*/
    public static final long SHORT_LINK_PARAM = 0x0000000AL;

    /*7.3.16.3 文件下载模式参数1*/
    public static final long FILE_DOWNLOAD_MODE_PARAM_1 = 0x0000000BL;

    /*7.3.16.4 文件下载模式参数2*/
    public static final long FILE_DOWNLOAD_MODE_PARAM_2 = 0x0000000CL;

    /*7.3.16.5 硬件版本号*/
    public static final long HARDWARE_VERSION = 0x00000021L;

    /*7.3.16.6 软件版本号*/
    public static final long SOFTWARE_VERSION = 0x00000022L;

    /*7.3.16.7 EVDO网络ip地址*/
    public static final long EVDO_IP_ADDRESS = 0x00000026L;

    /*7.3.16.8 音量*/
    public static final long VOLUME = 0x00000027L;

    /*7.3.16.9 平台服务器ip地址1*/
    public static final long PLATFORM_SERVER_IP_ADDRESS_1 = 0x00000028L;

    /*7.3.16.10 平台服务器ip地址2*/
    public static final long PLATFORM_SERVER_IP_ADDRESS_2 = 0x00000029L;

    /*7.3.16.11 基站信息*/
    public static final long BASE_STATION_INFORMATION = 0x0000002AL;

    /*7.3.16.12 位置信息*/
    public static final long LOCATION_INFORMATION = 0x0000002BL;

    /*7.3.16.13 FTP服务器IP地址*/
    public static final long FTP_SERVER_IP_ADDRESS = 0x0000002CL;

    /*7.3.16.14 FTP服务器账户口令*/
    public static final long FTP_SERVER_ACCOUNT_PASSWORD = 0x0000002DL;

    /*7.3.16.15 升级文件FTP目录*/
    public static final long FOTA_FILE_FTP_DIR = 0x0000002FL;

    /*7.3.16.16 音频文件FTP目录*/
    public static final long AUDIO_FILE_FTP_DIR = 0x00000031L;

    /*7.3.16.17 长链接心跳间隔(秒)*/
    public static final long LONG_LINK_HEARTBEAT_INTERVAL = 0x00000030L;

    /*7.3.16.18 终端复位*/
    public static final long DEVICE_RESET = 0x00000032L;

    /*7.3.16.19 拨打电话指令参数*/
    public static final long CALL_COMMAND_PARAM = 0x00000040L;

    /*7.3.16.20 功能键1拨打电话号码*/
    public static final long FUNCTION_1_CALL_PHONE_NUM = 0x00000041L;

    /*7.3.16.21 功能键2拨打电话号码*/
    public static final long FUNCTION_2_CALL_PHONE_NUM = 0x00000042L;

    /*7.3.16.22 功能键3拨打电话号码*/
    public static final long FUNCTION_3_CALL_PHONE_NUM = 0x00000043L;

    /*7.3.16.23 功能键4拨打电话号码*/
    public static final long FUNCTION_4_CALL_PHONE_NUM = 0x00000044L;

    /*7.3.16.24 平台获取终端可用存储空间大小*/
    public static final long DEVICE_AVAILABLE_SIZE = 0x00000045L;

    /*7.3.16.25 终端向平台查询系统当前时间*/
    public static final long QUERY_TIME_FORM_PLATFORM = 0x00000046L;

    /*7.3.16.26 终端个性化功能*/
    public static final long DEVICE_PERSON_FUNCTION = 0x00000047L;

    /*7.3.16.27 一键报警平台电话号码*/
    public static final long PLATFORM_PHONE_NUM = 0x00000048L;

    /*7.3.16.28 接警音量*/
    public static final long ALARM_VOLUME = 0x00000049L;

    /*7.3.16.29 批量删除文件指令*/
    public static final long BATCH_DEL_FILE = 0x0000004AL;

    /*7.3.16.30 设备进入短链接休眠*/
    public static final long DEVICE_SHORT_LINK_SLEEP = 0x0000004BL;

    /*7.3.16.31 音频直播频道服务器地址和端口*/
    public static final long AUDIO_LIVE_IP_ADDRESS_AND_PORT = 0x00000050L;

    /*7.3.16.32 音频直播频道账户密码*/
    public static final long AUDIO_LIVE_ACCOUNT_PASSWORD = 0x00000051L;

    /*7.3.16.33 音频直播频道开始结束时间*/
    public static final long AUDIO_LIVE_START_AND_END_TIME = 0x00000052L;

    /*7.3.16.34 音频直播频道url*/
    public static final long AUDIO_LIVE_CHANNEL_URL = 0x00000053L;

    /*7.3.16.35 FM固定频道设置*/
    public static final long FM_FIXED_CHANNEL_SETTINGS = 0x00000055L;

    /*全0数据*/
    public static final String dataZero = "00000000000000000000000000000000";


    static {
        for (long i = 0x00000180L; i <= 0x0000019FL; i++) {
            BREAKING_FM_SHOW_LIST.add(i);
        }
    }

    /*清除所有插播FM节目列表*/
    public static final long CLEAR_ALL_BREAKING_FM_SHOW = 0x000001FFL;

    /**
     * 终端登录平台失败后， 必须间隔60秒以上才能再次登录平台。
     * 任何情况下，终端登录平台两次间隔时间必须在60秒以上。
     */
    /*终端先上报SIM卡中的IMSI和终端的IMEI码*/
    public static final long LOGIN = 0X00000000L;
    /*平台下发随机数消息验证*/
    public static final long LOGIN_CONFIRM = 0X00000001L;
    /*登录结果*/
    public static final long LOGIN_RESULT = 0X00000003L;


    public enum LOGIN_CODE {
        SUCCESS(0),    //登录成功
        IMEID_NOT_EXIST(-99),//MEID不存在
        CODE_ERROR(-98),//认证码错误
        IMSI_NOT_SIGNED(-97),//IMSI未开户
        UKNOWN(-96);//其他

        private int code;

        public int getCode() {
            return code;
        }

        LOGIN_CODE(int code) {

        }
    }

    public static TcpPacket createPacketSend(long address, String data) {
        CMD cmd = null;
        if (address == HEARTBEAT_DATA || address == LOGIN) {
            cmd = CMD.WRITE;
        } else if (
                address == LOGIN_CONFIRM ||
                        address == EMR_BROADCAST_MP3 ||
                        address == EMR_BROADCAST_AMR ||
                        address == EMR_AMR_FILE_OPERATION ||
                        MUSIC_SHOW_LIST.contains(address) ||
                        address == CLEAR_ALL_MUSIC_SHOW ||
                        address == DOWNLOAD_AMR ||
                        address == PLAY_AMR ||
                        address == DOWNLOAD_UPDATE_PATCH ||
                        address == UPDATE_SYSTEM ||
                        address == COMMON_FILE_OPERATION ||
                        address == BACKUP1 ||
                        address == BACKUP2 ||
                        FM_SHOW_LIST.contains(address) ||
                        address == CLEAR_ALL_FM_SHOW ||
                        BREAKING_FM_SHOW_LIST.contains(address) ||
                        address == PLAY_FM ||
                        address == CLEAR_ALL_BREAKING_FM_SHOW) {
            cmd = CMD.WRITE_ANSWER;
        } else if (address == GET_FILE_SIZE ||
                address == GET_COMMON_FILE_INFO) {
            cmd = CMD.READ_ANSWER;
        }
        if (cmd != null) {
            return TcpPacket.getInstance().encode(cmd, address, data);
        } else {
            DDLog.i("不识别的指令！");
            return null;
        }
    }

    public static TcpPacket createPacketSend(Context context, long address) {
        if (address == LOGIN) {
            String imsi = PhoneManagerUtil.getIMSI(context);
            String imei1 = PhoneManagerUtil.getIMEI1(context);

            //TODO 测试时使用
            imsi = "460031234567890";
            imei1 = "0A9464026708209";

            //imsi和imei1中的86要替换为0A
            if (!TextUtils.isEmpty(imsi) && !TextUtils.isEmpty(imei1)) {
                imsi.replace("86", "0A");
                imei1.replace("86", "0A");
            }
            return createPacketSend(address, imsi + "/" + imei1 + "/");
        }
        return null;
    }

    public static TcpPacket from(byte[] tcpBytes) {
        return TcpPacket.decode(tcpBytes);
    }
}
