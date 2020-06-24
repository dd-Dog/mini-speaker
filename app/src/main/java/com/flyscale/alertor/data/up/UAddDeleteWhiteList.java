package com.flyscale.alertor.data.up;

import com.flyscale.alertor.data.base.BaseUpData;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 11:40
 * @DESCRIPTION 2.12添加、删除电话白名单
 * 平台下发增加或删除白名单号码，终端接收到自动修改并返回结果。白名单内容1-500个。
 */
public class UAddDeleteWhiteList extends BaseUpData {

    public UAddDeleteWhiteList(String changeResult,String tradeNum) {
        this.changeResult = changeResult;
        this.tradeNum = tradeNum;
    }

    @Override
    public int getType() {
        return TYPE_ADD_OR_DELETE_WHITE_LIST_U;
    }

    @Override
    public String formatToString() {
        return formatToString(getImei(),tradeNum,getType(),getIccid(),changeResult,getExtra1(),getExtra2());
    }
}
