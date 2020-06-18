package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.base.BaseDownData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 13:05
 * @DESCRIPTION 暂无
 */
public class DClientAlarm extends BaseDownData {
    @Override
    public int getType() {
        return TYPE_CLIENT_ALARM_D;
    }

    @Override
    public BaseData formatToObject(String result) {
        String[] array = formatToArray(result);
        setTradeNum(array[0]);
        setAlarmResult(array[2]);
        setExtra1(array[3]);
        setExtra2(array[4]);
        setExtra3(array[5]);
        setExtra4(array[6]);
        setExtra5(array[7]);
        return this;
    }
}
