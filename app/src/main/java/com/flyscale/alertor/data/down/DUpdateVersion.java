package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.base.BaseDownData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 11:02
 * @DESCRIPTION 暂无
 */
public class DUpdateVersion extends BaseDownData {
    @Override
    public int getType() {
        return TYPE_UPDATE_VERSION_D;
    }

    @Override
    public BaseData formatToObject(String result) {
        String[] array = formatToArray(result);
        setTradeNum(array[0]);
        setFactoryCode(array[2]);
        setIsCompress(array[3]);
        setTotalPacket(array[4]);
        setPacketNum(array[5]);
        setMessageSize(array[6]);
        setExtra1(array[7]);
        setExtra2(array[8]);
        setMessageBodyResp(array[9]);
        return this;
    }
}
