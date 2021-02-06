package com.flyscale.alertor.netty;

import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.alarmManager.AlarmManager;
import com.flyscale.alertor.alarmManager.AlarmMediaPlayer;
import com.flyscale.alertor.alarmManager.IpAlarmInstance;
import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.factory.BaseDataFactory;
import com.flyscale.alertor.data.packet.TcpPacket;
import com.flyscale.alertor.data.packet.TcpPacketFactory;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistWhite;
import com.flyscale.alertor.data.up.UAddDeleteWhiteList;
import com.flyscale.alertor.data.up.UChangeAlarmNumber;
import com.flyscale.alertor.data.up.UChangeHeart;
import com.flyscale.alertor.data.up.UHeart;
import com.flyscale.alertor.data.up.URing;
import com.flyscale.alertor.data.up.UUpdateVersion;
import com.flyscale.alertor.data.up.UVoice;
import com.flyscale.alertor.eventBusManager.EventBusUtils;
import com.flyscale.alertor.eventBusManager.EventType;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.helper.MD5Util;
import com.flyscale.alertor.helper.UserActionHelper;
import com.flyscale.alertor.helper.DataConvertHelper;
import com.flyscale.alertor.helper.FileHelper;
import com.flyscale.alertor.helper.MediaHelper;
import com.flyscale.alertor.helper.ThreadPool;
import com.flyscale.alertor.jni.NativeHelper;
import com.flyscale.alertor.led.LedInstance;
import com.flyscale.alertor.media.AlarmMediaInstance;
import com.flyscale.alertor.media.ReceiveMediaInstance;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.netty.buffer.ByteBuf;
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
        PersistConfig.saveLogin(false);
        LedInstance.getInstance().offStateLed();
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
        NettyHelper.getInstance().send(TcpPacketFactory.createPacketSend(TcpPacketFactory.LOGIN, "460031234567890/0A9464026708209/"));
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
//                NettyHelper.getInstance().send(new UHeart());
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
            } else {
                //系统变量
                SystemVariable(address ,tcpPacket);
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
        DDLog.d(getClass() ,"SystemVariable()... ");
        String data = tcpPacket.getData();
        if (TextUtils.isEmpty(data)) {
            return;
        }
        DDLog.d(getClass(), "SystemVariable() , data ：" + data);
        String[] split = data.split("/");
        if (address == TcpPacketFactory.DEVICE_ID) {
            //设备出厂编号
            if (split.length > 1) {
                //厂家名称
                String companyName = split[0];
                String MEID = split[1];

            }
        }else if (address == TcpPacketFactory.SHORT_LINK_PARAM) {
            //短连接参数
            if (split.length > 2) {
                //链接类型 ：0短链接；1长链接
                String linkType = split[0];
                //短链接休眠时长（秒）
                String shortLinkSleepTime = split[1];
                //短链接工作等待延迟（秒）
                String shortLinkDelay = split[2];

            }
        }else if (address == TcpPacketFactory.FILE_DOWNLOAD_MODE_PARAM_1) {
            //文件下载模式参数1
            if (split.length > 2) {
                //下载模式：0 ftp模式；1 http下载模式
                String mode = split[0];
                //http下载账户
                String httpAccount = split[1];
                //http下载密码
                String httpPwd = split[2];

            }
        }else if (address == TcpPacketFactory.FILE_DOWNLOAD_MODE_PARAM_2) {
            //文件下载模式参数2
            if (split.length > 0) {
                //http下载域名端口
                String httpDomianName = split[0];

            }
        } else if (address == TcpPacketFactory.HARDWARE_VERSION) {
            //硬件版本号
            if (split.length > 0) {
                String hardwareVersion = split[0];

            }
        } else if (address == TcpPacketFactory.SOFTWARE_VERSION) {
            //软件版本号
            if (split.length > 0) {
                String softwareVersion = split[0];

            }
        } else if (address == TcpPacketFactory.EVDO_IP_ADDRESS) {
            //EVDO网络ip地址
            if (split.length > 0) {
                String evdoIP = split[0];

            }
        } else if (address == TcpPacketFactory.VOLUME) {
            //音量
            if (split.length > 2) {
                //音量0-b分12档（0挡为最低档没有声音，其余挡位逐渐加大）
                String volume = split[0];
                //FM普通广播使能标志：1 使能，0 禁止
                String normalFM = split[1];
                //FM插播广播使能标志：1 使能，0 禁止
                String insertFM = split[2];

            }
        } else if (address == TcpPacketFactory.PLATFORM_SERVER_IP_ADDRESS_1) {
            //平台服务器ip地址1
            if (split.length > 0) {
                //这两个域名如果一个无法访问，终端应该切换至第二个域名
                String ip_1 = split[0];

            }
        } else if (address == TcpPacketFactory.PLATFORM_SERVER_IP_ADDRESS_2) {
            //平台服务器ip地址2
            if (split.length > 0) {
                //这两个域名如果一个无法访问，终端应该切换至第二个域名
                String ip_2 = split[0];

            }
        } else if (address == TcpPacketFactory.BASE_STATION_INFORMATION) {
            //基站信息
            if (split.length > 5) {
                String sid = split[0];
                String nid = split[1];
                String s = split[2];
                String bid = split[3];
                String singleLevel = split[4];

            }
        } else if (address == TcpPacketFactory.LOCATION_INFORMATION) {
            //位置信息
            if (split.length > 1) {
                //经度E119.327833
                String lat = split[0];
                //纬度N39.961949
                String lon = split[1];

            }
        } else if (address == TcpPacketFactory.FTP_SERVER_IP_ADDRESS) {
            //FTP服务器IP地址
            if (split.length > 0) {
                //ftp3.xjxlb.com:12345
                String ftpAddress = split[0];

            }
        } else if (address == TcpPacketFactory.FTP_SERVER_ACCOUNT_PASSWORD) {
            //FTP服务器账户口令
            if (split.length > 0) {
                String[] userAndPwd = split[0].split("@");
                if (userAndPwd == null || userAndPwd.length < 2) {
                    return;
                }
                String account = userAndPwd[0];
                String pwd = userAndPwd[1];

            }
        } else if (address == TcpPacketFactory.FOTA_FILE_FTP_DIR) {
            //升级文件FTP目录
            String[] fotaDir = data.split(",");
            if (fotaDir.length > 0) {
                String ftpFotafilePath = fotaDir[0];

            }
        } else if (address == TcpPacketFactory.AUDIO_FILE_FTP_DIR) {
            //音频文件FTP目录
            String[] audioDir = data.split(",");
            if (audioDir.length > 0) {
                String ftpAudioFilepath = audioDir[0];

            }
        } else if (address == TcpPacketFactory.LONG_LINK_HEARTBEAT_INTERVAL) {
            //长链接心跳间隔(秒)
            if (split.length > 3) {
                //长连接心跳间隔(秒)
                String longLinkHeartbeat = split[0];
                //长连接登录延迟(秒)
                String longLinkSignDelay = split[1];
                //连接短连接选择 (固定为0:短连接 ;固定为1:长连接)
                String linkType = split[2];
                //平台短信号码
                String platformSmsNum = split[3];

            }
        } else if (address == TcpPacketFactory.DEVICE_RESET) {
            //终端复位
            //写入全0数据，代表平台要求终端先复位重新启动（网络通讯模块也需要重新启动）
            if (data.equals(TcpPacketFactory.dataZero)) {

            }
        } else if (address == TcpPacketFactory.CALL_COMMAND_PARAM) {
            //拨打电话指令参数
            if (split.length > 3) {
                //拨打电话号码
                String phoneNum = split[0];
                //每次通话时长(秒)
                String callTime = split[1];
                //拨打时间(年月日时分)
                String callDate = split[2];
                //拨打失败后重试次数
                String times = split[3];

            }
        } else if (address == TcpPacketFactory.FUNCTION_1_CALL_PHONE_NUM) {
            //功能键 1 拨打电话号码
            if (split.length > 0) {
                //功能键1对应电话号码 0为取消
                String funcOneNum = split[0];

            }
        } else if (address == TcpPacketFactory.FUNCTION_2_CALL_PHONE_NUM) {
            //功能键 2 拨打电话号码
            if (split.length > 0) {
                //功能键2对应电话号码 0为取消
                String funcTwoNum = split[0];

            }
        } else if (address == TcpPacketFactory.FUNCTION_3_CALL_PHONE_NUM) {
            //功能键 3 拨打电话号码
            if (split.length > 0) {
                //功能键3对应电话号码 0为取消
                String funcThreeNum = split[0];

            }
        } else if (address == TcpPacketFactory.FUNCTION_4_CALL_PHONE_NUM) {
            //功能键 4 拨打电话号码
            if (split.length > 0) {
                //功能键4对应电话号码 0为取消
                String funcFourNum = split[0];

            }
        } else if (address == TcpPacketFactory.DEVICE_AVAILABLE_SIZE) {
            //平台获取终端可用存储空间大小
            if (data.equals(TcpPacketFactory.dataZero)) {
                //可用存储全部大小
                String availSize = "";
                //可用存储空闲大小
                String freeSize = "";
                //向服务器返回数据 ，ra,00000045,12345678901/12345678901/000000xxxx

            }
        } else if (address == TcpPacketFactory.QUERY_TIME_FORM_PLATFORM) {
            //平台查询终端系统当前时间
            if (data.equals(TcpPacketFactory.dataZero)) {
                //时间格式为yyyymmddhhmiss
                String systemTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
                //向服务器发送数据，ra,00000046,20180103201059/000000000000000xxxx

            }
        } else if (address == TcpPacketFactory.DEVICE_PERSON_FUNCTION) {
            //终端个性化功能
            //第1个字符: 0 APP紧急语音单次播放; 1：APP紧急语音循环播放
            String playMode = data.substring(0, 1);
            //第2个字符: 0防移开关启用; 1防移开关禁用
            String moveSwitch = data.substring(1,2);
            //第3个字符:
            // 0一键报警普通模式（十户联防号码呼入：用户按键接听）;
            // 1一键报警奎屯模式（十户联防号码呼入后：响3声报警音，再自动接听，播放完成报警信息后，用户挂断）
            //2：一键报警沙湾模式（十户联防号码呼入后：不响报警音，自动接听，播放完成报警信息后，用户挂断；）
            String alarmMode = data.substring(2,3);
            //第4个字符: 1就是只能拨打报警与快捷键; 0可以拨打所有电话
            String callEnable = data.substring(3,4);
            //第5个字符,“频选”参数 ：0 默认; 1 4G-800M优选;  2:4G-1800M优选
            String channelSelect = data.substring(4, 5);
            //第6个字符,“WIFI开关”参数 ：0 开通; 1 关闭
            String wifiSwitch = data.substring(5,6);



        } else if (address == TcpPacketFactory.PLATFORM_PHONE_NUM) {
            //一键报警平台电话号码
            if (split.length > 0) {
                //一键报警平台对应电话号码 ; 0为取消
                String platformPhoneNum = split[0];

            }
        } else if (address == TcpPacketFactory.ALARM_VOLUME) {
            //接警音量
            if (split.length > 0) {
                //接警音量: 1 1档; 2 2档; 3 3档; 4 4档
                String alarmVolume = split[0];


            }
        } else if (address == TcpPacketFactory.BATCH_DEL_FILE) {
            //批量删除文件指令
            if (split.length > 0) {
                //第一个参数为批量删除的文件名缩写，代表含义为：A=MP3A.amr; B=MP3B.amr... P=MP3P.amr
                String files = split[0];

            }

        } else if (address == TcpPacketFactory.DEVICE_SHORT_LINK_SLEEP) {
            //设备进入短链接休眠
            if (data.equals(TcpPacketFactory.dataZero)) {
                //平台发给设备wd，表示设备进入通讯休眠等待，平台收到设备的确认指令wa后，断开连接
                //wa,0000004b,00000000000000000000000000000000xxxx

            }
        } else if (address == TcpPacketFactory.AUDIO_LIVE_IP_ADDRESS_AND_PORT) {
            //音频直播频道服务器地址和端口
            String[] audioLive = data.split(",");
            if (audioLive.length > 1) {
                //频道编号,0开始
                String channelNum = audioLive[0];
                //频道音频流的服务器ip和端口
                String ipAndPort = audioLive[1];

            }

        } else if (address == TcpPacketFactory.AUDIO_LIVE_ACCOUNT_PASSWORD) {
            //音频直播频道账户密码
            String[] accountAndPwd = data.split(",");
            if (accountAndPwd.length > 2) {
                //频道编号，0开始
                String channelNum = accountAndPwd[0];
                //流媒体服务器的账户
                String account = accountAndPwd[1];
                //流媒体服务器的密码
                String pwd = accountAndPwd[2];

            }

        } else if (address == TcpPacketFactory.AUDIO_LIVE_START_AND_END_TIME) {
            //音频直播频道开始结束时间
            String[] liveTime = data.split(",");
            if (liveTime.length > 3) {
                //频道编号，0开始
                String channelNum = liveTime[0];
                //直播开始时间，小时分钟秒
                String startTime = liveTime[1];
                //直播结束时间，小时分钟秒
                String endTime = liveTime[2];
                //直播音量，0-9
                String volume = liveTime[3];

            }
        } else if (address == TcpPacketFactory.AUDIO_LIVE_CHANNEL_URL) {
            //音频直播频道url
            String[] channelAndUrl = data.split(",");
            if (channelAndUrl.length > 1) {
                //频道编号，0开始
                String channelNum = channelAndUrl[0];
                //频道音频流的url第1部分, url最大长度32字符
                String url = channelAndUrl[1];

            }
        } else if (address == TcpPacketFactory.FM_FIXED_CHANNEL_SETTINGS) {
            //FM固定频道设置
            if (data.equals(TcpPacketFactory.dataZero)) {
                //频道数字为0，表明清除所有频道。

            } else {
                String[] dataList = data.split("/");
                String[] channelArray = new String[dataList.length -1];
                System.arraycopy(dataList, 0 , channelArray , 0 , channelArray.length);
                //channelArray为设置的固定频道列表

            }

        }
    }

}
