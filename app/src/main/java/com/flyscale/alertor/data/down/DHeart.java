package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.base.BaseDownData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 15:19
 * @DESCRIPTION 暂无
 */
public class DHeart extends BaseDownData {
    @Override
    public int getType() {
        return TYPE_HEART_D;
    }

    @Override
    public BaseData formatToObject(String result) {
        String[] array = formatToArray(result);
        setTradeNum(array[0]);
        setExtra1(array[2]);
        return this;
    }
}
