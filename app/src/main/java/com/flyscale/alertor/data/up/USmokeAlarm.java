package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.annotations.noteSendCount;
import com.flyscale.alertor.data.base.BaseUpData;
import com.flyscale.alertor.data.base.UDefaultAlarm;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 13:32
 * @DESCRIPTION 2.17烟感报警
 * 终端每触发一次报警，向平台发送三遍报警上行信息（重复三遍报警上行消息的流水号不能改变）；
 * 平台接收报警消息，每次接收上行报警消息，都会回复一次下行报警消息（重复流水号也会回复），
 * 回复三遍响铃下行消息（重复流水号不回复响铃下行消息），三遍语音下行消息（重复流水号不回复语音下行消息）。补充：未收到报警回复，调用168电话报警
 */
public class USmokeAlarm extends UDefaultAlarm {

    public USmokeAlarm(@noteSendCount int sendCount) {
        super(sendCount);
    }

    @Override
    public int getType() {
        return TYPE_SMOKE_ALARM_U;
    }
}
