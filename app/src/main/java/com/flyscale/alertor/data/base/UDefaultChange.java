package com.flyscale.alertor.data.base;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 14:09
 * @DESCRIPTION 暂无
 */
public abstract class UDefaultChange extends BaseUpData {

    public UDefaultChange(String changeResult) {
        this.changeResult = changeResult;
    }

    @Override
    public String formatToString() {
        return formatToString(getImei(),getTradeNum(),getType(),getIccid(),changeResult,getExtra1(),getExtra2());
    }
}
