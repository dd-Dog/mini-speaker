package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.flyscale.alertor.data.persist.PersistConfig;
import com.flyscale.alertor.data.persist.PersistWhite;

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
     *    修改报警号码：          IPALARMNUM=2,19185963003;          --  1代表修改小按钮报警电话，2代表平台报警电话
     *    修改IP和语音报警优先级：IPALARMYUY=1;                      --  1为语音报警优先，0为IP报警优先
     *    修改报警灯常亮时间：    IPALARMLED=08:30,20:30;            --  设为00:00,00:00 表示常亮
     *    添加删除白名单：        IPALARMWLM=0,2,15710089835,112;    --  0是增加，2是两个,
     * 						   IPALARMWLM=1,1,15710089835;        --  1是删除，1代表1个号码
     *    修改白名单开关：        IPALARMWLS=0;                      --  0是关，1是开
     *
     *    注意：短信必须以分号结尾，所有字符需在英文输入法下编辑
     *
     * @param sender
     * @param content
     * @param msgDate
     */
    private void handleSMS(String sender, String content, long msgDate) {
        //支持任意手机号发短信修改 所以不需要判断手机号
        Log.i(TAG, "handleSMS: " + content);
        try {
            String[] array = TextUtils.split(content.trim(), ";");
            for (String item : array) {
                String[] arrayItem = TextUtils.split(item, "=");
                if (arrayItem != null && arrayItem.length >= 2) {
                    String key = arrayItem[0];
                    String value = arrayItem[1];
                    if (key.equals("IPALARMNUM")) {
                        String[] numArray = TextUtils.split(value, ",");
                        String number = numArray[0];
                        String mobile = numArray[1];
                        if (number.equals("1")) {
                            PersistConfig.saveSpecialNum(mobile);
                        } else if (number.equals("2")) {
                            PersistConfig.saveAlarmNum(mobile);
                        }
                        SmsManager.getDefault().sendTextMessage(sender,null,"IPALARMNUM=OK",null,null);
                    } else if (key.equals("IPALARMYUY")) {
                        if (value.equals("1")) {
                            PersistConfig.saveIsIpAlarmFirst(false);
                        } else if (value.equals("0")) {
                            PersistConfig.saveIsIpAlarmFirst(true);
                        }
                        SmsManager.getDefault().sendTextMessage(sender,null,"IPALARMYUY=OK",null,null);
                    } else if (key.equals("IPALARMLED")) {
                        String[] ledArray = TextUtils.split(value, ",");
                        String start = ledArray[0];
                        String end = ledArray[1];
                        PersistConfig.saveAlarmLedTime(start, end);
                        AlarmLedReceiver.sendRepeatAlarmBroadcast(start, end);
                        SmsManager.getDefault().sendTextMessage(sender,null,"IPALARMLED=OK",null,null);
                    } else if (key.equals("IPALARMWLM")) {
                        String[] wlmArray = TextUtils.split(value, ",");
                        String number = wlmArray[0];
                        String conut = wlmArray[1];
                        //白名单ip报文 是通过 ; 来跟分割的，这里转化一下
                        String mobile = wlmArray[2].replace(",", ";");
                        if (number.equals("0")) {
                            PersistWhite.saveList(mobile);
                        } else if (number.equals("1")) {
                            PersistWhite.deleteList(mobile);
                        }
                        SmsManager.getDefault().sendTextMessage(sender,null,"IPALARMWLM=OK",null,null);
                    } else if (key.equals("IPALARMWLS")) {
                        if (value.equals("0")) {
                            PersistConfig.saveIsAcceptOtherNum(true);
                        } else if (value.equals("1")) {
                            PersistConfig.saveIsAcceptOtherNum(false);
                        }
                        SmsManager.getDefault().sendTextMessage(sender,null,"IPALARMWLS=OK",null,null);
                    }
                }
            }
        } catch (Exception e) {

        }
    }


}
