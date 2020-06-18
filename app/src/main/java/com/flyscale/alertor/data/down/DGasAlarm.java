package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.DDefaultAlarm;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 13:49
 * @DESCRIPTION 暂无
 */
public class DGasAlarm extends DDefaultAlarm {
    @Override
    public int getType() {
        return TYPE_GAS_ALARM_D;
    }
}
