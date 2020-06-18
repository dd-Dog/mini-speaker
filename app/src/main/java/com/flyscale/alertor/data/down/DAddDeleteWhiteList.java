package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.base.BaseDownData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 11:43
 * @DESCRIPTION 暂无
 */
public class DAddDeleteWhiteList extends BaseDownData {
    @Override
    public int getType() {
        return TYPE_ADD_OR_DELETE_WHITE_LIST_D;
    }

    @Override
    public BaseData formatToObject(String result) {
        String[] array = formatToArray(result);
        setTradeNum(array[0]);
        setAddOrDeleteFlag(array[2]);
        setMobileNum(array[3]);
        setWhiteList(array[4]);
        setExtra1(array[5]);
        setExtra2(array[6]);
        return this;
    }
}
