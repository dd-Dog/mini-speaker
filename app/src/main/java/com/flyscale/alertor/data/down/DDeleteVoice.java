package com.flyscale.alertor.data.down;

import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.data.base.BaseDownData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 10:56
 * @DESCRIPTION 暂无
 */
public class DDeleteVoice extends BaseDownData {
    @Override
    public int getType() {
        return TYPE_DELETE_VOICE_D;
    }

    @Override
    public BaseData formatToObject(String result) {
        String[] array = formatToArray(result);
        setTradeNum(array[0]);
        setMobileNum(array[2]);
        setExtra1(array[3]);
        setExtra2(array[4]);
        return this;
    }
}
