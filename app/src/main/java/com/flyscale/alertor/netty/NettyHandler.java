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
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
     * @throws Exception
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
        }, 5 * 1000);
    }

    /**
     * channel被创建但没有注册到eventLoop
     *
     * @param ctx
     * @throws Exception
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
     * @throws Exception
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
        NettyHelper.getInstance().send(TcpPacketFactory.createPacketSend(TcpPacketFactory.LOGIN, "460031234567890/0A9464026708209/"));
    }

    /**
     * //利用写空闲发送心跳检测消息
     *
     * @param ctx
     * @param evt
     * @throws Exception
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
                                PhoneManagerUtil.getBatteryStatus(BaseApplication.sContext)  + "/" +
                                (float)(Math.round((PhoneManagerUtil.getBatteryVoltage(BaseApplication.sContext).floatValue() / 1000)*10))/10 + "/" +
                                36 + "/" +
                                PhoneManagerUtil.getTamperSwitch(BaseApplication.sContext) + "/"  + ClientInfoHelper.getVolume()
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
     * @throws Exception
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
}
