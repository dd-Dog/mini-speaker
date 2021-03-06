package com.flyscale.alertor.netty;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.DownloadListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.packet.CMD;
import com.flyscale.alertor.data.packet.TcpPacket;
import com.flyscale.alertor.data.packet.TcpPacketFactory;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.helper.ClientInfoHelper;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.DateHelper;
import com.flyscale.alertor.helper.FileHelper;
import com.flyscale.alertor.helper.HttpDownloadHelper;
import com.flyscale.alertor.helper.MD5Util;
import com.flyscale.alertor.helper.PhoneManagerUtil;
import com.flyscale.alertor.helper.PhoneUtil;
import com.flyscale.alertor.helper.UserActionHelper;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.jni.NativeHelper;
import com.flyscale.alertor.led.LedInstance;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.DownloadListener2;
import com.liulishuo.okdownload.core.listener.DownloadListener3;
import com.liulishuo.okdownload.core.listener.DownloadListener4;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 9:51
 * @DESCRIPTION 暂无
 */
public class NettyHandler extends SimpleChannelInboundHandler<TcpPacket> {

    String TAG = "NettyHandler";
    String data;


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
        Log.i(TAG, "channelActive:  ---  链接成功 ---");
        //保存第一次登陆的时间 永久不变
        PersistConfig.saveFirstLoginTime(System.currentTimeMillis());
        if (!UserActionHelper.isFastConnect(120 * 1000)) {
            MediaHelper.play(MediaHelper.SERVER_CONNECT_SUCCESS, true);
        }
        LedInstance.getInstance().showStateLed();

        //开始鉴权
        //TcpPacketFactory.LOGIN, "460031234567890/0A9464026708209/")
        NettyHelper.getInstance().send(TcpPacketFactory.createPacketSend(BaseApplication.sContext, TcpPacketFactory.LOGIN));
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
            if (((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
                long time = System.currentTimeMillis();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, TcpPacketFactory.HEARTBEAT_DATA,
                        PhoneManagerUtil.getBatteryLevel(BaseApplication.sContext) + "/" +
                                DateHelper.longToString(time, DateHelper.yyyyMMdd_HHmmss) + "/" +
                                PhoneManagerUtil.getBatteryStatus(BaseApplication.sContext) + "/" +
                                (float) (Math.round((PhoneManagerUtil.getBatteryVoltage(BaseApplication.sContext).floatValue() / 1000) * 10)) / 10 + "/" +
                                36 + "/" +
                                PhoneManagerUtil.getTamperSwitch(BaseApplication.sContext) + "/" + ClientInfoHelper.getVolume()
                ));
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
                            PersistConfig.saveLogin(true);
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
                /*
                输入格式：
                    如果终端可以下载，         返回 wa,01000000,0/000000000000000000000000000000xxxx
                    如果终端有下载正在进行中，  返回 wa,01000000,-1/00000000000000000000000000000xxxx
                    如果终端FTP下载失败，      返回 wa,01000000,-2/00000000000000000000000000000xxxx
                    如果终端没有足够的空间，    返回 wa,01000000,-3/00000000000000000000000000000xxxx
                 */
                if (!TextUtils.isEmpty(data)) {
                    if (data.split("/") != null) {
                        String fileName = data.split("/")[0];
                        long size = Long.parseLong(data.split("/")[1]);
                        final int playTimes = Integer.parseInt(data.split("/")[2]);
                        if (size > ClientInfoHelper.getAvailableSize()) {
                            NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address,
                                    "-3/00000000000000000000000000000"));
                            return;
                        }
                        String url = "http://ftp3.xjxlb.com:58021/au/hnkt01/5aad974525934efb/" + "MP3C.amr";

                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                                TcpPacketFactory.EMR_BROADCAST_MP3_FTP, "-2/00000000000000000000000000000"));
                        HttpDownloadHelper.downloadFile(url, "dada/dada/Alertor/", fileName, new DownloadListener2() {
                            @Override
                            public void taskStart(@NonNull DownloadTask task) {
                                //开始下载
                                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                                        TcpPacketFactory.EMR_BROADCAST_MP3_FTP, "0/000000000000000000000000000000"));
                            }

                            @Override
                            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                                if (cause.equals(EndCause.ERROR) || cause.equals(EndCause.CANCELED)) {
                                    //下载失败
                                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                                            TcpPacketFactory.EMR_BROADCAST_MP3_FTP, "-2/00000000000000000000000000000"));
                                } else if (cause.equals(EndCause.COMPLETED)) {
                                    //下载完成，设置播放列表
                                    DDLog.i("下载完成，打印播放次数" + playTimes);
                                }
                            }
                        });
                    }
                }
            } else if (address == TcpPacketFactory.EMR_BROADCAST_MP3) {
                /*7.3.2b下传紧急广播AMR文件播放反馈（内容是MP3格式，文件名后缀是amr）*/
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
                String fileName = data.split("/")[0];
                int result = Integer.parseInt(data.split("/")[1]);
                DDLog.i("下传紧急广播AMR文件播放反馈（内容是MP3格式，文件名后缀是amr）");

            } else if (address == TcpPacketFactory.EMR_BROADCAST_AMR_FTP) {
                /*7.3.2a下传紧急广播AMR文件（内容是MP3格式，文件名后缀是amr）*/
                /*
                输入格式：
                    wd,01000009,abcdefgh.amr/0123456789/30/13235xxxx
                    如果终端可以下载，         返回 wa,01000000,0/000000000000000000000000000000xxxx
                    如果终端有下载正在进行中，  返回 wa,01000000,-1/00000000000000000000000000000xxxx
                    如果终端FTP下载失败，      返回 wa,01000000,-2/00000000000000000000000000000xxxx
                    如果终端没有足够的空间，    返回 wa,01000000,-3/00000000000000000000000000000xxxx
                 */
                DDLog.i("下传紧急广播AMR文件（内容是amr格式，文件名后缀是amr）" + data);
                if (!TextUtils.isEmpty(data)) {
                    if (data.split("/") != null) {
                        String fileName = data.split("/")[0];
                        long size = Long.parseLong(data.split("/")[1]);
                        final int playTimes = Integer.parseInt(data.split("/")[2]);
                        if (size > ClientInfoHelper.getAvailableSize()) {
                            NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER, address,
                                    "-3/00000000000000000000000000000"));
                            return;
                        }
                        String url = "http://ftp3.xjxlb.com:58021/au/hnkt01/5aad974525934efb/" + "MP3C.amr";
                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                                TcpPacketFactory.EMR_BROADCAST_AMR_FTP, "-2/00000000000000000000000000000"));
                        HttpDownloadHelper.downloadFile(url, "dada/dada/Alertor/", fileName, new DownloadListener2() {
                            @Override
                            public void taskStart(@NonNull DownloadTask task) {

                            }

                            @Override
                            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                                if (cause.equals(EndCause.ERROR) || cause.equals(EndCause.CANCELED)) {
                                    //下载失败
                                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                                            TcpPacketFactory.EMR_BROADCAST_AMR_FTP, "-2/00000000000000000000000000000"));
                                } else if (cause.equals(EndCause.COMPLETED)) {
                                    //下载完成，设置播放列表
                                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                                            TcpPacketFactory.EMR_AMR_FILE_OPERATION, "0/000000000000000000000000000000"));
                                    DDLog.i("下载完成，打印播放次数" + playTimes);
                                }
                            }
                        });
                    }
                }
            } else if (address == TcpPacketFactory.EMR_BROADCAST_AMR) {
                /*7.3.2d下传紧急广播AMR文件播放反馈（内容是amr格式，文件名后缀是amr）*/
                DDLog.i("下传紧急广播AMR文件播放反馈（内容是amr格式，文件名后缀是amr）");
                /**
                 * 参数说明：
                 * 第一个参数为文件名，
                 * 第二个参数为播放前后标志：0播放前；1播放后
                 * 第三个参数为成功播放结果代码：
                 * 0	播放成功
                 * -31	没有播放文件
                 * -32	文件损坏
                 * -33	不可识别的文件
                 * 第四个参数为播放时间YYMMDDHHMISS：
                 * 该报文如果终端和设备暂时没有连接到平台无法发送，那么必须保存在终端中，待终端再次上线后逐条补发给平台。
                 */
                String fileName = data.split("/")[0];
                int beforePlay = Integer.parseInt(data.split("/")[1]);
                int result = Integer.parseInt(data.split("/")[2]);
                String time = data.split("/")[3];


            } else if (address == TcpPacketFactory.EMR_AMR_FILE_OPERATION) {
                /*7.3.3下传和删除普通AMR格式音频文件*/
                /*
                输入格式：
                    wd,01000001,abcdefgh.amr/0123456789/30/13235xxxx
                    wd,01000001,abcdefgh.amr/0123456789/30/13235xxxx
                    行地址为01000001，表明文件头，数据中1-12字节为文件名，14-23字节为文件原始大小，25-26字节为连续播放次数，
                    每次播放间隔1秒。 如果文件大小是0，则表明需要删除改音频文件
                        如果终端可以下载，         返回 wa,01000001,0/000000000000000000000000000000xxxx
                        如果终端有下载正在进行中，  返回 wa,01000001,-1/00000000000000000000000000000xxxx
                        如果终端FTP下载失败，      返回 wa,01000001,-2/00000000000000000000000000000xxxx
                        如果终端没有足够的空间，    返回 wa,01000001,-3/00000000000000000000000000000xxxx
                        如果FTP域名无法解析，     返回 wa,01000001,-10/0000000000000000000000000000xxxx
                        如果FTP地址端口无法连接， 返回 wa,01000001,-11/0000000000000000000000000000xxxx
                        如果FTP账户密码错误，     返回 wa,01000001,-12/0000000000000000000000000000xxxx
                        如果FTP目录不存在，       返回 wa,01000001,-13/0000000000000000000000000000xxxx
                        如果文件正在播放无法删除， 返回 wa,01000001,-15/0000000000000000000000000000xxxx
                        终端收到指令先删除终端本地存储的该文件，再进行下载， 如果该文件正在播放，需返回-15
                 */
                DDLog.i("下传和删除普通AMR格式音频文件" + data);
                if (!TextUtils.isEmpty(data)) {
                    if (data.split("/") != null) {
                        String fileName = data.split("/")[0];
                        long size = Long.parseLong(data.split("/")[1]);
                        final int playTimes = Integer.parseInt(data.split("/")[2]);

                        if (ClientInfoHelper.getAvailableSize() < size) {
                            NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                                    TcpPacketFactory.EMR_AMR_FILE_OPERATION, "-3/00000000000000000000000000000"));
                        } else if (size == 0) {
                            //删除该文件
                            DDLog.i("删除该文件");
                        } else {
                            NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                                    TcpPacketFactory.EMR_AMR_FILE_OPERATION, "0/000000000000000000000000000000"));

                            String url = "http://ftp3.xjxlb.com:58021/au/hnkt01/5aad974525934efb/" + "MP3C.amr";
                            HttpDownloadHelper.downloadFile(url, "dada/dada/Alertor/", fileName, new DownloadListener2() {
                                @Override
                                public void taskStart(@NonNull DownloadTask task) {
                                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                                            TcpPacketFactory.EMR_AMR_FILE_OPERATION, "-2/00000000000000000000000000000"));
                                }

                                @Override
                                public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
                                    if (cause.equals(EndCause.ERROR) || cause.equals(EndCause.CANCELED)) {
                                        //下载失败
                                        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE_ANSWER,
                                                TcpPacketFactory.EMR_AMR_FILE_OPERATION, "-2/00000000000000000000000000000"));
                                    } else if (cause.equals(EndCause.COMPLETED)) {
                                        //下载完成，设置播放列表
                                        DDLog.i("下载完成，打印播放次数" + playTimes);
                                    }
                                }
                            });
                        }
                    }
                }
            } else if (address == TcpPacketFactory.GET_FILE_SIZE) {
                /*7.3.3a 获取AMR格式音频文件大小*/
                //ra,01000002,abcdefgh.amr/123456789/000000000xxxx
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        data.split("/")[0] + "/" +
                                FileHelper.getFileSize(new File(data.split("/")[0])) + "000000000"));
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
                String playTime = data.split("/")[1];
                String endTime = data.split("/")[2];
                String voice = data.split("/")[3];
                boolean isPlay;
                if (data.split("/")[4].equals("1")) {
                    isPlay = true;
                } else isPlay = false;
                String week = data.split("/")[5];

                if (address == TcpPacketFactory.CLEAR_ALL_MUSIC_SHOW) {
                    /*清除所有音频节目列表*/
                    DDLog.i("清除所有音频节目列表");
                }
                //播放正常
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE, address,
                        "0/000000000000000000000000000000"));
                //播放错误
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE, address,
                        "-100/000000000000000000000000000"));
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
                int beforePlay = Integer.parseInt(data.split("/")[2]);
                int result = Integer.parseInt(data.split("/")[3]);
            } else {
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

    //系统变量
    private void SystemVariable(long address, TcpPacket tcpPacket) {
        DDLog.d(getClass(), "SystemVariable()... ");
        String data = tcpPacket.getData();
        CMD cmd = tcpPacket.getCmd();
        if (TextUtils.isEmpty(data)) {
            DDLog.e(getClass() , "data数据内容为空");
            return;
        }
        DDLog.d(getClass(), "SystemVariable() , data ：" + data);
        String[] split = data.split("/");

        if (address == TcpPacketFactory.DEVICE_ID) {
            //设备出厂编号(只读) ra,00000009,cn-telcom/aren-aaluke-13-0000001xxxx
            if (cmd == CMD.READ) {
                String companyName = "";  //9位
                String MEID = "";         //22位
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


                }
            } else if (cmd == CMD.READ){
                //从设备中获取参数
                linkType = "";
                shortLinkSleepTime = "";
                shortLinkDelay = "";
                String shortLinkParam = linkType + "/" + shortLinkSleepTime + "/" + shortLinkDelay + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        shortLinkParam + TcpPacketFactory.dataZero.substring(shortLinkParam.length())));
            }
        } else if (address == TcpPacketFactory.FILE_DOWNLOAD_MODE_PARAM_1) {
            //文件下载模式参数1(可读可写) wd,0000000b,1/ostar/ikll*^AA/0000000000000000000xxxx
            //参数1：下载模式：0 ftp模式；1 http下载模式
            String mode = "";
            //参数2：ttp下载账户
            String httpAccount = "";
            //参数3：http下载密码
            String httpPwd = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 2) {
                    mode = split[0];
                    httpAccount = split[1];
                    httpPwd = split[2];
                    // TODO 服务器下发的数据，修改设备中的下载模式参数1

                }
            }else if (cmd == CMD.READ) {
                //从设备中获取下载模式的参数
                mode = "";
                httpAccount = "";
                httpPwd = "";
                String fileDownloadModeParam = mode + "/" + httpAccount + "/" + httpPwd + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        fileDownloadModeParam + TcpPacketFactory.dataZero.substring(fileDownloadModeParam.length())));

            }

        } else if (address == TcpPacketFactory.FILE_DOWNLOAD_MODE_PARAM_2) {
            //文件下载模式参数2(可读可写) wd,0000000c,htp1.xjxlb.com:58003/0000000000xxxx
            //参数1：http下载域名端口
            String httpDomianName = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    httpDomianName = split[0];
                    // TODO 服务器下发的数据，修改设备中的下载模式参数2

                }
            } else if (cmd == CMD.READ) {
                //从设备中获取文件下载模式参数2
                httpDomianName = "";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        httpDomianName+ "/" + TcpPacketFactory.dataZero.substring(httpDomianName.length() + 1)));
            }
        } else if (address == TcpPacketFactory.HARDWARE_VERSION) {
            //硬件版本号(只读) ra,00000021,xj-6850-v2.19b/00000000000000000xxxx
            if (cmd == CMD.READ) {
                String hardwareVersion = PhoneManagerUtil.getDeviceModel();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        hardwareVersion + "/" + TcpPacketFactory.dataZero.substring(hardwareVersion.length() + 1)));
            }
        } else if (address == TcpPacketFactory.SOFTWARE_VERSION) {
            //软件版本号(只读) ra,00000022,fm-mp3-evdo-v3.50a/0000000000000xxxx
            if (cmd == CMD.READ) {
                String softwareVersion = PhoneManagerUtil.getDisplay();
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        softwareVersion + "/" + TcpPacketFactory.dataZero.substring(softwareVersion.length() + 1)));
            }
        } else if (address == TcpPacketFactory.EVDO_IP_ADDRESS) {
            //EVDO网络ip地址(只读) ra,00000026,192.168.111.123/0000000000000000xxxx
            if (cmd == CMD.READ) {
                String evdoIP = "";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        evdoIP + "/" + TcpPacketFactory.dataZero.substring(evdoIP.length() + 1)));
            }
        } else if (address == TcpPacketFactory.VOLUME) {
            //音量(可读可写) wa,00000027,7/1/1/00000000000000000000000000xxxx
            // 参数1：音量0-b分12档（0挡为最低档没有声音，其余挡位逐渐加大）
            String volume = "";
            //参数2：FM普通广播使能标志：1 使能，0 禁止
            String normalFM = "";
            //参数3：FM插播广播使能标志：1 使能，0 禁止
            String insertFM = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 2) {
                    volume = split[0];
                    normalFM = split[1];
                    insertFM = split[2];
                    //TODO 服务器下发的数据，修改设备中的音量参数

                }
            } else if (cmd == CMD.READ) {
                //从设备中获取音量参数
                volume = "";
                normalFM = "";
                insertFM = "";
                String volumeData = volume +"/" + normalFM + "/" + insertFM + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        volumeData + TcpPacketFactory.dataZero.substring(volumeData.length())));
            }

        } else if (address == TcpPacketFactory.PLATFORM_SERVER_IP_ADDRESS_1) {
            //平台服务器ip地址1(可读可写) wd,00000028,xlb1.xjxlb.com:58005/0000000000xxxx
            //参数1：平台服务器域名1(这两个域名如果一个无法访问，终端应该切换至第二个域名)
            String ip_1 = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    ip_1 = split[0];
                    //TODO 服务器下发的数据，修改设备中的平台服务器域名1

                }
            } else if (cmd == CMD.READ) {
                //从设备中获取平台服务器域名1
                ip_1 = "";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        ip_1 + "/" + TcpPacketFactory.dataZero.substring(ip_1.length() + 1)));
            }
        } else if (address == TcpPacketFactory.PLATFORM_SERVER_IP_ADDRESS_2) {
            //平台服务器ip地址2(可读可写) wd,00000029,xlb1.xj-ict.com:58005/0000000000xxxx
            //参数1：平台服务器域名2(这两个域名如果一个无法访问，终端应该切换至第二个域名)
            String ip_2 = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    ip_2 = split[0];
                    //TODO 服务器下发的数据，修改设备中的平台服务器域名2

                }
            } else if (cmd == CMD.READ) {
                //从设备中获取平台服务器域名2
                ip_2 = "";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        ip_2 + "/" + TcpPacketFactory.dataZero.substring(ip_2.length() + 1)));
            }
        } else if (address == TcpPacketFactory.BASE_STATION_INFORMATION) {
            //基站信息(只读) rd,0000002a,36d0/000b/b0c1/0000/60/000000000xxxx
            if (cmd == CMD.READ) {
                //各个参数：SID/NID/0000/BID/signal_level
                //从设备中获取参数
                String sid = "";
                String nid = "";
                String s = "";
                String bid = "";
                String singleLevel = "";
                String baseStationInfo = sid + "/" + nid + "/" + s + "/" + bid + "/" + singleLevel + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        baseStationInfo + TcpPacketFactory.dataZero.substring(baseStationInfo.length())));
            }
        } else if (address == TcpPacketFactory.LOCATION_INFORMATION) {
            //位置信息(只读) rd,0000002b,E119.327833/N39.961949/000000000xxxx
            if (cmd == CMD.READ) {
                //参数1：经度
                String lat = "";
                //参数2：纬度
                String lon = "";
                //从设备中获取经纬度，发送给服务器
                String location = lat + "/" + lon + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        location + TcpPacketFactory.dataZero.substring(location.length())));
            }
        } else if (address == TcpPacketFactory.FTP_SERVER_IP_ADDRESS) {
            //FTP服务器IP地址(可读可写) wd,0000002c,ftp3.xjxlb.com:12345/00000000000xxxx
            // 参数1：FTP服务器IP
            String ftpAddress = "";
            if (cmd == CMD.WRITE) {
                ftpAddress = split[0];
                //TODO 服务器下发最新FTP服务器IP，修改设备中的该数据

            } else if (cmd == CMD.READ) {
                //从设备中获取FTP服务器IP
                ftpAddress = "";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        ftpAddress + "/" + TcpPacketFactory.dataZero.substring(ftpAddress.length() + 1)));
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
                        DDLog.e(getClass() , "FTP服务器账号或密码为空");
                        return;
                    }
                    account = userAndPwd[0];
                    pwd = userAndPwd[1];
                    //TODO 服务器下发最新FTP服务器账号密码，修改设备中的该数据

                }
            } else if (cmd == CMD.READ) {
                //从设备中获取FTP服务器账号密码
                account = "";
                pwd = "";
                String accountAndPwd = account + "@" + pwd + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        accountAndPwd + TcpPacketFactory.dataZero.substring(accountAndPwd.length())));
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

                }
            } else if (cmd == CMD.READ) {
                //从设备中获取升级目录
                ftpFotafilePath = "";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        ftpFotafilePath + "," + TcpPacketFactory.dataZero.substring(ftpFotafilePath.length() + 1)));
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

                }
            } else if (cmd == CMD.READ) {
                //从设备中获取音频文件FTP目录
                ftpAudioFilepath = "";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        ftpAudioFilepath + "," + TcpPacketFactory.dataZero.substring(ftpAudioFilepath.length() + 1)));
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


                }
            } else if (cmd == CMD.READ) {
                //获取设备中的长链接心跳参数
                longLinkHeartbeat = "";
                longLinkSignDelay = "";
                linkType = "";
                platformSmsNum = "";
                String longLinkParam = longLinkHeartbeat + "/" + longLinkSignDelay + "/" + linkType + "/" + platformSmsNum + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        longLinkParam + TcpPacketFactory.dataZero.substring(longLinkParam.length())));
            }
        } else if (address == TcpPacketFactory.DEVICE_RESET) {
            //终端复位(可读可写) wd,00000032,00000000000000000000000000000000xxxx
            if (cmd == CMD.WRITE) {
                if (data.equals(TcpPacketFactory.dataZero)) {
                    //TODO 服务器写入全0数据，代表平台要求终端先复位重新启动（网络通讯模块也需要重新启动）

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


                }
            } else if (cmd == CMD.READ) {
                //从设备中获取以下参数
                phoneNum = "";
                callTime = "";
                callDate = "";
                times = "";
                String callCommandParam = phoneNum + "/" + callTime + "/" + callDate + "/" +times + "/";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        callCommandParam + TcpPacketFactory.dataZero.substring(callCommandParam.length())));
             }
        } else if (address == TcpPacketFactory.FUNCTION_1_CALL_PHONE_NUM) {
            //功能键 1 拨打电话号码（可写） wd,00000041,18909910000/000000000000000000xxxx
            //参数1：功能键1对应电话号码 0为取消
            String funcOneNum = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    funcOneNum = split[0];
                    if (TextUtils.equals(funcOneNum , "0")) {
                        //TODO 取消功能键1的功能

                        return;
                    }
                    //TODO 服务器最新下发的功能键1的号码，修改设备中的该数据

                }
            } else if (cmd == CMD.READ) {

            }

        } else if (address == TcpPacketFactory.FUNCTION_2_CALL_PHONE_NUM) {
            //功能键 2 拨打电话号码(可写) wd,00000042,18909910000/000000000000000000xxxx
            //参数1：功能键2对应电话号码 0为取消
            String funcTwoNum = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    funcTwoNum = split[0];
                    if (TextUtils.equals(funcTwoNum , "0")) {
                        //TODO 取消功能键2的功能

                        return;
                    }
                    //TODO 服务器最新下发的功能键2的号码，修改设备中的该数据

                }
            } else if (cmd == CMD.READ) {

            }

        } else if (address == TcpPacketFactory.FUNCTION_3_CALL_PHONE_NUM) {
            //功能键 3 拨打电话号码（可写）wd,00000043,18909910000/000000000000000000xxxx
            //参数1：功能键3对应电话号码 0为取消
            String funcThreeNum = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    funcThreeNum = split[0];
                    if (TextUtils.equals(funcThreeNum , "0")) {
                        //TODO 取消功能键3的功能

                        return;
                    }
                    //TODO 服务器最新下发的功能键3的号码，修改设备中的该数据

                }
            } else if (cmd == CMD.READ) {

            }
        } else if (address == TcpPacketFactory.FUNCTION_4_CALL_PHONE_NUM) {
            //功能键 4 拨打电话号码（可写）wd,00000044,18909910000/000000000000000000xxxx
            //参数1：功能键4对应电话号码 0为取消
            String funcFourNum = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    funcFourNum = split[0];
                    if (TextUtils.equals(funcFourNum , "0")) {
                        //TODO 取消功能键4的功能

                        return;
                    }
                    //TODO 服务器最新下发的功能键4的号码，修改设备中的该数据

                }
            } else if (cmd == CMD.READ) {

            }

        } else if (address == TcpPacketFactory.DEVICE_AVAILABLE_SIZE) {
            //平台获取终端可用存储空间大小（只读），查询指令：rd,00000045,000000000000000000000000000000xxxx
            if (cmd == CMD.READ) {
                if (data.equals(TcpPacketFactory.dataZero)) {
                    String[] memInfo = PhoneUtil.getRamInfo(BaseApplication.sContext);
                    //可用存储全部大小
                    String totalMem = memInfo[0];
                    //可用存储空闲大小
                    String availMem = memInfo[1];
                    //反馈指令：ra,00000045,12345678901/12345678901/000000xxxx
                    String totalAndAvail = totalMem + "/" + availMem + "/";
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                            totalAndAvail + TcpPacketFactory.dataZero.substring(totalAndAvail.length())));
                }
            }
        } else if (address == TcpPacketFactory.QUERY_TIME_FORM_PLATFORM) {
            //平台查询终端系统当前时间（只读），查询指令：rd,00000046,000000000000000000000000000000xxxx
            if (cmd == CMD.READ) {
                if (data.equals(TcpPacketFactory.dataZero)) {
                    //时间格式为yyyymmddhhmiss
                    String systemTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
                    //反馈指令：ra,00000046,20180103201059/000000000000000xxxx
                    NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                            systemTime + "/" + TcpPacketFactory.dataZero.substring(systemTime.length() + 1)));
                }
            }
        } else if (address == TcpPacketFactory.DEVICE_PERSON_FUNCTION) {
            //终端个性化功能（可读可写）
            //第1个字符: 0 APP紧急语音单次播放; 1：APP紧急语音循环播放
            String playMode = "";
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
                playMode = data.substring(0, 1);
                moveSwitch = data.substring(1, 2);
                alarmMode = data.substring(2, 3);
                callEnable = data.substring(3, 4);
                channelSelect = data.substring(4, 5);
                wifiSwitch = data.substring(5, 6);
                //TODO 服务器下发的最新参数，修改设备中的该参数


            } else if (cmd == CMD.READ) {
                //从设备中获取参数
                playMode = "";
                moveSwitch = "";
                alarmMode = "";
                callEnable = "";
                channelSelect = "";
                wifiSwitch = "";
                String devicePersonFunc = playMode + moveSwitch + alarmMode + callEnable + channelSelect + wifiSwitch;
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        devicePersonFunc + TcpPacketFactory.dataZero.substring(devicePersonFunc.length())));
            }
        } else if (address == TcpPacketFactory.PLATFORM_PHONE_NUM) {
            //一键报警平台电话号码(可写) wd,00000048,18909910000/000000000000000000xxxx
            //参数1：一键报警平台对应电话号码 ; 0为取消
            String platformPhoneNum = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    platformPhoneNum = split[0];
                    if (TextUtils.equals(platformPhoneNum , "0")) {
                        //TODO 取消功能

                        return;
                    }
                    //TODO 服务器下发最新的一键报警平台电话号码，修改设备中的数据


                }
            } else if (cmd == CMD.READ) {

            }

        } else if (address == TcpPacketFactory.ALARM_VOLUME) {
            //接警音量(可读可写) wd,00000049,1/0000000000000000000000000000xxxx
            //参数1：接警音量: 1 1档; 2 2档; 3 3档; 4 4档
            String alarmVolume = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    alarmVolume = split[0];
                    //TODO 服务器下发接警音量，修改设备中的数据

                }
            } else if (cmd == CMD.READ) {
                //从设备中获取接警音量
                alarmVolume = "";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        alarmVolume + "/" + TcpPacketFactory.dataZero.substring(alarmVolume.length() + 1)));
            }
        } else if (address == TcpPacketFactory.BATCH_DEL_FILE) {
            //批量删除文件指令(可写) wd,0000004a,ABCDEFGHIJKLMNOP/0000000000000xxxx
            //参数1：批量删除的文件名缩写，代表含义为：A=MP3A.amr; B=MP3B.amr... P=MP3P.amr
            String fliesName = "";
            if (cmd == CMD.WRITE) {
                if (split.length > 0) {
                    fliesName = split[0];
                    //TODO 服务器下发要删除的文件名缩写，执行删除操作

                }
            }
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
                    ipAndPort= audioLive[1];
                    //TODO 修改设备中的该参数数据


                }
            } else if (cmd == CMD.READ) {
                //获取本地存储的参数
                channelNum = "";
                ipAndPort = "";
                String audioLiveChannelAndIP = channelNum + "," + ipAndPort + ",";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        audioLiveChannelAndIP + TcpPacketFactory.dataZero.substring(audioLiveChannelAndIP.length())));
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
                        audioLiveAccountAndPwd + TcpPacketFactory.dataZero.substring(audioLiveAccountAndPwd.length())));
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
                String audioLiveStartAndEnd = channelNum + "," + startTime + "," + endTime + "," + volume +",";
                NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.READ_ANSWER, address,
                        audioLiveStartAndEnd + TcpPacketFactory.dataZero.substring(audioLiveStartAndEnd.length())));
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
                        audioLiveChannelUrl + TcpPacketFactory.dataZero.substring(audioLiveChannelUrl.length())));
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
                if (TextUtils.equals(TcpPacketFactory.dataZero , data)) {
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

}
