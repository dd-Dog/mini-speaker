package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.base.BaseUpData;
import com.flyscale.alertor.data.base.UDefaultChange;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 11:46
 * @DESCRIPTION 2.13更新对应号码在终端的响铃铃声
 * 上传最新响铃铃声，终端接收到自动修改并返回结果
 */
public class UUpdateNumberRing extends UDefaultChange {

    public UUpdateNumberRing(String changeResult) {
        super(changeResult);
    }

    @Override
    public int getType() {
        return TYPE_UPDATE_NUMBER_RING_U;
    }

}
