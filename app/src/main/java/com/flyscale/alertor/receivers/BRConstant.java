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
}
