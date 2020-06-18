package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.base.BaseDownData;
import com.flyscale.alertor.data.base.DDefaultAlarm;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 9:06
 * @DESCRIPTION 终端报警，未收到回复消息，再次发送报警，三次后还是没有收到平台回复转168语音报警。
 * 1秒1次
 */
public class DAlarm extends DDefaultAlarm {

    @Override
    public int getType() {
        return TYPE_ALARM_D;
    }
}
