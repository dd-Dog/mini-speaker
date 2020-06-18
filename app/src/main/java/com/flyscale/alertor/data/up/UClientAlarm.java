package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.base.BaseUpData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 11:55
 * @DESCRIPTION 2.14移动终端报警
 * 1、移动终端触发报警，每5秒发送一次报警信息（包含位置信息），当收到平台回执信息（报警结果为1），停止发送报警信息。终端具备手动停止发送报警信息功能。
 * 2、若在报警过程中，未收到回执信息（报警结果为1），但丢失网络信号，恢复网络信号后仍然要保持5秒发送一次报警的状态。
 * 3、从报警到收到平台回执信息（报警结果为1）的周期内，流水号不变；报警周期内，再次触发报警无效。
 */
public class UClientAlarm extends BaseUpData {
    @Override
    public int getType() {
        return TYPE_CLIENT_ALARM_U;
    }

    @Override
    public String formatToString() {
        return formatToString(getImei(),getTradeNum(),getType(),getIccid(),getCurrentTime()
                ,getLon(),getLat(),getExtra1(),getExtra2(),getExtra3(),getExtra4(),getExtra5());
    }
}
