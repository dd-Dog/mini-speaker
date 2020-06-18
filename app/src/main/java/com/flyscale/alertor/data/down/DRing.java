package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.base.BaseDownData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 10:21
 * @DESCRIPTION 暂无
 */
public class DRing extends BaseDownData {
    @Override
    public int getType() {
        return TYPE_RING_D;
    }

    @Override
    public BaseData formatToObject(String result) {
        String[] array = formatToArray(result);
        setTradeNum(array[0]);
        setSendCount(Integer.parseInt(array[2]));
        setMobileNum(array[3]);
        setExtra1(array[4]);
        setExtra1(array[5]);
        return this;
    }
}
