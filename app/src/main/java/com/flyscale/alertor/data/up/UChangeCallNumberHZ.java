package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.base.BaseUpData;
import com.flyscale.alertor.data.base.UDefaultChange;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 11:25
 * @DESCRIPTION 2.10修改主动拨打的电话、及频率
 * 平台下发新的号码及拨打的频率，终端接收到自动修改并返回结果
 */
public class UChangeCallNumberHZ extends UDefaultChange {

    public UChangeCallNumberHZ(String changeResult) {
        super(changeResult);
    }

    @Override
    public int getType() {
        return TYPE_CHANGE_CALL_HZ_U;
    }
}
