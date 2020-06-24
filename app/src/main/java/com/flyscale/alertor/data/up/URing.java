package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.annotations.noteSendCount;
import com.flyscale.alertor.data.base.BaseUpData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 10:07
 * @DESCRIPTION 业务平台收到报警信息，发送下行响铃信息。
 */
public class URing extends BaseUpData {


    public URing(@noteSendCount int sendCount,String tradeNum) {
        this.sendCount = sendCount;
        this.tradeNum = tradeNum;
    }

    @Override
    public int getType() {
        return TYPE_RING_U;
    }

    @Override
    public String formatToString() {
        return formatToString(getImei(),tradeNum,getType(),getIccid(),getSendCount(),getExtra1(),getExtra2());
    }
}
