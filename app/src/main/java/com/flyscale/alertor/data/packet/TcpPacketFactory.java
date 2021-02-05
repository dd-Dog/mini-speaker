package com.flyscale.alertor.data.packet;

import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.PhoneUtil;

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

    public static TcpPacket from(byte[] tcpBytes) {
        return TcpPacket.decode(tcpBytes);
    }
}
