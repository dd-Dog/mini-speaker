package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.base.BaseDownData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 11:49
 * @DESCRIPTION 暂无
 */
public class DUpdateNumberRing extends BaseDownData {
    @Override
    public int getType() {
        return TYPE_UPDATE_NUMBER_RING_D;
    }

    @Override
    public BaseData formatToObject(String result) {
        String[] array = formatToArray(result);
        setTradeNum(array[0]);
        setMobileNum(array[2]);
        setWhiteList(array[3]);
        setWhiteListRingNum(Integer.parseInt(array[4]));
        setMessageSize(array[5]);
        setMessageBodyResp(array[6]);
        setExtra1(array[7]);
        setExtra2(array[8]);
        return this;
    }
}
