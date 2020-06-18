package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.base.BaseDownData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 10:31
 * @DESCRIPTION 暂无
 */
public class DVoice extends BaseDownData {
    @Override
    public int getType() {
        return TYPE_VOICE_D;
    }

    @Override
    public BaseData formatToObject(String result) {
        String[] array = formatToArray(result);
        setTradeNum(array[0]);
        setMobileNum(array[2]);
        setSendCount(Integer.parseInt(array[3]));
        setIsCompress(array[4]);
        setTotalPacket(array[5]);
        setPacketNum(array[6]);
        setMessageSize(array[7]);
        setExtra1(array[8]);
        setExtra2(array[9]);
        setVoiceText(array[10]);
        setMessageBodyResp(array[11]);
        return this;
    }
}
