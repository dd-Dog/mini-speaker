package com.flyscale.alertor.data.base;

import com.flyscale.alertor.data.annotations.noteSendCount;
/**
 * @author 高鹤泉
 * @TIME 2020/6/12 13:37
 * @DESCRIPTION 暂无
 */
public abstract class UDefaultAlarm extends BaseUpData {

    public UDefaultAlarm(@noteSendCount int sendCount) {
        this.sendCount = sendCount;
    }

    @Override
    public String formatToString() {
        return formatToString(getImei(),getTradeNum(),getType(),getIccid(),sendCount,getCurrentTime(),getGpsStatus()
                ,getExtra1(),getExtra2(),getExtra3(),getExtra4(),getExtra5());
    }
}
