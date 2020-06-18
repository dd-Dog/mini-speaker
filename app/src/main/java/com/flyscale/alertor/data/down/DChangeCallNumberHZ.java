package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.base.BaseDownData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 11:28
 * @DESCRIPTION 暂无
 */
public class DChangeCallNumberHZ extends BaseDownData {
    @Override
    public int getType() {
        return TYPE_CHANGE_CALL_HZ_D;
    }

    @Override
    public BaseData formatToObject(String result) {
        String[] array = formatToArray(result);
        setTradeNum(array[0]);
        setCallNumber(array[2]);
        setDayHZ(Integer.parseInt(array[3]));
        setExtra1(array[4]);
        setExtra2(array[5]);
        return this;
    }
}
