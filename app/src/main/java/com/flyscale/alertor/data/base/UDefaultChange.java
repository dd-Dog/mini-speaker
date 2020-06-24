package com.flyscale.alertor.data.base;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 14:09
 * @DESCRIPTION 暂无
 */
public abstract class UDefaultChange extends BaseUpData {

    public UDefaultChange(String changeResult) {
        this.changeResult = changeResult;
        this.tradeNum = getTradeNum();
    }

    public UDefaultChange(String changeResult,String tradeNum) {
        this.changeResult = changeResult;
        this.tradeNum = tradeNum;
    }


    @Override
    public String formatToString() {
        return formatToString(getImei(),tradeNum,getType(),getIccid(),changeResult,getExtra1(),getExtra2());
    }
}
