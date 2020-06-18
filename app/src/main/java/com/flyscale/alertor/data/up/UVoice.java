package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.annotations.noteSendCount;
import com.flyscale.alertor.data.base.BaseUpData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 10:24
 * @DESCRIPTION 业务平台收到报警信息，发送下行语音信息。语音文件不分包发送。
 * 设置状态:0表示成功，1表示失败
 * 总包数:语音报分包总数
 * 包序号:包序号,比如是第几个包
 * 终端录音存储状态：0表示空间满，1表示空间不满，可以继续接受录音
 */
public class UVoice extends BaseUpData {

    public UVoice(@noteSendCount int sendCount, String messageBody) {
        this.sendCount = sendCount;
        this.messageBody = messageBody;
    }

    @Override
    public int getType() {
        return TYPE_VOICE_U;
    }

    @Override
    public String formatToString() {
        return formatToString(getImei(),getTradeNum(),getType(),getIccid(),sendCount,messageBody,getExtra1(),getExtra2());
    }
}
