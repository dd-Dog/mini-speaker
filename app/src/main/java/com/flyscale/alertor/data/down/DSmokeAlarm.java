package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.base.BaseDownData;
import com.flyscale.alertor.data.base.DDefaultAlarm;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 13:35
 * @DESCRIPTION 暂无
 */
public class DSmokeAlarm extends DDefaultAlarm {
    @Override
    public int getType() {
        return TYPE_SMOKE_ALARM_D;
    }
}
