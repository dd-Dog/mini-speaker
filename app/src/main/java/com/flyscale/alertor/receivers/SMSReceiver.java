package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.BuildConfig;
import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistWhite;
import com.flyscale.alertor.helper.DDLog;
import com.flyscale.alertor.netty.NettyHelper;

import java.util.List;

/**
 * @author bianjb
 * 短信监听广播
 */
public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSReceiver";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    //短信指令格式 key:value，长度限定20个字符
//    private static final String SPLIT_FLAG = ":";
//    private static final String SMS_FORMAT_REGEX = "^\\w{1,20}" + SPLIT_FLAG + "\\w{1,20}$";


    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.equals(intent.getAction(), SMS_RECEIVED)) {
            Log.d(TAG, "sms received!");
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                if (messages.length > 0) {
                    String content = messages[0].getMessageBody();
                    String sender = messages[0].getOriginatingAddress();
                    long msgDate = messages[0].getTimestampMillis();
                    Log.d(TAG, "message from: " + sender + ", message body: " + content
                            + ", message date: " + msgDate);

                    handleSMS(sender, content, msgDate);
                }
            }
        }
    }

    /**
     * 1. 短信格式：
     * 修改报警号码：          IPALARMNUM=2,19185963003;          --  1代表修改小按钮报警电话，2代表平台报警电话
     * 修改IP和语音报警优先级：IPALARMYUY=1;                      --  1为语音报警优先，0为IP报警优先
     * 修改报警灯常亮时间：    IPALARMLED=08:30,20:30;            --  设为00:00,00:00 表示常亮
     * 添加删除白名单：        IPALARMWLM=0,2,15710089835,112;    --  0是增加，2是两个,
     * IPALARMWLM=1,1,15710089835;        --  1是删除，1代表1个号码
     * 修改白名单开关：        IPALARMWLS=0;                      --  0是关，1是开Re
     * <p>
     * flyscale  fota开关   FLYSCALEFOTA=1;
     * 本地存储的数据         FLYSCALEPERSISTDATA=1;
     * <p>
     * 注意：短信必须以分号结尾，所有字符需在英文输入法下编辑
     *
     * @param sender
     * @param content
     * @param msgDate
     */
    private void handleSMS(String sender, String content, long msgDate) {
        //支持任意手机号发短信修改 所以不需要判断手机号
        Log.i(TAG, "handleSMS: " + content);
        try {
            //BMD9876*SC*01*02*03# BMD9876*SC*0#（删除全部）
            //BMD9876*XG*01#10000*02#18922709554 修改
            //BMD9876*CX*01*02*03# BMD9876*CX*0#
            if (content != null && content.startsWith("BMD9876")) {
                content = content.trim();
                if (content.endsWith("#")) {
                    content = content.trim().substring(0, content.trim().length() - 1);
                }
                String[] array = TextUtils.split(content, "\\*");
                if (array != null && array.length >= 3 && TextUtils.equals(array[0], "BMD9876")) {
                    if (TextUtils.equals(array[1], "SC")) {
                        for (int i = 2; i < array.length; i++) {
                            try {
                                if (i == 2 && TextUtils.equals(array[i], "0")) {
                                    //删除所有
                                    PersistWhite.deleteAllNum();
                                    break;
                                } else {
                                    PersistWhite.deleteNumByIndex(array[i]);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (TextUtils.equals(array[1], "XG")) {
                        for (int i = 2; i < array.length; i++) {
                            if (i >= 4) break;//一次最多修改两组号码
                            String[] arr = array[i].split("#");
                            if (arr.length == 2) {
                                PersistWhite.saveNum(arr[0], arr[1]);
                            }
                        }
                    } else if (TextUtils.equals(array[1], "CX")) {
                        List<PersistWhite> list = PersistWhite.findList();
                        StringBuilder sb = new StringBuilder();
                        sb.append("BMD9876*CX");
                        for (int i = 2; i < array.length; i++) {
                            if (i == 2 && array[i].equals("0")) {
                                for (PersistWhite pw : list) {
                                    sb.append("*").append(pw.getIndex()).append("#").append(pw.getReceiveNum());
                                }
                                break;
                            }
                            for (PersistWhite pw : list) {
                                if (pw.getIndex().equals(array[i])) {
                                    sb.append("*").append(pw.getIndex()).append("#").append(pw.getReceiveNum());
                                }
                            }
                        }

                        String replyMsg = sb.toString();
                        DDLog.i("回复：" + replyMsg);
                        SmsManager.getDefault().sendTextMessage(sender, null, replyMsg, null, null);
                    }

                }
            } else if (content != null && content.startsWith("PTHM9876")) {
                String[] split = content.trim().split("\\*");
                if (split.length == 2) {
                    PersistConfig.saveAlarmNum(split[1]);
                    DDLog.i("修改报警号码成功:" + PersistConfig.findConfig().getAlarmNum());
                } else {
                    DDLog.i("格式不正确！");
                }
            } else if (content != null && content.startsWith("JCHM9876")) {
                //JCHM9876*10000 JCHM9876*1#10000
                String[] split = content.trim().split("\\*");
                if (split.length == 2){
                    String[] arr = split[1].split("#");
                    if (arr.length == 2){
                        int key = Integer.parseInt(arr[0]);
                        switch (key){
                            case 1:
                                PersistConfig.saveKey1Num(arr[1]);
                                DDLog.i("修改快捷键1成功：" + PersistConfig.findConfig().getKey1Num());
                                break;
                            case 2:
                                PersistConfig.saveKey2Num(arr[1]);
                                DDLog.i("修改快捷键2成功：" + PersistConfig.findConfig().getKey2Num());
                                break;
                            case 3:
                                PersistConfig.saveKey3Num(arr[1]);
                                DDLog.i("修改快捷键3成功：" + PersistConfig.findConfig().getKey3Num());
                                break;
                            case 4:
                                PersistConfig.saveKey4Num(arr[1]);
                                DDLog.i("修改快捷键4成功：" + PersistConfig.findConfig().getKey4Num());
                                break;
                        }

                    }
                }

            } else {
                DDLog.i("格式错误！");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
