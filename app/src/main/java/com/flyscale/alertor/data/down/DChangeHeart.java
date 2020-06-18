package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.base.BaseDownData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 10:49
 * @DESCRIPTION 暂无
 */
public class DChangeHeart extends BaseDownData {
    @Override
    public int getType() {
        return TYPE_CHANGE_HEART_D;
    }

    @Override
    public BaseData formatToObject(String result) {
        String[] array = formatToArray(result);
        setTradeNum(array[0]);
        setHeartHZ(Integer.parseInt(array[2]));
        setExtra1(array[3]);
        setExtra2(array[4]);
        return this;
    }
}
