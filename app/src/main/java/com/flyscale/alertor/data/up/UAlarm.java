package com.flyscale.alertor.data.up;
import com.flyscale.alertor.data.annotations.noteSendCount;
import com.flyscale.alertor.data.base.UDefaultAlarm;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 17:06
 * @DESCRIPTION 终端报警，未收到回复消息，再次发送报警，三次后还是没有收到平台回复转168语音报警。
 */
public class UAlarm extends UDefaultAlarm {

    public static int sSendCount = 1;

    public UAlarm(@noteSendCount int sendCount) {
        super(sendCount);
    }

    @Override
    public int getType() {
        return TYPE_ALARM_U;
    }
}
