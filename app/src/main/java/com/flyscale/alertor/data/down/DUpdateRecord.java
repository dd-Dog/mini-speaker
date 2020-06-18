package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.base.BaseDownData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 13:17
 * @DESCRIPTION 暂无
 */
public class DUpdateRecord extends BaseDownData {
    @Override
    public int getType() {
        return TYPE_CLIENT_UPDATE_RECORD_D;
    }

    //结果	STRING[1]	1表示收到音频文件，0没有收到音频文件
    @Override
    public BaseData formatToObject(String result) {
        String[] array = formatToArray(result);
        setTradeNum(array[0]);
        setFileNum(array[2]);
        setAlarmSerNum(array[3]);
        setChangeResult(array[4]);
        setExtra1(array[5]);
        return this;
    }
}
