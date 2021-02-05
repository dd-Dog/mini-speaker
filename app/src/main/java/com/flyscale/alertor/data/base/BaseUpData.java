package com.flyscale.alertor.data.base;

import com.flyscale.alertor.base.BaseApplication;
import com.flyscale.alertor.data.base.BaseData;
import com.flyscale.alertor.helper.ClientInfoHelper;
import com.flyscale.alertor.helper.DateHelper;
import com.flyscale.alertor.helper.LocationHelper;
import com.flyscale.alertor.helper.Num9999Helper;

/**
 * @author 高鹤泉
 * @TIME 2020/6/12 9:09
 * @DESCRIPTION 暂无
 */
public abstract class BaseUpData extends BaseData {

    //设备的IMEI ，终端业务平台的上行报文加入标识字段
    @Override
    public String getImei() {
        return ClientInfoHelper.getIMEI();
    }

    //交易流水号 时间格式+4位循环 时间格式：yyyyMMddHHmmss
    //4位循环数：范围0-9999，从0开始，递增赋值，步长为1，增加到9999后，再从0开始
    @Override
    public String getTradeNum() {
        return DateHelper.longToString(DateHelper.yyyyMMddHHmmss) + Num9999Helper.formatNum();
    }

    //设备号码
    @Override
    public String getIccid() {
        return ClientInfoHelper.getICCID();
    }

    //市电 是否接入220V电源，0接入，1未接入
    @Override
    public int getAC() {
//        return ClientInfoHelper.getAC();
        return 1;
    }

    //电量百分比
    @Override
    public String getBatteryLevel() {
        return ClientInfoHelper.getBatteryLevel();
    }

    //基站信息  STRING[10]  基站1@基站2@基站3；按基站距离排序由近到远
    @Override
    public String getStationInfo() {
        return ClientInfoHelper.getStationsInfo();
    }

    //GPS状态	STRING[10]	0--关闭  1-开启
    @Override
    public int getGpsStatus() {
        boolean b = LocationHelper.gpsIsOpen(BaseApplication.sContext);
        return b ? 1 : 0;
    }

    //终端类型	STRING[10]	001TCL，002卡尔
    @Override
    public String getTerminalType() {
        return ClientInfoHelper.getTerminalInfo();
    }

    //网络制式	STRING
    @Override
    public String getNetType() {
        return ClientInfoHelper.getNetType();
    }

    //型号	STRING	厂家定义终端型号
    @Override
    public String getTerminalModel() {
        return ClientInfoHelper.getTerminalModel();
    }

    //终端第一次登录时间	STRING	时间格式：yyyy-MM-dd hh:mm:ss，记录终端第一次登录平台的时间，永久不变
    @Override
    public String getFirstLoginTime() {
        return ClientInfoHelper.getFirstLoginTime();
    }

    //报警时间	STRING[10]	时间格式：yyyyMMddHHmm
    @Override
    public String getCurrentTime() {
        return DateHelper.longToString(DateHelper.yyyyMMddHHmm);
    }

    @Override
    public String getLat() {
        return LocationHelper.getLat();
    }

    @Override
    public String getLon() {
        return LocationHelper.getLon();
    }

    @Override
    public BaseData formatToObject(String result) {
        return null;
    }
}
