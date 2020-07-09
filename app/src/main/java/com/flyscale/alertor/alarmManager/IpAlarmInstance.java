package com.flyscale.alertor.alarmManager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author 高鹤泉
 * @TIME 2020/7/8 13:11
 * @DESCRIPTION 暂无
 */
@Deprecated
public class IpAlarmInstance {
    private static final IpAlarmInstance ourInstance = new IpAlarmInstance();
    public static final int STATUS_NONE = 0;//初始状态
    public static final int STATUS_ALARMING = 1;//正在报警
    public static final int STATUS_ALARM_SUCCESS = 2;//报警成功
    public static final int STATUS_ALARM_FAIL =3;//报警失败

    String TAG = "IpAlarmInstance";
    int mStatus = STATUS_NONE;
    int mSendCount =1;

    public static IpAlarmInstance getInstance() {
        return ourInstance;
    }

    private IpAlarmInstance() {

    }

    public void polling(int type){
        if(AlarmManager.isFineNet()){

        }
    }

    public class AlarmHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1003){

            }
        }
    }
}
