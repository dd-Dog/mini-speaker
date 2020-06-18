package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.base.BaseUpData;
import com.flyscale.alertor.data.base.UDefaultChange;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 11:16
 * @DESCRIPTION 2.9修改测试键的拨叫测试号码
 * 平台下发新的号码，终端接收到自动修改并返回结果
 */
public class UChangeTestKeyNumber extends UDefaultChange {

    public UChangeTestKeyNumber(String changeResult) {
        super(changeResult);
    }

    @Override
    public int getType() {
        return TYPE_CHANGE_TEST_KEY_NUMBER_U;
    }

}
