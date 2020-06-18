package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.base.BaseUpData;
import com.flyscale.alertor.data.base.UDefaultChange;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 10:53
 * @DESCRIPTION 暂无
 */
public class UDeleteVoice extends UDefaultChange {

    public UDeleteVoice(String changeResult) {
        super(changeResult);
    }

    @Override
    public int getType() {
        return TYPE_DELETE_VOICE_U;
    }
}
