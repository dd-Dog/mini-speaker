package com.flyscale.alertor.data.persist;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class FMLitepalBean extends LitePalSupport {
    /**
     * //运用注解来为字段添加index标签
     * //name是唯一的不可重复，且默认值为unknown
     * @Column(unique = true, defaultValue = "unknown")
     * //忽略即是不在数据库中创建该属性对应的字段
     * @Column(ignore = true)
     * //不为空
     * @Column(nullable = false)
     * */
    @Column(unique = true, defaultValue = "unknown")
    String name = "unknown";
    String startDate = "127";
    String freq = "0.0";
    String startTime = "00:00";
    String endTime = "00:00";
    String volume = "0";
    String isSetUp = "false";
    String address = "";
    String data = "";

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getIsSetUp() {
        return isSetUp;
    }

    public void setIsSetUp(String isSetUp) {
        this.isSetUp = isSetUp;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }



}
