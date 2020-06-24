package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.base.BaseUpData;
import com.flyscale.alertor.data.base.UDefaultChange;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 11:07
 * @DESCRIPTION  修改报警拨叫的号码
 * 平台下发新的号码，终端接收到自动修改并返回结果
 */
public class UChangeAlarmNumber extends UDefaultChange {

    public UChangeAlarmNumber(String changeResult,String tradeNum) {
        super(changeResult,tradeNum);
    }

    @Override
    public int getType() {
        return TYPE_CHANGE_ALARM_NUMBER_U;
    }
}
