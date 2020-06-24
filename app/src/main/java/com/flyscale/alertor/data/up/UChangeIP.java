package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.base.BaseUpData;
import com.flyscale.alertor.data.base.UDefaultChange;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 11:34
 * @DESCRIPTION 2.11修改IP地址及端口
 * 平台下发新的号码，终端接收到自动修改并返回结果若修改失败旧的地址、
 * 端口依然有效，如果新ip调用10次还不通，改回旧ip，有测试结果（新ip是否启用）返回上行信息。
 */
public class UChangeIP extends UDefaultChange {

    public UChangeIP(String changeResult,String tradeNum) {
        super(changeResult,tradeNum);
    }

    @Override
    public int getType() {
        return TYPE_CHANGE_IP_U;
    }
}
