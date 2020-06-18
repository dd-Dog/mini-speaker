package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.base.BaseUpData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/11 15:02
 * @DESCRIPTION 默认设置为每5秒发送一次心跳包。
 */
public class UHeart extends BaseUpData {
    @Override
    public int getType() {
        return TYPE_HEART_U;
    }

    @Override
    public String formatToString() {
        return formatToString(getImei(),getTradeNum(),getType(),getIccid()
                ,getAC(),getBatteryLevel(),getStationInfo(),getGpsStatus(),getTerminalType()
                ,getNetType(),getTerminalModel(),getFirstLoginTime());
    }
}
