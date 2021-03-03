package com.flyscale.alertor.netty;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyscale.alertor.Constants;
import com.flyscale.alertor.R;
import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.packet.CMD;
import com.flyscale.alertor.data.packet.TcpPacket;
import com.flyscale.alertor.data.packet.TcpPacketFactory;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistPacket;
import com.flyscale.alertor.data.persist.PersistWhite;
import com.flyscale.alertor.helper.AlarmManagerUtil;
import com.flyscale.alertor.helper.ClientInfoHelper;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.DateHelper;
import com.flyscale.alertor.helper.FileHelper;
import com.flyscale.alertor.helper.FillZeroUtil;
import com.flyscale.alertor.helper.HttpDownloadHelper;
import com.flyscale.alertor.helper.LocationHelper;
import com.flyscale.alertor.helper.LoginHelper;
import com.flyscale.alertor.helper.MD5Util;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.NetHelper;
import com.flyscale.alertor.helper.PhoneManagerUtil;
import com.flyscale.alertor.helper.PhoneUtil;
import com.flyscale.alertor.helper.UserActionHelper;
import com.flyscale.alertor.led.LedInstance;
import com.flyscale.alertor.media.MusicPlayer;
import com.flyscale.alertor.services.AlarmService;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.DownloadListener2;
import com.flyscale.alertor.helper.FMLitepalUtil;
import com.flyscale.alertor.helper.FMUtil;
import com.flyscale.alertor.helper.BreakFMLitepalUtil;

import org.litepal.LitePal;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import static com.flyscale.alertor.helper.ListHelper.removeDuplicate;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 9:51
 * @DESCRIPTION 暂无
 */
public class NettyHandler extends SimpleChannelInboundHandler<TcpPacket> {

    String TAG = "NettyHandler";
    String data;
    Timer timer = new Timer();
    AlarmManagerUtil alarmManagerUtil;


    public NettyHandler() {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        Log.i(TAG, "handlerAdded: ");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        Log.i(TAG, "handlerRemoved: ");
    }

    /**
     * channel没有连接到远程节点
     *
     * @param ctx
     * @throws
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (timer != null) {
            timer.cancel();
        }
        Log.i(TAG, "channelInactive:  --- 准备重连 ---  ");
//        PersistConfig.saveLogin(false);
//        LedInstance.getInstance().offStateLed();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                NettyHelper.getInstance().connect();
            }
        }, 65 * 1000);
    }

    /**
     * channel被创建但没有注册到eventLoop
     *
     * @param ctx
     * @throws
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        Log.i(TAG, "channelUnregistered: ");
    }


    /**
     * channel处于活动状态 连接到了远程节点 可以接收和发送
     *
     * @param ctx
     * @throws
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Log.i(TAG, "channelActive");

        //开始鉴权
        //TcpPacketFactory.LOGIN, "460031234567890/0A9464026708209/")
        NettyHelper.getInstance().send(TcpPacketFactory.createPacketSend(BaseApplication.sContext, TcpPacketFactory.LOGIN));
//        NettyHelper.getInstance().send(TcpPacketFactory.createPacketSend(TcpPacketFactory.LOGIN, "460031234567891/0A6285039008479/"));

        //模拟数据
//        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE, TcpPacketFactory.EMR_BROADCAST_MP3,
//                "abcdefgh.amr/0000006789/30/13235"));
//        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE, TcpPacketFactory.LOGIN_RESULT,
//                FillZeroUtil.getString(0 + "/", 32)));
    }

    /**
     * //利用写空闲发送心跳检测消息
     *
     * @param ctx
     * @param evt
     * @throws
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE || ((IdleStateEvent) evt).state() == IdleState.ALL_IDLE) {
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(TcpPacket.BLANK));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Log.i(TAG, "exceptionCaught: " + cause.getMessage());
        ctx.close();
    }

    /**
     * channel已经注册到eventLoop
     *
     * @param ctx
     * @throws
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        Log.i(TAG, "channelRegistered: ");
    }


    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, TcpPacket tcpPacket) throws Exception {
        DDLog.i("channelRead0 tcpPacket=" + tcpPacket);
        if (tcpPacket != null) {
            long address = tcpPacket.getAddress();
            data = tcpPacket.getData();
            if (address == TcpPacketFactory.LOGIN_CONFIRM) {
                PersistConfig.saveLogin(false);
                //登录鉴权
                String md5 = MD5Util.md5(tcpPacket.getData(), MD5Util.getKI());
                PersistConfig.saveRandomKey(tcpPacket.getData());
                PersistConfig.saveLogin(false);
                DDLog.i("鉴权MD5=" + md5);
                TcpPacket packetSend = TcpPacketFactory.createPacketSend(TcpPacketFactory.LOGIN_CONFIRM, md5);
                NettyHelper.getInstance().send(packetSend);
            } else if (address == TcpPacketFactory.LOGIN_RESULT) {
                //判断登录成功
                String data = tcpPacket.getData();
                DDLog.i("登录返回：" + data);
                if (!TextUtils.isEmpty(data)) {
                    if (data.split("/") != null) {
                        if (TextUtils.equals(TcpPacketFactory.LOGIN_CODE.SUCCESS.getCode() + "", data.split("/")[0])) {
                            DDLog.i("登录成功");
                            //保存第一次登陆的时间 永久不变
                            PersistConfig.saveFirstLoginTime(System.currentTimeMillis());
                            if (!UserActionHelper.isFastConnect(120 * 1000)) {
                                MediaHelper.play(MediaHelper.SERVER_CONNECT_SUCCESS, true);
                            }
                            LedInstance.getInstance().showStateLed();

                            PersistConfig.saveLogin(true);
                            PersistConfig.saveBattery(PhoneManagerUtil.getBatteryLevel(BaseApplication.sContext));
                            LoginSuccess();
                            return;
                        }
                    }
                }
                DDLog.e("登录失败！");
                PersistConfig.saveLogin(false);
            } else if (address == TcpPacketFactory.HEARTBEAT_DATA) {
                /*7.3.1 数据心跳消息*/
                DDLog.i("心跳返回：" + data);
            } else if (address == TcpPacketFactory.HEARTBEAT_BLANK) {
                /*7.3.1b 空白心跳消息*/
                DDLog.i("空白心跳消息返回" + data);
            } else if (address == TcpPacketFactory.EMR_BROADCAST_MP3_FTP) {
                /*7.3.2a下传紧急广播AMR文件（内容是MP3格式，文件名后缀是amr）*/
                DDLog.i("下传紧急广播AMR文件（内容是MP3格式，文件名后缀是amr）" + data);
                //wd,01000000,abcdefgh.amr/0123456789/30/13235xxxx
                /*行地址为01000000，表明文件头，数据中1-12字节为文件名，14-23字节为文件原始大小，25-26字节为连续播放次数，每次播放间隔1秒。*/
                if (!TextUtils.isEmpty(data)) {
                    if (data.split("/") != null) {
                        String fileName = data.split("/")[0];
                        long size = Long.parseLong(data.split("/")[1]);
                        final int playTimes = Integer.parseInt(data.split("/")[2]);
                        DDLog.i("结果" + fileName + size + playTimes);
                        PersistConfig.saveEmrInfo(fileName, size, playTimes, "mp3");
                        AlarmService.emergencyAudio();
                    }
                }
            } else if (address == TcpPacketFactory.EMR_BROADCAST_MP3) {
                /*7.3.2b下传紧急广播AMR文件播放反馈（内容是MP3格式，文件名后缀是amr）*/
                DDLog.i("下传紧急广播AMR文件播放反馈（内容是MP3格式，文件名后缀是amr）");

            } else if (address == TcpPacketFactory.EMR_BROADCAST_AMR_FTP) {
                /*7.3.2a下传紧急广播AMR文件（内容是MP3格式，文件名后缀是amr）*/
                /*行地址为01000009，表明文件头，数据中1-12字节为文件名，14-23字节为文件原始大小，25-26字节为连续播放次数，每次播放间隔1秒。*/
                DDLog.i("下传紧急广播AMR文件（内容是amr格式，文件名后缀是amr）" + data);
                if (!TextUtils.isEmpty(data)) {
                    if (data.split("/") != null) {
                        String fileName = data.split("/")[0];
                        long size = Long.parseLong(data.split("/")[1]);
                        final int playTimes = Integer.parseInt(data.split("/")[2]);
                        PersistConfig.saveEmrInfo(fileName, size, playTimes, "amr");
                        AlarmService.emergencyAudio();
                    }
                }
            } else if (address == TcpPacketFactory.EMR_BROADCAST_AMR) {
                /*7.3.2d下传紧急广播AMR文件播放反馈（内容是amr格式，文件名后缀是amr）*/
                DDLog.i("下传紧急广播AMR文件播放反馈（内容是amr格式，文件名后缀是amr）");

            } else if (address == TcpPacketFactory.EMR_AMR_FILE_OPERATION) {
                /*7.3.3下传和删除普通AMR格式音频文件*/
                /**
                 * wd,01000001,abcdefgh.amr/0123456789/30/13235xxxx
                 * 行地址为01000001，
                 * 表明文件头，数据中1-12字节为文件名，
                 * 14-23字节为文件原始大小，
                 * 25-26字节为连续播放次数，每次播放间隔1秒。
                 */
                DDLog.i("下传和删除普通AMR格式音频文件" + data);
                if (!TextUtils.isEmpty(data)) {
                    if (data.split("/") != null) {
                        String fileName = data.split("/")[0];
                        long size = Long.parseLong(data.split("/")[1]);
                        final int playTimes = Integer.parseInt(data.split("/")[2]);
                        PersistConfig.saveNormal(fileName, size, playTimes);
                        AlarmService.remotePlayMP3();
                    }
                }
            } else if (address == TcpPacketFactory.GET_FILE_SIZE) {
                /*7.3.3a 获取AMR格式音频文件大小*/
                //ra,01000002,abcdefgh.amr/123456789/000000000xxxx
                @SuppressLint("SdCardPath")
                String filePath = "/mnt/sdcard/flyscale/media/normal/" + data.split("/")[0];
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        FillZeroUtil.getString(data.split("/")[0] + "/" +
                                FillZeroUtil.getString(9, String.valueOf(FileHelper.getFileSize(new File(filePath)))) +
                                "/", 32)));
            } else if (address == TcpPacketFactory.GET_FILE_MD5) {
                /* 获取AMR格式音频文件MD5值*/
                //终端返回输出格式：
                //ra,01000006,12345678901234567890123456789012xxxx
                @SuppressLint("SdCardPath")
                String filePath = "/mnt/sdcard/flyscale/media/normal/" + data.split("/")[0];
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        FillZeroUtil.getString(32, MD5Util.md5(new File(filePath)))));
            } else if (TcpPacketFactory.MUSIC_SHOW_LIST.contains(address)) {
                /*7.3.3b设置音频文件播放节目*/
                /*
                文件名(最长16个字符)/
                开始播放时间(6字节)/
                结束播放时间(6字节)/
                播放音量(1字节)/
                是否播放前导音(1字节：0不播放；1播放)/
                星期控制字节
                 */
                String fileName = data.split("/")[0];
                String startTime = data.split("/")[1];
                String endTime = data.split("/")[2];
                String voice = data.split("/")[3];
                boolean isPlay = data.split("/")[4].equals("1");
                int week = Integer.parseInt(data.split("/")[5].substring(0, 3));
                Log.i(TAG, "channelRead0: isplay=" + isPlay);
                ShowProgram(fileName, startTime, endTime, voice, isPlay, week, address);
            } else if (address == TcpPacketFactory.CLEAR_ALL_MUSIC_SHOW) {
                /*清除所有音频节目列表*/
                DDLog.i("清除所有音频节目列表");
                alarmManagerUtil = AlarmManagerUtil.getInstance(BaseApplication.sContext);
                alarmManagerUtil.cancelAlarm();
            } else if (address == TcpPacketFactory.DOWNLOAD_AMR) {
                /*7.3.3bc 终端接收完AMR音频文件后反馈*/
                DDLog.i("终端接收完AMR音频文件后反馈");
                /*
                参数说明：
                第一个参数为文件名，
                第二个参数为成功接收的文件大小，如果接收失败，返回负的失败代码：
                    0	下载成功
                    -1	正在下载
                    -2	FTP域名解析失败
                    -3	服务器连接失败
                    -4	用户密码错误
                    -5	文件不存在
                    -6	下载失败
                    -7	磁盘空间不够
                    -8	解密失败
                    -9 解密中
                 */
                String fileName = data.split("/")[0];
                long size = Long.parseLong(data.split("/")[1]);

            } else if (address == TcpPacketFactory.PLAY_AMR) {
                /*7.3.3bd普通AMR文件播放反馈 ok*/
                DDLog.i("普通AMR文件播放反馈");
                /**
                 * 参数说明：
                 * 第一个参数为文件名，
                 * 第二个参数为音频文件节目行地址后三位，
                 * 第三个参数为播放前后标志：0播放前；1播放后
                 * 第四个参数为成功播放结果代码：
                 * 0	播放成功
                 * -21	没有播放文件
                 * -22	文件损坏
                 * -23	不可识别的文件
                 * 第五个参数为播放时的系统时间，该参数紧跟在第四个参数后面，没有/分隔符，格式为YYMMDDHHMISS：
                 * 该报文如果终端和设备暂时没有连接到平台无法发送，那么必须保存在终端中，待终端再次上线后逐条补发给平台。
                 */
                String fileName = data.split("/")[0];
                int program = Integer.parseInt(data.split("/")[1]);
                String beforePlay = data.split("/")[2];
                DDLog.i("播放前后标志" + beforePlay);
                if (data.split("/")[3] != null) {
                    Log.i(TAG, "结果加时间: " + data.split("/")[3]);
                }
//                int beforePlay = Integer.parseInt(data.split("/")[2]);
//                int result = Integer.parseInt(data.split("/")[3]);
            } else if (address == TcpPacketFactory.DOWNLOAD_UPDATE_PATCH) {
                /*7.3.4下传升级文件*/
                DDLog.i("下传升级文件");
                /**
                 * 参数说明
                 * 输入格式：
                 * wd,02000000,abcdefgh.gz/0123456789/020000/00xxxx
                 * 参数1：文件名
                 * 参数2：文件大小（字节,限制在10240000以内）
                 */
                String[] split = data.split("/");
                if (tcpPacket.getCmd() == CMD.WRITE) {
                    String fotaName = split[0];
                    long fotaFileSize = Long.parseLong(split[1]);
                    //TODO 下载升级文件，下载完成后立刻升级
                    if (fotaFileSize > 10240000) {
                        //超过大小限制
                        DDLog.i("超过大小限制10240000");
                    }

                    //下载升级文件并升级
                    downLoadCommonFile(fotaName, fotaFileSize, address);

                }
            } else if (address == TcpPacketFactory.UPDATE_SYSTEM) {
                /*7.3.5 终端接收完升级文件后反馈*/
                DDLog.i("终端接收完升级文件后反馈");
                /**
                 * 参数说明
                 * wd,01000004,abcdefgh.amr/1234567890/00000000xxxx
                 * 平台响应报文：
                 * wa,01000004,abcdefgh.amr/0000000000000000000xxxx
                 * 参数1：文件名
                 * 参数2：文件大小
                 */
                if (tcpPacket.getCmd() == CMD.WRITE) {
                    String[] split = data.split("/");
                    if (split.length > 1) {
                        String fotaName = split[0];
                        long fotaFileSize = Long.parseLong(split[1]);
                        //立刻反馈
                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address,
                                fotaName + "/" + addZero(fotaName + "/")));
                        //下载升级文件，但是不升级
                        downLoadCommonFile(fotaName, fotaFileSize, address);
                    }
                }

            } else if (address == TcpPacketFactory.COMMON_FILE_OPERATION_FTP) {
                /*7.3.6通用文件下载和删除*/
                DDLog.i("通用文件下载和删除");
                /**
                 * 平台下发报文：
                 * wd,02000005,abcdefgh.txt/0123456789/3C1d/0000xxxx
                 * 终端立刻反馈报文
                 * wa,02000005,abcdefgh.txt/00000000000000000000xxxx
                 *
                 * 参数说明：
                 * 参数1：文件名
                 * 参数2：文件大小 （0表示删除文件）
                 * 参数3：校验码（作废了）
                 */

                if (tcpPacket.getCmd() == CMD.WRITE) {
                    String[] split = data.split("/");
                    if (split.length > 1) {
                        String fileName = split[0];
                        long fileSize = Long.parseLong(split[1]);
                        //立刻反馈
                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address,
                                fileName + "/" + addZero(fileName + "/")));
                        if (fileSize > 0) {
                            //文件大小不为0，表示下载该文件
                            downLoadCommonFile(fileName, fileSize, address);
                        } else {
                            //大小为0，表示删除该文件
                            FileHelper.deleteFile(PersistConfig.COMMON_FILE_PATH + fileName);
                        }
                    }
                }

            } else if (address == TcpPacketFactory.COMMON_FILE_OPERATION) {
                /*7.3.7 通用文件下载反馈*/
                DDLog.i("通用文件下载反馈");
                /**
                 * 参数说明
                 *wd,02000006,abcdefgh.txt/1234567890/00000000xxxx
                 * 平台响应报文：
                 * wa,02000006,abcdefgh.txt/0000000000000000000xxxx
                 * 第一个参数：文件名
                 * 第二个参数：文件大小
                 */
                if (tcpPacket.getCmd() == CMD.WRITE) {
                    String[] split = data.split("/");
                    if (split.length > 1) {
                        String fileName = split[0];
                        long fileSize = Long.parseLong(split[1]);
                        //立刻反馈
                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address,
                                fileName + "/" + addZero(fileName + "/")));
                        if (fileSize > 0) {
                            //文件大小不为0，表示下载该文件
                            downLoadCommonFile(fileName, fileSize, address);
                        }
                    }
                }


            } else if (address == TcpPacketFactory.GET_COMMON_FILE_INFO) {
                /*7.3.7b 获取通用文件大小及校验码*/
                DDLog.i("获取通用文件大小和校验码");
                /**
                 * 参数说明
                 * 平台下发输入格式：
                 * rd,02000007,abcdefgh.txt/0000000000000000000xxxx
                 * 终端返回输出格式：
                 * ra,02000007,abcdefgh.txt/123456789/000000000xxxx
                 * 第一个参数：文件名
                 * 第二个参数：文件大小
                 *
                 */
                if (tcpPacket.getCmd() == CMD.READ) {
                    String[] split = data.split("/");
                    String fileName = split[0];
                    String filePath = PersistConfig.COMMON_FILE_PATH + fileName;
                    //判断文件是否存在
                    if (FileHelper.fileIsExists(filePath)) {
                        String fileSize = FileHelper.getFileOrFilesSize(filePath, FileHelper.SIZETYPE_B);
                        String nameAndSize = fileName + "/" + fileSize + "/";
                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                                nameAndSize + addZero(nameAndSize)));
                    } else {
                        DDLog.i("文件不存在");
                        //文件不存在（原因值 -5）
                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER,
                                address, "-5/" + addZero("-5/")));
                    }
                }

            } else if (address == TcpPacketFactory.BACKUP1) {
                /*7.3.8下传文件备份指令*/
                DDLog.i("下传文件备份指令");
                /**
                 * 参数说明
                 * 输入格式：
                 * wd,02000001,20180226190845073_15311228/00000xxxx
                 * 行地址为02000001，字段1为业务流水号。
                 */
                if (tcpPacket.getCmd() == CMD.WRITE) {
                    String[] split = data.split("/");
                    String num = split[0];
                    //备份文件
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                            address, "0/" + num + "/" + addZero("0/" + num + "/")));

                }
            } else if (address == TcpPacketFactory.BACKUP2) {
                /*7.3.9 终端备份文件系统结束后反馈*/
                DDLog.i("终端备份文件系统结束后反馈");
                /**
                 * 参数说明
                 * wa,02000002,0/20180226190845073_15311228/000xxxx
                 * 参数1：备份结果（0备份成功 ；-70备份失败）
                 * 参数2：业务流水号
                 */
                if (tcpPacket.getCmd() == CMD.WRITE) {
                    String[] split = data.split("/");
                    String num = split[0];
                    //备份文件
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                            address, "0/" + num + "/" + addZero("0/" + num + "/")));

                }
            } else if(FMLitepalUtil.isFmShow(address)) {
                /*7.3.11 设置FM普通广播节目*/
                String data = tcpPacket.getData();
                FMLitepalUtil.updateLine(address, data);
                /**
                 * 设置正常反馈代码为0,否则为错误编码,例如:
                 * wa,00000101,0/000000000000000000000000000000xxxx
                 * wa,00000101,-100/000000000000000000000000000xxxx
                 * */
                if(FMLitepalUtil.isUpdataSuccess(address,data)){
                    String result = "0/";
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address,
                            result + TcpPacketFactory.dataZero.substring(result.length())));
                }else{
                    String result = "-100/";
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address,
                            result + TcpPacketFactory.dataZero.substring(result.length())));
                }
            }else if(address == TcpPacketFactory.CLEAR_ALL_FM_SHOW){
                /*清除所有FM播放*/
                for (int i=1;i<33;i++){
                    FMUtil.cancelFMAlarmManager(BaseApplication.sContext,i);
                }
                FMUtil.stopFM(BaseApplication.sContext);
            }else if(address == TcpPacketFactory.CLEAR_ALL_BREAKING_FM_SHOW){
                /*清除所有FM播放*/
                for (int i=1;i<33;i++){
                    FMUtil.cancelBrFMAlarmManager(BaseApplication.sContext,i);
                }
                FMUtil.stopFM(BaseApplication.sContext);
            }else if(address == TcpPacketFactory.PLAY_FM){
                /*7.3.11b FM播放反馈*/
                String data = tcpPacket.getData();
                Log.e("fengpj","接收到平台的FM播放反馈");
            }else if(BreakFMLitepalUtil.isFmShow(address)) {
                /*7.3.12 设置FM插播广播节目*/
                String data = tcpPacket.getData();
                BreakFMLitepalUtil.updateLine(address, data);
                /**
                 * 设置正常反馈代码为0,否则为错误编码,例如:
                 * wa,00000101,0/000000000000000000000000000000xxxx
                 * wa,00000101,-100/000000000000000000000000000xxxx
                 * */
                if(BreakFMLitepalUtil.isUpdataSuccess(address,data)){
                    String result = "0/";
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address,
                            result + TcpPacketFactory.dataZero.substring(result.length())));
                }else{
                    String result = "-100/";
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address,
                            result + TcpPacketFactory.dataZero.substring(result.length())));
                }
            }else {
                //系统变量
                SystemVariable(address, tcpPacket);
            }


        }
        /*BaseData baseData = BaseDataFactory.getDataInstance(msg).formatToObject(msg);
        int type = BaseDataFactory.parseType(msg);
        final String tradeNum = baseData.getTradeNum();
        Log.i(TAG, "下行报文：channelRead0: 开始 ---------------------");
        Log.i(TAG, "报文内容：" + msg);
        Log.i(TAG, "报文类型type : " + type );
        Log.i(TAG, "下行报文：channelRead0: 结束 =====================");

        if(type == BaseData.TYPE_HEART_D){

        }else if(type == BaseData.TYPE_ALARM_D || type == BaseData.TYPE_DOOR_ALARM_D
                || type == BaseData.TYPE_SMOKE_ALARM_D || type == BaseData.TYPE_GAS_ALARM_D){
            //报警结果	STRING[1]	0表示正常接收，1没有正常接收，重发
            String result = baseData.getAlarmResult();
            if(TextUtils.equals(result,"0")){
                //ip报警成功
                IpAlarmInstance.getInstance().setStatus(IpAlarmInstance.STATUS_ALARM_SUCCESS);
            }
        }else if(type == BaseData.TYPE_RING_D){
            //响铃
//            NettyHelper.getInstance().send(new URing(baseData.getSendCount(),tradeNum));
        }else if(type == BaseData.TYPE_VOICE_D){
            //下载报警语音包
            final String hex = baseData.getMessageBodyResp();
            //总包数@包序号@设置状态@终端录音存储状态
            //设置状态:0表示成功，1表示失败
            //总包数:语音报分包总数
            //包序号:包序号,比如是第几个包
            //终端录音存储状态：0表示空间满，1表示空间不满，可以继续接受录音
            String message = baseData.getTotalPacket() + "@" + baseData.getPacketNum() + "@0@1";
//            NettyHelper.getInstance().send(new UVoice(baseData.getSendCount(),message,tradeNum));
            ThreadPool.getSyncInstance().execute(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "run: 下载文件");
                    FileHelper.byteToFile(DataConvertHelper.hexToBytes(hex),FileHelper.S_ALARM_RESP_NAME);
                    AlarmMediaPlayer.getInstance().playReceive(FileHelper.S_ALARM_RESP_FILE,3);
//                    ReceiveMediaInstance.getInstance().play(FileHelper.S_ALARM_RESP_FILE,3);
                }
            });
        }else if(type == BaseData.TYPE_CHANGE_ALARM_NUMBER_D){
            //修改报警号码
            String number = baseData.getAlarmNum();
            PersistConfig.saveAlarmNum(number);
//            NettyHelper.getInstance().send(new UChangeAlarmNumber("1@",tradeNum));
        }else if(type == BaseData.TYPE_CHANGE_IP_D){
            //修改ip
            String newIp = baseData.getIpAddress();
            String newPort = baseData.getIpPort();
            PersistConfig.saveNewIp(newIp, Integer.parseInt(newPort));
            NettyHelper.getInstance().disconnectByChangeIp(tradeNum);
        }else if(type == BaseData.TYPE_ADD_OR_DELETE_WHITE_LIST_D){
            //白名单 0添加1删除
            String flag = baseData.getAddOrDeleteFlag();
            String whiteList = baseData.getWhiteList();
            if(TextUtils.equals("0",flag))
                PersistWhite.saveList(whiteList);
            else if(TextUtils.equals("1",flag))
                PersistWhite.deleteList(whiteList);
//            NettyHelper.getInstance().send(new UAddDeleteWhiteList("1@",tradeNum));
        }else if(type == BaseData.TYPE_CHANGE_HEART_D){
            //修改心跳频率
            int heartHZ = baseData.getHeartHZ();
            NettyHelper.getInstance().modifyIdleStateHandler(heartHZ);
//            NettyHelper.getInstance().send(new UChangeHeart("1@",tradeNum));
        }else if(type == BaseData.TYPE_UPDATE_VERSION_D){
            //终端版本升级
            //总包数@包序号@接收状态@失败原因
            String total = baseData.getTotalPacket();
            String num = baseData.getPacketNum();
            NettyHelper.getInstance().modifyFota(total,num,tradeNum);
            //总包数@包序号@接收状态@失败原因
            //总包数:语音报分包总数
            //包序号:包序号,比如是第几个包
            //接收状态:0接收成功，1接收失败
            //失败原因：成功不填写（长度为0），失败填写原因
            //收到下行报文 直接回复升级成功的上行报文
//            String message = total + "@" + num + "@0@";
//            NettyHelper.getInstance().send(new UUpdateVersion(message,tradeNum));
        }else if(type == BaseData.TYPE_CHANGE_CLIENT_CA_D){
            //终端更换证书
            Log.i(TAG,
                    "channelRead0: 更换证书以前的ip及端口号......." + PersistConfig.findConfig().getIp() + PersistConfig.findConfig().getPort());
            String clientCa = baseData.getClientCaMessage();
            String clientKey = baseData.getClientPwdMessage();
            String rootCa = baseData.getRootCaMessage();
            final String ip = baseData.getIpAddress();
            final String port = baseData.getIpPort();
            final byte[] clientCaB = DataConvertHelper.hexToBytes(clientCa);
            final byte[] clientKeyB = DataConvertHelper.hexToBytes(clientKey);
            final byte[] rootCaB = DataConvertHelper.hexToBytes(rootCa);
            ThreadPool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    FileHelper.byteToFile(clientCaB,FileHelper.S_CLIENT_CRT_NAME);
                    FileHelper.byteToFile(clientKeyB, FileHelper.S_CLIENT_KEY_NAME);
                    FileHelper.byteToFile(rootCaB,FileHelper.S_ROOT_CRT_NAME);
                    //修改ca的同时  要修改ip  逻辑要包含修改ip
                    Log.i(TAG, "run: 要更换的ip及端口号 \n" + ip + Integer.parseInt(port));
                    PersistConfig.saveNewIp(ip, Integer.parseInt(port));
                    NettyHelper.getInstance().modifySslHandler(tradeNum);

                }
            });
        }*/
    }

    /**
     * 下载通用文件
     * @param fileName 文件名
     * @param size     文件大小
     * @param address  行地址
     */
    private void downLoadCommonFile(final String fileName, long size , final long address) {
        if (ClientInfoHelper.getAvailableSize() < size) {
            DDLog.i("磁盘空间不足，取消下载");
            //空间不足（原因值 -7）
            NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                    address, "-7/" + addZero("-7")));
            return;
        }
        //先删除后下载
        FileHelper.deleteFile(PersistConfig.COMMON_FILE_PATH + fileName);
        String url = LoginHelper.getHttpDownloadUrl() + fileName;
        HttpDownloadHelper.downloadFile(url, PersistConfig.COMMON_FILE_PATH, fileName, new DownloadListener2() {
            @Override
            public void taskStart(@NonNull DownloadTask task) {
                DDLog.i("开始下载通用文件... , " + task);
            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                DDLog.i("下载通用文件结束...");
                if (cause.equals(EndCause.ERROR) || cause.equals(EndCause.CANCELED)) {
                    DDLog.i("下载通用文件失败 ,  cause == " + cause);
                    //下载失败(原因值 -6)
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                            address, "-6/" + addZero("-6/")));
                } else if (cause.equals(EndCause.COMPLETED)) {
                    DDLog.i("下载通用文件成功");
                    if (address == TcpPacketFactory.DOWNLOAD_UPDATE_PATCH) {
                        //TODO 下载完成并升级
                        //终端版本升级
                        NettyHelper.getInstance().modifyFota();
                    } else {
                        if (TextUtils.equals(fileName , "whitelst.txt")) {
                            //下载的为白名单，添加到白名单列表
                            PersistWhite.deleteAllNum();
                            List<String> listWhite = FileHelper.readFileList(PersistConfig.COMMON_FILE_PATH + fileName);
                            DDLog.i("服务器下发白名单内容 == " + listWhite.toString());
                            PersistWhite.saveList(listWhite);
                        }
                        //下载成功（原因值 0）
                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                                address, "0/" + addZero("0/")));
                    }
                }
            }
        });
    }

    private void ShowProgram(final String fileName, final String startTime, final String endTime, final String voice, final boolean isPlay,
                             final int week, final long address) {
        /**
         * 根据指定的音频文件名，读写播放开始和结束时间
         * 输入格式：
         * wd,00000200,MP3A.amr/080000/090000/1/0/00000xxxx
         * wd,00000208,MP3D.amr/080000/090000/1/0/00000xxxx
         * wd,00000209,GUOGE.amr/080000/090000/1/0/1270xxxx
         *
         * 设置音频文件广播的行地址200-20f，最多16个节目，27F地址用作清除全部音频文件广播。每行数据格式为：
         * 文件名(最长16个字符)/开始播放时间(6字节)/结束播放时间(6字节)/播放音量(1字节)/是否播放前导音(1字节：0不播放；1播放)/星期控制字节
         * 设置正常反馈代码为0,否则为错误编码,例如:
         * wa,00000201,0/000000000000000000000000000000xxxx
         * wa,00000201,-100/000000000000000000000000000xxxx
         *
         * 星期控制字节（长度3字节）为0-127之间的整数，用二进制表示就是 0000-0000 到 0111-1111，
         * Bit0表示星期一是否播放(0播放、1不播放)；
         * Bit1表示星期二是否播放(0播放、1不播放)；
         */
        DDLog.i("地址=" + address);
        String url = LoginHelper.getHttpDownloadUrl() + fileName;//测试时使用
        final String path = Constants.FilePath.FILE_NORMAL;
        HttpDownloadHelper.downloadFile(url, path, fileName, new DownloadListener2() {
            @Override
            public void taskStart(@NonNull DownloadTask task1) {
                DDLog.i("下载开始" + task1);
            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                DDLog.i("下载结束" + cause);
                if (cause.equals(EndCause.ERROR) || cause.equals(EndCause.CANCELED)) {
                    //下载失败
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ,
                            TcpPacketFactory.DOWNLOAD_AMR, FillZeroUtil.getString("-6/", 32)));
                } else if (cause.equals(EndCause.COMPLETED)) {
                    try {
                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ, TcpPacketFactory.DOWNLOAD_AMR,
                                FillZeroUtil.getString(fileName + "/" + FillZeroUtil.getString(10,
                                        String.valueOf(FileHelper.getFileSize(new File(path + fileName)))) + "/", 32)
                        ));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < TcpPacketFactory.MUSIC_SHOW_LIST.size(); i++) {
                    if (address == TcpPacketFactory.MUSIC_SHOW_LIST.get(i)) {
                        // TODO: 2021/2/20  将字符转换为二进制，显示周几
                        String s = FillZeroUtil.getString(8, Integer.toBinaryString(week));
                        StringBuffer buf = new StringBuffer(s);
                        buf = buf.reverse();
                        ArrayList<Integer> list = new ArrayList<>();
                        for (int a = 0; a < buf.length(); a++) {
                            int index = buf.indexOf("0", a) + 1;
                            if (index != buf.length()) {
                                list.add(index);
                            }
                        }
                        removeDuplicate(list);
                        // TODO: 2021/2/20 设置周几定时播放
                        for (int j = 0; j < list.size(); j++) {
                            if (list.get(j) != null) {
                                Log.i(TAG, "initView: " + list.get(j));
                                Random random = new Random();
                                final int requestCode = random.nextInt(1000) + j;
                                alarmManagerUtil = AlarmManagerUtil.getInstance(BaseApplication.sContext);
                                alarmManagerUtil.getAlarmManagerStart(requestCode, list.get(j),
                                        startTime, endTime, fileName, voice, isPlay, address);
                            }
                        }
                    }
                }
            }
        });
    }

    //下载和删除
    @SuppressLint("SdCardPath")
    public static void DownLoadAndDelete(final String fileName, long size, final int playTimes) {
        /**
         * 输入格式：
         * wd,01000001,abcdefgh.amr/0123456789/30/13235xxxx
         * wd,01000001,abcdefgh.amr/0123456789/30/13235xxxx
         * 行地址为01000001，表明文件头，数据中1-12字节为文件名，14-23字节为文件原始大小，25-26字节为连续播放次数，每次播放间隔1秒。 如果文件大小是0，则表明需要删除改音频文件
         * 如果终端可以下载，         返回 wa,01000001,0/000000000000000000000000000000xxxx
         * 如果终端有下载正在进行中，  返回 wa,01000001,-1/00000000000000000000000000000xxxx
         * 如果终端FTP下载失败，      返回 wa,01000001,-2/00000000000000000000000000000xxxx
         * 如果终端没有足够的空间，    返回 wa,01000001,-3/00000000000000000000000000000xxxx
         * 如果FTP域名无法解析，     返回 wa,01000001,-10/0000000000000000000000000000xxxx
         * 如果FTP地址端口无法连接， 返回 wa,01000001,-11/0000000000000000000000000000xxxx
         * 如果FTP账户密码错误，     返回 wa,01000001,-12/0000000000000000000000000000xxxx
         * 如果FTP目录不存在，       返回 wa,01000001,-13/0000000000000000000000000000xxxx
         * 如果文件正在播放无法删除， 返回 wa,01000001,-15/0000000000000000000000000000xxxx
         * 终端收到指令先删除终端本地存储的该文件，再进行下载， 如果该文件正在播放，需返回-15
         */
        final String path = Constants.FilePath.FILE_NORMAL;
        String s = MusicPlayer.music.substring(MusicPlayer.music.lastIndexOf(File.separator)).replace("/", "");
        if (ClientInfoHelper.getAvailableSize() < size) {
            NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                    TcpPacketFactory.EMR_AMR_FILE_OPERATION, "-3/00000000000000000000000000000"));
        } else if (size == 0) {
            //删除该文件
            DDLog.i("删除该文件");
            if (new File(path + fileName).exists()) {
                FileHelper.deleteFile(path + fileName);
            }
        } else if (!s.equals(null) && s.equals(fileName)) {
            //正在播放，无法删除
            NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                    TcpPacketFactory.EMR_AMR_FILE_OPERATION, FillZeroUtil.getString(-15 + "/", 32)));
        } else {
            if (new File(path + fileName).exists()) {
                FileHelper.deleteFile(path + fileName);
            }
//            String url = PersistConfig.findConfig().getHttpDownloadUrl() + fileName;
            String url = LoginHelper.getHttpDownloadUrl() + fileName;//测试时使用
            HttpDownloadHelper.downloadFile(url, path, fileName,
                    new DownloadListener2() {
                        @Override
                        public void taskStart(@NonNull DownloadTask task1) {
                            DDLog.i("下载开始" + task1);
//                    if (task1.getInfo().isSameFrom(task)) {
//                        DDLog.i("重复");
//                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
//                                TcpPacketFactory.EMR_AMR_FILE_OPERATION, "-1/00000000000000000000000000000"));
//                    }
                        }

                        @Override
                        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                            DDLog.i("下载结束" + cause);
                            if (cause.equals(EndCause.ERROR) || cause.equals(EndCause.CANCELED)) {
                                //下载失败
                                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                                        TcpPacketFactory.EMR_AMR_FILE_OPERATION, "-2/00000000000000000000000000000"));
                            } else if (cause.equals(EndCause.COMPLETED)) {
                                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                                        TcpPacketFactory.EMR_AMR_FILE_OPERATION, "0/000000000000000000000000000000"));
                                // TODO: 2021/2/8 下载完成，设置播放列表
                                DDLog.i("下载完成，打印播放次数" + playTimes);
                                MusicPlayer.getInstance().playNormal(path + fileName, playTimes);
                            }
                        }
                    });
        }
    }


    //下载文件
    public static void DownLoadAmr(final String fileName, long size, final int playTimes, final String type) {
        Log.i("TAG", "DownLoadAmr: 下载紧急文件");
        /**
         * 输入格式：
         * 如果终端可以下载，         返回 wa,01000000,0/000000000000000000000000000000xxxx
         * 如果终端有下载正在进行中，  返回 wa,01000000,-1/00000000000000000000000000000xxxx
         * 如果终端FTP下载失败，      返回 wa,01000000,-2/00000000000000000000000000000xxxx
         * 如果终端没有足够的空间，    返回 wa,01000000,-3/00000000000000000000000000000xxxx
         */
        String url = LoginHelper.getHttpDownloadUrl() + fileName;//测试时使用
        @SuppressLint("SdCardPath") final String path = Constants.FilePath.FILE_EMR;
        final long address;
        if ("mp3".equals(type)) {
            address = TcpPacketFactory.EMR_BROADCAST_MP3_FTP;
        } else {
            address = TcpPacketFactory.EMR_BROADCAST_AMR_FTP;
        }

        if (size > ClientInfoHelper.getAvailableSize()) {
            NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address,
                    "-3/00000000000000000000000000000"));
        } else {
            DDLog.i("这里会下载i");
            HttpDownloadHelper.downloadFile(url, path, "JINJIMP3.AMR", new DownloadListener2() {
                @Override
                public void taskStart(@NonNull DownloadTask task1) {
                    DDLog.i("开始下载" + task1.toString());
                    //开始下载
//                    if (task1.getInfo().isSameFrom(task)) {
//                        DDLog.i("重复");
//                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address,
//                                "-1/00000000000000000000000000000"));
//                    }
                }

                @Override
                public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                    DDLog.i("下载结果" + cause);
                    if (cause.equals(EndCause.ERROR) || cause.equals(EndCause.CANCELED)) {
                        //下载失败
                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address,
                                "-2/00000000000000000000000000000"));
                    } else if (cause.equals(EndCause.COMPLETED)) {
                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address,
                                "0/000000000000000000000000000000"));
                        /**
                         * 参数说明：
                         * 第一个参数为文件名，
                         * 第二个参数为成功播放结果代码：
                         * 0	播放成功
                         * -31	没有播放文件
                         * -32	文件损坏
                         * -33	不可识别的文件
                         * 第三个参数为播放时间YYMMDDHHMISS：
                         * 该报文如果终端和设备暂时没有连接到平台无法发送，那么必须保存在终端中，待终端再次上线后逐条补发给平台。
                         */

                        String state = null;
                        long cmd;
                        String name = null;
                        if (address == TcpPacketFactory.EMR_BROADCAST_MP3_FTP) {
                            cmd = TcpPacketFactory.EMR_BROADCAST_MP3;
                            name = "JINJIMP3.AMR";
                        } else {
                            cmd = TcpPacketFactory.EMR_BROADCAST_AMR;
                            name = "JINJIMP3.AMR/0";
                        }
                        //播放紧急文件
                        MusicPlayer.getInstance().playTip(path + "JINJIMP3.AMR", true, playTimes);
                        File file = new File(path + "JINJIMP3.AMR");
                        if (!file.exists()) {
                            state = "-31";//文件不存在
                        } else if (!file.getName().endsWith(".mp3") && !file.getName().endsWith(".MP3") &&
                                !file.getName().endsWith(".amr") && !file.getName().endsWith(".AMR")) {
                            state = "-33";//不可识别的文件
                        } else {
                            state = "0";
                        }

                        //发送播放反馈
                        final long time = System.currentTimeMillis();
                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, cmd,
                                FillZeroUtil.getString(name + "/" + state + "/" +
                                        DateHelper.longToString(time, DateHelper.yyMMddHHmmss) + "/", 32)));
                    }
                }
            });
        }
    }

    //定时发送心跳
    private void LoginSuccess() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                final long time = System.currentTimeMillis();
                NettyHelper.getInstance().send(TcpPacketFactory.createPacketSend(TcpPacketFactory.HEARTBEAT_DATA,
                        FillZeroUtil.getString(3, String.valueOf(PhoneManagerUtil.getBatteryLevel(BaseApplication.sContext))) +
                                "/" + DateHelper.longToString(time, DateHelper.yyyyMMdd_HHmmss) + "/" +
                                PhoneManagerUtil.getBatteryStatus(BaseApplication.sContext) + "/" +
                                (float) (Math.round((PhoneManagerUtil.getBatteryVoltage(BaseApplication.sContext).floatValue() / 1000) * 10)) / 10 + "/" +
                                36 + "/" +
                                PhoneManagerUtil.getTamperSwitch(BaseApplication.sContext) + "/" + ClientInfoHelper.getMusicVolume()
                ));
            }
        }, 0, 5 * 60 * 1000);
        List<PersistPacket> list =
                LitePal.select("cmd", "address", "data").find(PersistPacket.class);
        for (int i = 0; i < list.size(); i++) {
            CMD cmd = CMD.getCMD(list.get(i).getCmd());
            long address = list.get(i).getAddress();
            String data = list.get(i).getData();
            if (address != 0) {
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(cmd, address, data));
            }
        }
    }

    //系统变量
    private void SystemVariable(long address, TcpPacket tcpPacket) {
        DDLog.d(getClass(), "SystemVariable()... ");
        String data = tcpPacket.getData();
        CMD cmd = tcpPacket.getCmd();
        if (TextUtils.isEmpty(data)) {
            DDLog.e(getClass(), "data数据内容为空");
            return;
        }
        DDLog.d(getClass(), "SystemVariable() , data ：" + data);
        String[] split = data.split("/");

        if (address == TcpPacketFactory.DEVICE_ID) {
            //设备出厂编号(只读) ra,00000009,cn-telcom/aren-aaluke-13-0000001xxxx
            if (cmd == CMD.READ) {
                String companyName = PhoneManagerUtil.getFactory();  //9位
                String MEID = PhoneManagerUtil.getMEID(BaseApplication.sContext);         //22位
                if (companyName.length() > 9) {
                    DDLog.e(getClass(), "厂家名字不能超过9位");
                    return;
                }
                companyName = companyName + TcpPacketFactory.dataZero.substring(0, 9 - companyName.length());
                MEID = MEID + TcpPacketFactory.dataZero.substring(0, 22 - MEID.length());
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        companyName + "/" + MEID));
            }

        } else if (address == TcpPacketFactory.SHORT_LINK_PARAM) {
            //短连接参数(可读可写)
            //参数1：链接类型(0短链接；1长链接)
            String linkType = "";
            //参数2：短链接休眠时长（秒）
            String shortLinkSleepTime = "";
            //参数3： 短链接工作等待延迟（秒）
            String shortLinkDelay = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 2) {
                    linkType = split[0];
                    shortLinkSleepTime = split[1];
                    shortLinkDelay = split[2];
                    // TODO 服务器下发的数据，修改设备中的短链接参数
                    PersistConfig.saveLinkType(linkType);
                    PersistConfig.saveShortLinkSleepTime(shortLinkSleepTime);
                    PersistConfig.saveShortLinkDelay(shortLinkDelay);
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                }
            } else if (cmd == CMD.READ) {
                //从设备中获取参数
                linkType = PersistConfig.findConfig().getLinkType();
                shortLinkSleepTime = PersistConfig.findConfig().getShortLinkSleepTime();
                shortLinkDelay = PersistConfig.findConfig().getShortLinkDelay();
                String shortLinkParam = linkType + "/" + shortLinkSleepTime + "/" + shortLinkDelay + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        shortLinkParam + addZero(shortLinkParam)));
            }
        } else if (address == TcpPacketFactory.FILE_DOWNLOAD_MODE_PARAM_1) {
            //文件下载模式参数1(可读可写) wd,0000000b,1/ostar/ikll*^AA/0000000000000000000xxxx
            //参数1：下载模式：0 ftp模式；1 http下载模式
            String mode = "";
            //参数2：http下载账户
            String httpAccount = "";
            //参数3：http下载密码
            String httpPwd = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 2) {
                    mode = split[0];
                    httpAccount = split[1];
                    httpPwd = split[2];
                    // TODO 服务器下发的数据，修改设备中的下载模式参数1
                    PersistConfig.saveDownloadMode(mode);
                    PersistConfig.saveHttpAccount(httpAccount);
                    PersistConfig.saveHttpPwd(httpPwd);
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                }
            } else if (cmd == CMD.READ) {
                //从设备中获取下载模式的参数
                mode = PersistConfig.findConfig().getDownloadMode();
                httpAccount = PersistConfig.findConfig().getHttpAccount();
                httpPwd = PersistConfig.findConfig().getHttpPwd();
                String fileDownloadModeParam = mode + "/" + httpAccount + "/" + httpPwd + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        fileDownloadModeParam + addZero(fileDownloadModeParam)));

            }

        } else if (address == TcpPacketFactory.FILE_DOWNLOAD_MODE_PARAM_2) {
            //文件下载模式参数2(可读可写) wd,0000000c,htp1.xjxlb.com:58003/0000000000xxxx
            //参数1：http下载域名端口
            String httpDownloadUrl = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    httpDownloadUrl = split[0];
                    // TODO 服务器下发的数据，修改设备中的下载模式参数2
                    PersistConfig.saveHttpDownloadUrl(httpDownloadUrl);
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                }
            } else if (cmd == CMD.READ) {
                //从设备中获取文件下载模式参数2
                httpDownloadUrl = PersistConfig.findConfig().getHttpDownloadUrl();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        httpDownloadUrl + "/" + addZero(httpDownloadUrl + "/")));
            }
        } else if (address == TcpPacketFactory.HARDWARE_VERSION) {
            //硬件版本号(只读) ra,00000021,xj-6850-v2.19b/00000000000000000xxxx
            if (cmd == CMD.READ) {
                String hardwareVersion = PhoneManagerUtil.getPlatform();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        hardwareVersion + "/" + addZero(hardwareVersion + "/")));
            }
        } else if (address == TcpPacketFactory.SOFTWARE_VERSION) {
            //软件版本号(只读) ra,00000022,fm-mp3-evdo-v3.50a/0000000000000xxxx
            if (cmd == CMD.READ) {
                String softwareVersion = PhoneManagerUtil.getDisplay();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        softwareVersion + "/" + addZero(softwareVersion + "/")));
            }
        } else if (address == TcpPacketFactory.EVDO_IP_ADDRESS) {
            //EVDO网络ip地址(只读) ra,00000026,192.168.111.123/0000000000000000xxxx
            if (cmd == CMD.READ) {
                String evdoIP = NetHelper.getIpAddressString();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        evdoIP + "/" + addZero(evdoIP + "/")));
            }
        } else if (address == TcpPacketFactory.VOLUME) {
            //音量(可读可写) wa,00000027,7/1/1/00000000000000000000000000xxxx
            // 参数1：音量0-b分12档（0挡为最低档没有声音，其余挡位逐渐加大）
            String musicVolume = "";
            //参数2：FM普通广播使能标志：1 使能，0 禁止
            String normalFmEnabled = "";
            //参数3：FM插播广播使能标志：1 使能，0 禁止
            String insertFmEnabled = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 2) {
                    musicVolume = split[0];
                    normalFmEnabled = split[1];
                    insertFmEnabled = split[2];
                    //TODO 服务器下发的数据，修改设备中的音量参数
                    ClientInfoHelper.setMusicVolume(musicVolume);
                    PersistConfig.saveNormalFmEnabled(normalFmEnabled);
                    PersistConfig.saveInsertFmEnabled(insertFmEnabled);
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                }
            } else if (cmd == CMD.READ) {
                //从设备中获取音量参数
                musicVolume = ClientInfoHelper.getMusicVolume();
                normalFmEnabled = PersistConfig.findConfig().getNormalFmEnabled();
                insertFmEnabled = PersistConfig.findConfig().getInsertFmEnabled();
                String volumeData = musicVolume + "/" + normalFmEnabled + "/" + insertFmEnabled + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        volumeData + addZero(volumeData)));
            }

        } else if (address == TcpPacketFactory.PLATFORM_SERVER_IP_ADDRESS_1) {
            //平台服务器ip地址1(可读可写) wd,00000028,xlb1.xjxlb.com:58005/0000000000xxxx
            //参数1：平台服务器域名1(这两个域名如果一个无法访问，终端应该切换至第二个域名)
            String ip_1 = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    ip_1 = split[0];
                    //TODO 服务器下发的数据，修改设备中的平台服务器域名1
                    String[] name1AndPort = ip_1.split(":");
                    if (name1AndPort.length > 1) {
                        PersistConfig.saveTcpHostNameRelease1(name1AndPort[0]);
                        PersistConfig.saveTcpPortRelease(Integer.valueOf(name1AndPort[1]));
                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                    }

                }
            } else if (cmd == CMD.READ) {
                //从设备中获取平台服务器域名1
                ip_1 = PersistConfig.findConfig().getTcpHostNameRelease1() + ":" + PersistConfig.findConfig().getTcpPortRelease();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        ip_1 + "/" + addZero(ip_1 + "/")));
            }
        } else if (address == TcpPacketFactory.PLATFORM_SERVER_IP_ADDRESS_2) {
            //平台服务器ip地址2(可读可写) wd,00000029,xlb1.xj-ict.com:58005/0000000000xxxx
            //参数1：平台服务器域名2(这两个域名如果一个无法访问，终端应该切换至第二个域名)
            String ip_2 = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    ip_2 = split[0];
                    //TODO 服务器下发的数据，修改设备中的平台服务器域名2
                    String[] name2AndPort = ip_2.split(":");
                    if (name2AndPort.length > 1) {
                        PersistConfig.saveTcpHostNameRelease2(name2AndPort[0]);
                        PersistConfig.saveTcpPortRelease(Integer.valueOf(name2AndPort[1]));
                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                    }
                }
            } else if (cmd == CMD.READ) {
                //从设备中获取平台服务器域名2
                ip_2 = PersistConfig.findConfig().getTcpHostNameRelease2() + ":" + PersistConfig.findConfig().getTcpPortRelease();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        ip_2 + "/" + addZero(ip_2 + "/")));
            }
        } else if (address == TcpPacketFactory.BASE_STATION_INFORMATION) {
            //基站信息(只读) rd,0000002a,36d0/000b/b0c1/0000/60/000000000xxxx
            if (cmd == CMD.READ) {
                //各个参数：SID/NID/BID/000/signal_level
                String[] cellInfo = NetHelper.getBaseData(BaseApplication.sContext).split(",");
                //从设备中获取参数
                String sid = cellInfo[0];
                String nid = cellInfo[1];
                String bid = cellInfo[2];
                String s = "0000";
                String singleLevel = PhoneUtil.getMobileDbm() + "";
                String baseStationInfo = sid + "/" + nid + "/" + bid + "/" + s + "/" + singleLevel + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        baseStationInfo + addZero(baseStationInfo)));
            }
        } else if (address == TcpPacketFactory.LOCATION_INFORMATION) {
            //位置信息(只读) rd,0000002b,E119.327833/N39.961949/000000000xxxx
            if (cmd == CMD.READ) {
                //参数1：经度
                String lon = LocationHelper.getLon();
                //参数2：纬度
                String lat = LocationHelper.getLat();
                //从设备中获取经纬度，发送给服务器
                String location = lon + "/" + lat + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        location + addZero(location)));
            }
        } else if (address == TcpPacketFactory.FTP_SERVER_IP_ADDRESS) {
            //FTP服务器IP地址(可读可写) wd,0000002c,ftp3.xjxlb.com:12345/00000000000xxxx
            // 参数1：FTP服务器IP
            String ftpAddress = "";
            if (cmd == CMD.WRITE) {
                ftpAddress = split[0];
                //TODO 服务器下发最新FTP服务器IP，修改设备中的该数据
                String[] ftpData = ftpAddress.split(":");
                if (ftpData.length > 1) {
                    PersistConfig.saveFtpHostNameRelease(ftpData[0]);
                    PersistConfig.saveFtpHostPortRelease(Integer.valueOf(ftpData[1]));
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                }

            } else if (cmd == CMD.READ) {
                //从设备中获取FTP服务器IP
                ftpAddress = PersistConfig.findConfig().getFtpHostNameRelease() + ":" + PersistConfig.findConfig().getFtpHostPortRelease();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        ftpAddress + "/" + addZero(ftpAddress)));
            }
        } else if (address == TcpPacketFactory.FTP_SERVER_ACCOUNT_PASSWORD) {
            //FTP服务器账户口令(可读可写) wd,0000002d,ftpuser@passwd111/00000000000000xxxx
            //参数1：FTP服务器账号
            String account = "";
            //参数2：FTP服务器账号对应的密码
            String pwd = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    String[] userAndPwd = split[0].split("@");
                    if (userAndPwd == null || userAndPwd.length < 2) {
                        DDLog.e(getClass(), "FTP服务器账号或密码为空");
                        return;
                    }
                    account = userAndPwd[0];
                    pwd = userAndPwd[1];
                    //TODO 服务器下发最新FTP服务器账号密码，修改设备中的该数据
                    PersistConfig.saveFtpUsernameRelease(account);
                    PersistConfig.saveFtpPasswordRelease(pwd);
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                }
            } else if (cmd == CMD.READ) {
                //从设备中获取FTP服务器账号密码
                account = PersistConfig.findConfig().getFtpUsernameRelease();
                pwd = PersistConfig.findConfig().getFtpPasswordRelease();
                String accountAndPwd = account + "@" + pwd + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        accountAndPwd + addZero(accountAndPwd)));
            }
        } else if (address == TcpPacketFactory.FOTA_FILE_FTP_DIR) {
            //升级文件FTP目录(可读可写) wd,0000002f,/china_telcom/3gdev,000000000000xxxx
            //参数1：升级目录（逗号前面的字符串）
            String ftpFotafilePath = "";
            if (cmd == CMD.WRITE) {
                String[] fotaDir = data.split(",");
                if (fotaDir.length > 0) {
                    ftpFotafilePath = fotaDir[0];
                    //TODO 服务器下发的最新升级目录，修改本地设备中的该数据
                    PersistConfig.saveFtpFotafilePath(ftpFotafilePath);
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                }
            } else if (cmd == CMD.READ) {
                //从设备中获取升级目录
                ftpFotafilePath = PersistConfig.findConfig().getFtpFotafilePath();;
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        ftpFotafilePath + "," + addZero(ftpFotafilePath + ",")));
            }
        } else if (address == TcpPacketFactory.AUDIO_FILE_FTP_DIR) {
            //音频文件FTP目录(可读可写) wd,00000031,/dev1,00000000000000000000000000xxxx
            //参数1：音频文件FTP目录（逗号前面的字符串）
            String ftpAudioFilepath = "";
            if (cmd == CMD.WRITE) {
                String[] audioDir = data.split(",");
                if (audioDir.length > 0) {
                    ftpAudioFilepath = audioDir[0];
                    //TODO 服务器下发的最新FTP音频目录，修改设备中该数据
                    PersistConfig.saveFtpAudioFilepath(ftpAudioFilepath);
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                }
            } else if (cmd == CMD.READ) {
                //从设备中获取音频文件FTP目录
                ftpAudioFilepath = PersistConfig.findConfig().getFtpAudioFilepath();;
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        ftpAudioFilepath + "," + addZero(ftpAudioFilepath + "," )));
            }
        } else if (address == TcpPacketFactory.LONG_LINK_HEARTBEAT_INTERVAL) {
            //长链接心跳间隔(秒)[可读可写] wd,00000030,20/300/1/13309910000/00000000000xxxx
            //参数1：长连接心跳间隔(秒)
            String longLinkHeartbeat = "";
            //参数2：长连接登录延迟(秒)
            String longLinkSignDelay = "";
            //参数3：长链接短链接选择 (固定为0:短连接 ;固定为1:长连接)
            String linkType = "";
            //参数4：平台短信号码
            String platformSmsNum = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 3) {
                    longLinkHeartbeat = split[0];
                    longLinkSignDelay = split[1];
                    linkType = split[2];
                    platformSmsNum = split[3];
                    //TODO 服务器下发的最新参数，修改设备中的该参数
                    PersistConfig.saveLongLinkHeartbeat(longLinkHeartbeat);
                    PersistConfig.saveLongLinkSignDelay(longLinkSignDelay);
                    PersistConfig.savePlatformSmsNum(platformSmsNum);
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                }
            } else if (cmd == CMD.READ) {
                //获取设备中的长链接心跳参数
                longLinkHeartbeat = PersistConfig.findConfig().getLongLinkHeartbeat();
                longLinkSignDelay = PersistConfig.findConfig().getLongLinkSignDelay();
                linkType = "1";
                platformSmsNum = PersistConfig.findConfig().getPlatformSmsNum();
                String longLinkParam = longLinkHeartbeat + "/" + longLinkSignDelay + "/" + linkType + "/" + platformSmsNum + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        longLinkParam + addZero(longLinkParam)));
            }
        } else if (address == TcpPacketFactory.DEVICE_RESET) {
            //终端复位(可读可写) wd,00000032,00000000000000000000000000000000xxxx
            if (cmd == CMD.WRITE) {
                if (data.equals(TcpPacketFactory.dataZero)) {
                    //TODO 服务器写入全0数据，代表平台要求终端先复位重新启动（网络通讯模块也需要重新启动）
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                    PhoneManagerUtil.reboot(BaseApplication.sContext , BaseApplication.sContext.getResources().getString( R.string.reset));
                }
            } else if (cmd == CMD.READ) {

            }

        } else if (address == TcpPacketFactory.CALL_COMMAND_PARAM) {
            //拨打电话指令参数（可读可写） wd,00000040,18909910000/10/201706051130/03xxxx
            //参数1：拨打的电话号码
            String phoneNum = "";
            //参数2：每次通话时长（秒）
            String callTime = "";
            //参数3：拨打电话的时间（年月日时分）
            String callDate = "";
            //参数4：拨打失败后的重试次数
            String times = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 3) {
                    phoneNum = split[0];
                    callTime = split[1];
                    callDate = split[2];
                    times = split[3];
                    //TODO 服务器最新下发数据，修改设备中的该参数
                    PersistConfig.savePhoneNum(phoneNum);
                    PersistConfig.saveCallTime(callTime);
                    PersistConfig.saveCallDate(callDate);
                    PersistConfig.saveCallTimes(times);
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                }
            } else if (cmd == CMD.READ) {
                //从设备中获取以下参数
                phoneNum = PersistConfig.findConfig().getPhoneNum();
                callTime = PersistConfig.findConfig().getCallTime();
                callDate = PersistConfig.findConfig().getCallDate();
                times = PersistConfig.findConfig().getTimes();
                String callCommandParam = phoneNum + "/" + callTime + "/" + callDate + "/" + times + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        callCommandParam + addZero(callCommandParam)));
            }
        } else if (address == TcpPacketFactory.FUNCTION_1_CALL_PHONE_NUM) {
            //功能键 1 拨打电话号码（可写） wd,00000041,18909910000/000000000000000000xxxx
            //参数1：功能键1对应电话号码 0为取消
            String funcOneNum = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    funcOneNum = split[0];
                    //TODO 服务器最新下发的功能键1的号码，修改设备中的该数据(0:取消功能)
                    PersistConfig.saveKey1Num(funcOneNum);
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                }
            } else if (cmd == CMD.READ) {
                funcOneNum = PersistConfig.findConfig().getKey1Num();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        funcOneNum + "/" + addZero(funcOneNum + "/")));
            }

        } else if (address == TcpPacketFactory.FUNCTION_2_CALL_PHONE_NUM) {
            //功能键 2 拨打电话号码(可写) wd,00000042,18909910000/000000000000000000xxxx
            //参数1：功能键2对应电话号码 0为取消
            String funcTwoNum = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    funcTwoNum = split[0];
                    //TODO 服务器最新下发的功能键2的号码，修改设备中的该数据(0:取消功能)
                    PersistConfig.saveKey2Num(funcTwoNum);
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                }
            } else if (cmd == CMD.READ) {
                funcTwoNum = PersistConfig.findConfig().getKey2Num();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        funcTwoNum + "/" + addZero(funcTwoNum + "/")));
            }

        } else if (address == TcpPacketFactory.FUNCTION_3_CALL_PHONE_NUM) {
            //功能键 3 拨打电话号码（可写）wd,00000043,18909910000/000000000000000000xxxx
            //参数1：功能键3对应电话号码 0为取消
            String funcThreeNum = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    funcThreeNum = split[0];
                    //TODO 服务器最新下发的功能键3的号码，修改设备中的该数据(0:取消功能)
                    PersistConfig.saveKey3Num(funcThreeNum);
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                }
            } else if (cmd == CMD.READ) {
                funcThreeNum = PersistConfig.findConfig().getKey3Num();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        funcThreeNum + "/" + addZero(funcThreeNum + "/")));
            }
        } else if (address == TcpPacketFactory.FUNCTION_4_CALL_PHONE_NUM) {
            //功能键 4 拨打电话号码（可写）wd,00000044,18909910000/000000000000000000xxxx
            //参数1：功能键4对应电话号码 0为取消
            String funcFourNum = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    funcFourNum = split[0];
                    //TODO 服务器最新下发的功能键4的号码，修改设备中的该数据(0:取消功能)
                    PersistConfig.saveKey4Num(funcFourNum);
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                }
            } else if (cmd == CMD.READ) {
                funcFourNum = PersistConfig.findConfig().getKey4Num();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        funcFourNum + "/" + addZero(funcFourNum + "/")));
            }

        } else if (address == TcpPacketFactory.DEVICE_AVAILABLE_SIZE) {
            //平台获取终端可用存储空间大小（只读），查询指令：rd,00000045,000000000000000000000000000000xxxx
            if (cmd == CMD.READ) {
                if (data.equals(TcpPacketFactory.dataZero)) {
                    //可用存储全部大小
                    String totalMem = ClientInfoHelper.getTotalSize() + "";
                    //可用存储空闲大小
                    String availMem = ClientInfoHelper.getAvailableSize() + "";
                    String totalAndAvail = totalMem + "/" + availMem + "/";
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                            totalAndAvail + addZero(totalAndAvail)));
                }
            }
        } else if (address == TcpPacketFactory.QUERY_TIME_FORM_PLATFORM) {
            //平台查询终端系统当前时间（只读），查询指令：rd,00000046,000000000000000000000000000000xxxx
            if (cmd == CMD.READ) {
                if (data.equals(TcpPacketFactory.dataZero)) {
                    //时间格式为yyyyMMddHHmmss
                    String systemTime = new SimpleDateFormat(DateHelper.yyyyMMddHHmmss).format(new Date(System.currentTimeMillis()));
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                            systemTime + "/" +addZero(systemTime + "/")));
                }
            }
        } else if (address == TcpPacketFactory.DEVICE_PERSON_FUNCTION) {
            //终端个性化功能（可读可写）
            //第1个字符: 0 APP紧急语音单次播放; 1：APP紧急语音循环播放
            String emrPlayMode = "";
            //第2个字符: 0防移开关启用; 1防移开关禁用
            String moveSwitch = "";
            //第3个字符:
            // 0一键报警普通模式（十户联防号码呼入：用户按键接听）;
            // 1一键报警奎屯模式（十户联防号码呼入后：响3声报警音，再自动接听，播放完成报警信息后，用户挂断）
            //2：一键报警沙湾模式（十户联防号码呼入后：不响报警音，自动接听，播放完成报警信息后，用户挂断；）
            String alarmMode = "";
            //第4个字符: 1就是只能拨打报警与快捷键; 0可以拨打所有电话
            String callEnable = "";
            //第5个字符,“频选”参数 ：0 默认; 1 4G-800M优选;  2:4G-1800M优选
            String channelSelect = "";
            //第6个字符,“WIFI开关”参数 ：0 开通; 1 关闭
            String wifiSwitch = "";

            if (cmd == CMD.WRITE) {
                emrPlayMode = data.substring(0, 1);
                moveSwitch = data.substring(1, 2);
                alarmMode = data.substring(2, 3);
                callEnable = data.substring(3, 4);
                channelSelect = data.substring(4, 5);
                wifiSwitch = data.substring(5, 6);
                //TODO 服务器下发的最新参数，修改设备中的该参数
                PersistConfig.saveEmrPlayMode(emrPlayMode);
                PersistConfig.saveMoveSwitch(moveSwitch);
                PersistConfig.saveAlarmMode(alarmMode);
                PersistConfig.saveCallEnabled(callEnable);
                PersistConfig.saveChannelSelect(channelSelect);
                PersistConfig.saveWifiSwitch(wifiSwitch);
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));

            } else if (cmd == CMD.READ) {
                //从设备中获取参数
                emrPlayMode = PersistConfig.findConfig().getEmrPlayMode();
                moveSwitch = PersistConfig.findConfig().getMoveSwitch();
                alarmMode = PersistConfig.findConfig().getAlarmMode();
                callEnable = PersistConfig.findConfig().getCallEnable();
                channelSelect = PersistConfig.findConfig().getChannelSelect();
                wifiSwitch = PersistConfig.findConfig().getWifiSwitch();
                String devicePersonFunc = emrPlayMode + moveSwitch + alarmMode + callEnable + channelSelect + wifiSwitch;
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        devicePersonFunc + addZero(devicePersonFunc)));
            }
        } else if (address == TcpPacketFactory.PLATFORM_PHONE_NUM) {
            //一键报警平台电话号码(可写) wd,00000048,18909910000/000000000000000000xxxx
            //参数1：一键报警平台对应电话号码 ; 0为取消
            String alarmNum = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    alarmNum = split[0];
                    //TODO 服务器下发最新的一键报警平台电话号码，修改设备中的数据(0 取消功能)
                    PersistConfig.saveAlarmNum(alarmNum);
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
                }
            } else if (cmd == CMD.READ) {
                alarmNum = PersistConfig.findConfig().getAlarmNum();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        alarmNum + "/" + addZero(alarmNum + "/")));
            }

        } else if (address == TcpPacketFactory.ALARM_VOLUME) {
            //接警音量(可读可写) wd,00000049,1/0000000000000000000000000000xxxx
            //参数1：接警音量: 1 1档; 2 2档; 3 3档; 4 4档
            String alarmVolume = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    alarmVolume = split[0];
                    //TODO 服务器下发接警音量，修改设备中的数据
                    ClientInfoHelper.setCallVolume(alarmVolume);
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));

                }
            } else if (cmd == CMD.READ) {
                //从设备中获取接警音量
                alarmVolume = ClientInfoHelper.getCallVolume();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        alarmVolume + "/" + addZero(alarmVolume + "/")));
            }
        } else if (address == TcpPacketFactory.BATCH_DEL_FILE) {
            //批量删除文件指令(可写) wd,0000004a,ABCDEFGHIJKLMNOP/0000000000000xxxx
            //参数1：批量删除的文件名缩写，代表含义为：A=MP3A.amr; B=MP3B.amr... P=MP3P.amr
//            String fliesName = "";
//            if (cmd == CMD.WRITE) {
//                if (split.length > 0) {
//                    fliesName = split[0];
//                    //TODO 服务器下发要删除的文件名缩写，执行删除操作
//                    char[] names = fliesName.toCharArray();
//                    for (int i = 0 ; i < names.length ; i++) {
//                        FileHelper.deleteFile(MusicPlayer.MEDIA_PATH + "MP3" + names[i] + ".amr");
//                    }
//                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address, TcpPacketFactory.dataZero));
//                }
//            }
        } else if (address == TcpPacketFactory.DEVICE_SHORT_LINK_SLEEP) {
            //设备进入短链接休眠（可写）wd,0000004b,00000000000000000000000000000000xxxx
            if (cmd == CMD.WRITE) {
                if (data.equals(TcpPacketFactory.dataZero)) {
                    //平台发给设备wd，表示设备进入通讯休眠等待，平台收到设备的确认指令wa后，断开连接
                    //确认指令：wa,0000004b,00000000000000000000000000000000xxxx
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address,
                            TcpPacketFactory.dataZero));
                }
            }

        } else if (address == TcpPacketFactory.AUDIO_LIVE_IP_ADDRESS_AND_PORT) {
            //音频直播频道服务器地址和端口(可读可写) wd,00000050,0,au.xjxlb.com:58007,00000000000xxxx
            //参数1：频道编号，0开始
            String channelNum = "";
            //参数2：频道音频流的服务器ip和端口
            String ipAndPort = "";
            if (cmd == CMD.WRITE) {
                String[] audioLive = data.split(",");
                if (audioLive.length > 1) {
                    channelNum = audioLive[0];
                    ipAndPort = audioLive[1];
                    //TODO 修改设备中的该参数数据


                }
            } else if (cmd == CMD.READ) {
                //获取本地存储的参数
                channelNum = "";
                ipAndPort = "";
                String audioLiveChannelAndIP = channelNum + "," + ipAndPort + ",";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        audioLiveChannelAndIP + addZero(audioLiveChannelAndIP)));
            }

        } else if (address == TcpPacketFactory.AUDIO_LIVE_ACCOUNT_PASSWORD) {
            //音频直播频道账户密码(可读可写) wd,00000051,0,auplay,s1Wi_v8X,00000000000000xxxx
            //参数1：频道编号，0开始
            String channelNum = "";
            //参数2：流媒体服务器的账户
            String account = "";
            //参数3：流媒体服务器的密码
            String pwd = "";
            if (cmd == CMD.WRITE) {
                String[] accountAndPwd = data.split(",");
                if (accountAndPwd.length > 2) {
                    channelNum = accountAndPwd[0];
                    account = accountAndPwd[1];
                    pwd = accountAndPwd[2];
                    //TODO 修改设备中的的这几个参数


                }
            } else if (cmd == CMD.READ) {
                //获取设备中的数据
                channelNum = "";
                account = "";
                pwd = "";
                String audioLiveAccountAndPwd = channelNum + "," + account + "," + pwd + ",";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        audioLiveAccountAndPwd + addZero(audioLiveAccountAndPwd)));
            }
        } else if (address == TcpPacketFactory.AUDIO_LIVE_START_AND_END_TIME) {
            //音频直播频道开始结束时间(可读可写) wd,00000052,0,100001,113000,5,00000000000000xxxx
            //参数1：频道编号，0开始
            String channelNum = "";
            //参数2：直播开始时间，小时分钟秒
            String startTime = "";
            //参数3：直播结束时间，小时分钟秒
            String endTime = "";
            //参数4：直播音量，0-9
            String volume = "";
            if (cmd == CMD.WRITE) {
                String[] liveTime = data.split(",");
                if (liveTime.length > 3) {
                    channelNum = liveTime[0];
                    startTime = liveTime[1];
                    endTime = liveTime[2];
                    volume = liveTime[3];
                    //TODO 修改设备中的以上几个参数


                }
            } else if (cmd == CMD.READ) {
                //获取设备中的参数
                channelNum = "";
                startTime = "";
                endTime = "";
                volume = "";
                String audioLiveStartAndEnd = channelNum + "," + startTime + "," + endTime + "," + volume + ",";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        audioLiveStartAndEnd + addZero(audioLiveStartAndEnd)));
            }

        } else if (address == TcpPacketFactory.AUDIO_LIVE_CHANNEL_URL) {
            //音频直播频道url(可读可写) wd,00000053,0,audio_play,0000000000000000000xxxx
            //参数1：频道编号，0开始
            String channelNum = "";
            //参数2：频道音频流的url第1部分, url最大长度32字符
            String url = "";
            if (cmd == CMD.WRITE) {
                String[] channelAndUrl = data.split(",");
                if (channelAndUrl.length > 1) {
                    channelNum = channelAndUrl[0];
                    url = channelAndUrl[1];
                    //TODO 修改设备中的参数


                }
            } else if (cmd == CMD.READ) {
                channelNum = "";
                url = "";
                String audioLiveChannelUrl = channelNum + "," + url + ",";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        audioLiveChannelUrl + addZero(audioLiveChannelUrl)));
            }

        } else if (address == TcpPacketFactory.FM_FIXED_CHANNEL_SETTINGS) {
            //FM固定频道设置(可读可写) wd,00000055,1/96.5/93.5/000000000000000xxxx
            //1、固定FM频道到设置的值，可以设置多个，每条指令最多设置4个频道；
            //2、设置后，终端平台控制播放，包括本地播放必须全部锁定这个频道
            //3、如果平台下发FM播放或者插播的频道不在这个频道范围，终端直接反馈播放停止指令；
            //4、频道从1开始
            //5、如果频道数字为0，表明清除所有频道。
            //6、该指令要求可以查询

            //wd,00000055,1/96.5/93.5/000000000000000xxxx
            //wd,00000055,2/96.5/93.5/000000000000000xxxx
            //wa,00000055,0/0000000000000000000000000XXXX
            //rd,00000055,000000000000000000000000000XXXX
            //ra,00000055,2/96.5/93.5/000000000000000xxxx

            if (cmd == CMD.WRITE) {
                if (TextUtils.equals(TcpPacketFactory.dataZero, data)) {
                    //TODO 频道字数为0 ，清除所有频道

                    return;
                }
                String[] dataList = data.split("/");
                String[] channelArray = new String[dataList.length - 1];
                System.arraycopy(dataList, 0, channelArray, 0, channelArray.length);
                //channelArray为设置的固定频道列表


            } else if (cmd == CMD.READ) {

            }
        }
    }

    /**
     * 补0；
     * @param str 需要补0的字符串
     * @return 全0
     */
    private String addZero(String str) {
        if (str.length() > 31) {
            str = str.substring(0 , 32);
        }
        return TcpPacketFactory.dataZero.substring(str.length());
    }

}
