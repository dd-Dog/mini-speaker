package com.flyscale.alertor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author bianjb
 * 短信监听广播
 */
public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSReceiver";
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    //短信指令格式 key:value，长度限定20个字符
    private static final String SPLIT_FLAG = ":";
    private static final String SMS_FORMAT_REGEX = "^\\w{1,20}" + SPLIT_FLAG + "\\w{1,20}$";


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

    private void handleSMS(String sender, String content, long msgDate) {
        //支持任意手机号发短信修改 所以不需要判断手机号
//        if (!checkSender(sender)) return;
        if (!TextUtils.isEmpty(content) && content.matches(SMS_FORMAT_REGEX)) {
            String[] split = content.split(SPLIT_FLAG);
            String key = split[0];
            String value = split[1];
            Log.i(TAG, "handleSMS: key=" + key + ",value=" + value);

            if (isValidKey(key)) {
                //TODO 持久化处理
            }
        }
    }

    private boolean isValidKey(String key) {
        //TODO 判断指令key是否正确

        return false;
    }

    private boolean checkSender(String sender) {
        //TODO 检查发件人是否在白名单

        return true;
    }


}
