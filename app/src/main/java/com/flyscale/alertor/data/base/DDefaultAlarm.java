package com.flyscale.alertor.data.base;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 13:43
 * @DESCRIPTION 暂无
 */
public abstract class DDefaultAlarm extends BaseDownData {

    @Override
    public BaseData formatToObject(String result) {
        String[] array = formatToArray(result);
        setTradeNum(array[0]);
        setAlarmResult(array[2]);
        setSendCount(Integer.parseInt(array[3]));
        setExtra1(array[4]);
        setExtra2(array[5]);
        setExtra3(array[6]);
        setExtra4(array[7]);
        setExtra5(array[8]);
        return this;
    }
}
