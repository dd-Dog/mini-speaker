package com.flyscale.alertor.receivers;

/**
 * @author 高鹤泉
 * @TIME 2020/7/2 12:00
 * @DESCRIPTION 暂无
 */
public interface BRConstant {

    //是否市电充电
    String ACTION_AC = "flyscale.privkey.adapter";
    //来电广播（有电话呼入）
    String ACTION_PHONE_INCOME = "com.android.phone.FLYSCALE_PHONE_STATE.INCOMING_CALL";
    //自检
    String ACTION_CHECK_SELF = "flyscale.privkey.SELF_CHECK";
    //报警灯开关
    String ACTION_ALARM_LED_STATUS = "flyscale.privkey.status.ALARM_LED";
    //ADB开关  (能否连接usb)
    String ACTION_USB_TOGGLE = "flyscale.privkey.TOGGLE_USB_CONFIG";
    //学习键按下
    String ACTION_STUDY_DOWN = "flyscale.privkey.SENSOR_LEARN.down";
    String ACTION_STUDY_UP = "flyscale.privkey.SENSOR_LEARN.up";
}
