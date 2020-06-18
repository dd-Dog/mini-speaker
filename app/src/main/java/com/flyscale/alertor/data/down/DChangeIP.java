package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.base.BaseDownData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 11:37
 * @DESCRIPTION 暂无
 */
public class DChangeIP extends BaseDownData {
    @Override
    public int getType() {
        return TYPE_CHANGE_IP_D;
    }

    @Override
    public BaseData formatToObject(String result) {
        String[] array = formatToArray(result);
        setTradeNum(array[0]);
        setMobileNum(array[2]);
        setIpAddress(array[3]);
        setIpPort(array[4]);
        setExtra1(array[5]);
        setExtra2(array[6]);
        return this;
    }
}
