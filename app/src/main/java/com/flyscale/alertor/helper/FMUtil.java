package com.flyscale.alertor.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.flyscale.alertor.data.packet.CMD;
import com.flyscale.alertor.data.packet.TcpPacket;
import com.flyscale.alertor.data.packet.TcpPacketFactory;
import com.flyscale.alertor.netty.NettyHelper;

import java.util.ArrayList;
import java.util.List;

public class FMUtil {


    public static float freq = 0f;
    public static boolean isFmOn = false;
    public static boolean isMuted = false;
    public static String freqList = "";
    public static int count = 0;

    public FMUtil() {
    }

    /**
     * 十进制转二进制
     * */
    public static String toBinary(int date){
        String binary = Integer.toBinaryString(date);
        return binary;
    }


    public static void startFM(Context context){
        Intent intent = new Intent("com.android.flyscale.FM_START");
        context.sendBroadcast(intent);
    }

    public static void pauseFM(Context context){
        Intent intent = new Intent("com.android.flyscale.FM_PAUSE");
        context.sendBroadcast(intent);
    }

    public static void searchFM(Context context){
        Intent intent = new Intent("com.android.flyscale.FM_SEARCH");
        context.sendBroadcast(intent);
    }

    public static void stopFM(Context context){
        Intent intent = new Intent("com.android.flyscale.FM_STOP");
        context.sendBroadcast(intent);
    }

    public static void adjustFM(Context context,float freq){
        Intent intent = new Intent("com.android.flyscale.FM_ADJUST");
        intent.putExtra("freq",freq);
        context.sendBroadcast(intent);
    }

    public static void informationFM(Context context){
        Intent intent = new Intent("com.android.flyscale.FM_INFORMATION");
        context.sendBroadcast(intent);
    }

    public static float getFreq() {
        return freq;
    }

    public static void setFreq(float freq) {
        FMUtil.freq = freq;
    }

    public static boolean isIsFmOn() {
        return isFmOn;
    }

    public static void setIsFmOn(boolean isFmOn) {
        FMUtil.isFmOn = isFmOn;
    }

    public static boolean isIsMuted() {
        return isMuted;
    }

    public static void setIsMuted(boolean isMuted) {
        FMUtil.isMuted = isMuted;
    }

    public static String getFreqList() {
        return freqList;
    }

    public static void setFreqList(String freqList) {
        FMUtil.freqList = freqList;
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        FMUtil.count = count;
    }

    public static void setFMStatus(boolean isFmOn,boolean isMuted,String freqList,float freq,int count){
        FMUtil.isFmOn = isFmOn;
        FMUtil.isMuted = isMuted;
        FMUtil.freqList = freqList;
        FMUtil.freq = freq;
        FMUtil.count = count;
    }

    /**
     * 设定fm启动闹钟
     * */
    public static void startFMAlarmManager(Context context,int fmId,long time,String weekly,String freq){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("FLYSCALE_ALARMMANAGER_FM_START");
        intent.putExtra("fmId",fmId);
        intent.putExtra("weekly",weekly);
        intent.putExtra("freq",freq);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, fmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent);
        Log.e("fengpj","定时启动设置"+fmId + "当前时间" + System.currentTimeMillis() + "设置时间" +(System.currentTimeMillis() + time) );
    }

    /**
     * 设定fm停止闹钟
     * */
    public static void stopFMAlarmManager(Context context,int fmId,long time){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("FLYSCALE_ALARMMANAGER_FM_STOP");
        intent.putExtra("fmId",fmId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, fmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent);
        Log.e("fengpj","定时停止Fm设置"+fmId + "当前时间" + System.currentTimeMillis() + "设置时间" +(System.currentTimeMillis() + time) );
    }

    /**
     * 设定brfm停止闹钟
     * */
    public static void stopBrFMAlarmManager(Context context,int fmId,long time){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("FLYSCALE_ALARMMANAGER_BRFM_STOP");
        intent.putExtra("fmId",fmId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, fmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent);
        Log.e("fengpj","定时停止Brfm设置"+fmId + "当前时间" + System.currentTimeMillis() + "设置时间" +(System.currentTimeMillis() + time) );
    }

    /**
     * 取消fm闹钟提醒
     */
    public static void cancelFMAlarmManager(Context context,int fmId)
    {
        // 取消AlarmManager的定时服务
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent("FLYSCALE_ALARMMANAGER_FM_START");// 和设定闹钟时的action要一样
        // 这里PendingIntent的requestCode、intent和flag要和设定闹钟时一样
        PendingIntent pi=PendingIntent.getBroadcast(context, fmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pi);
        Log.e("fengpj","取消定时设置"+fmId);
    }


    /**
     * 取消brfm闹钟提醒
     */
    public static void cancelBrFMAlarmManager(Context context,int fmId)
    {
        // 取消AlarmManager的定时服务
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent=new Intent("FLYSCALE_ALARMMANAGER_BRFM_START");// 和设定闹钟时的action要一样
        // 这里PendingIntent的requestCode、intent和flag要和设定闹钟时一样
        PendingIntent pi=PendingIntent.getBroadcast(context, fmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pi);
        Log.e("fengpj","取消定时设置"+fmId);
    }


    /**
     * 设定插入fm启动闹钟
     * */
    public static void startBrFMAlarmManager(Context context,int fmId,long time,String freq){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("FLYSCALE_ALARMMANAGER_BRFM_START");
        intent.putExtra("fmId",fmId);
        intent.putExtra("freq",freq);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, fmId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent);
        Log.e("fengpj","定时启动设置"+fmId + "当前时间" + System.currentTimeMillis() + "设置时间" +(System.currentTimeMillis() + time) );
    }

    /**
     * 平台设置码
     *wd,00000100,127000/094.900/080000/090000/100xxxx
     * wd,00000101,001000/105.300/090000/100000/000xxxx
     * 播放fm向平台发送回调
     * wd,01000085,100/094.900/0/0/0190605121011000xxxx
     * 平台响应报文：
     * wa,01000085,00000000000000000000000000000000xxxx
     * 参数说明：
     * 第一个参数：1-3：FM地址码
     * 第二个参数：5-11：FM频率
     * 第三个参数：13：为播放前后标志：0播放前；1播放后
     * 第四个参数为成功播放结果代码：
     * 0	播放成功
     * -51	FM电路损坏
     * -52	FM信号差
     * -53	没有FM信号
     * 第五个参数为播放时的系统时间，该参数紧跟在第四个参数后面，没有/分隔符，格式为YYMMDDHHMISS：
     * 该报文如果终端和设备暂时没有连接到平台无法发送，那么必须保存在终端中，待终端再次上线后逐条补发给平台。
     * */
    public static void fmCallBack(int id,String isfinish,String playresult){
        String data = FMLitepalUtil.getData(id);
        String addressCode =Integer.toHexString(Integer.parseInt(FMLitepalUtil.getAddress(id)));
        String freq = FMLitepalUtil.getFreq(id);
        String isFinish = isfinish;
        String playResult = playresult;
        String time = DateUtil.StringTimeYmdhms();
        String result = addressCode + "/" + freq + "/" + isFinish + "/" + playResult + "/" + time;
        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE, TcpPacketFactory.PLAY_FM, result + TcpPacketFactory.dataZero.substring(result.length())));
    }


    public static void fmCallBack2(int id,String isfinish,String playresult){
        String data = BreakFMLitepalUtil.getData(id);
        String addressCode =Integer.toHexString(Integer.parseInt(BreakFMLitepalUtil.getAddress(id)));
        String freq = BreakFMLitepalUtil.getFreq(id);
        String isFinish = isfinish;
        String playResult = playresult;
        //String time = DateUtil.StringTimeYmdhms();
        String result = addressCode + "/" + freq + "/" + isFinish + "/" + playResult + "/" ;
        NettyHelper.getInstance().send(TcpPacket.getInstance().encode(CMD.WRITE, TcpPacketFactory.PLAY_FM, result + TcpPacketFactory.dataZero.substring(result.length())));
    }

    /**
     * 还原任意fm播放状态
     * */
    public static void fmReduction(){
        //重新设置周期广播
        List<String> isSetUp2 = FMLitepalUtil.getIsSetUpId();
        if (isSetUp2 != null){
            for (String a:isSetUp2){
                Log.e("fengpj","重新设置已经修改过的行  == " +a);
                int id = Integer.parseInt(a);
                String data = FMLitepalUtil.getData(id);
                DateUtil.updataAlarmForFM(id,FMLitepalUtil.getWeeklyRecord(data),FMLitepalUtil.getFreq(data),
                        FMLitepalUtil.getStartFMTime(data),FMLitepalUtil.getEndFMTime(data),FMLitepalUtil.getVolume(data));
            }
        }

        //重新设置插入广播
        List<String> isSetUp = BreakFMLitepalUtil.getIsSetUpId();
        if (isSetUp != null){
            for (String a:isSetUp){
                Log.e("fengpj","重新设置已经修改过的行  == " +a);
                int id = Integer.parseInt(a);
                String data = BreakFMLitepalUtil.getData(id);
                DateUtil.updataAlarmForBrFM(id,BreakFMLitepalUtil.getStartDate(data),BreakFMLitepalUtil.getFreq(data),
                        BreakFMLitepalUtil.getStartTime(data),BreakFMLitepalUtil.getEndTime(data),BreakFMLitepalUtil.getVolume(data));
            }
        }
    }
}
