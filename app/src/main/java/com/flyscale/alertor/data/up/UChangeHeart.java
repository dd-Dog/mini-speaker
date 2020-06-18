package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.base.BaseUpData;
import com.flyscale.alertor.data.base.UDefaultChange;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 10:45
 * @DESCRIPTION 2.5修改心跳频率
 * 2.5.1说明
 * 业务平台向终端发起修改心跳包频率请求，终端修改心跳包频率。
 */
public class UChangeHeart extends UDefaultChange {

    public UChangeHeart(String changeResult) {
        super(changeResult);
    }

    @Override
    public int getType() {
        return TYPE_CHANGE_HEART_U;
    }
}
