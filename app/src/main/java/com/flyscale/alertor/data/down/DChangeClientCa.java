package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.base.BaseDownData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 13:56
 * @DESCRIPTION 暂无
 */
public class DChangeClientCa extends BaseDownData {
    @Override
    public int getType() {
        return TYPE_CHANGE_CLIENT_CA_D;
    }

    @Override
    public BaseData formatToObject(String result) {
        String[] array = formatToArray(result);
        setTradeNum(array[0]);
        setClientCaMessage(array[2]);
        setClientPwdMessage(array[3]);
        setRootCaMessage(array[4]);
        setIpAddress(array[5]);
        setIpPort(array[6]);
        setExtra1(array[7]);
        return this;
    }
}
