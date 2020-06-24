package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.base.BaseUpData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 10:59
 * @DESCRIPTION 终端版本升级，业务平台向所有终端推送升级包。分包工作由厂家完成，升级包存储空间需要提前预留。
 * 升级包为.gz格式，分包10个以内，每个包不超过10kb，建议升级包大小控制在100KB以内。
 */
public class UUpdateVersion extends BaseUpData {

    //报文体 	STRING[0-128]	总包数@包序号@接收状态@失败原因
    public UUpdateVersion(String messageBody,String tradeNum) {
        this.messageBody = messageBody;
        this.tradeNum = tradeNum;
    }

    @Override
    public int getType() {
        return TYPE_UPDATE_VERSION_U;
    }

    @Override
    public String formatToString() {
        return formatToString(getImei(),tradeNum,getType(),getIccid(),messageBody,getExtra1(),getExtra2());
    }
}
