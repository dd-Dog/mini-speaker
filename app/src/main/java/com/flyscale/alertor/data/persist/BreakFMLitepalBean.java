package com.flyscale.alertor.data.persist;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class BreakFMLitepalBean extends LitePalSupport {
    /**
     * //运用注解来为字段添加index标签
     * //name是唯一的不可重复，且默认值为unknown
     * @Column(unique = true, defaultValue = "unknown")
     * //忽略即是不在数据库中创建该属性对应的字段
     * @Column(ignore = true)
     * //不为空
     * @Column(nullable = false)
     *
     * wd,00000180,20170620/094.900/080000/090000/1xxxx
     * wd,00000181,20170621/105.300/090000/100000/0xxxx
     * wd,00000182,20170728/107.700/103830/104530/91f4e
     * */
    @Column(unique = true, defaultValue = "unknown")
    String name = "unknown";
    String startDate = "00000000";
    String freq = "0.0";
    String startTime = "00:00";
    String endTime = "00:00";
    String volume = "0";
    String isSetUp = "false";
    String address = "";
    String data = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFreq() {
        return freq;
    }

    public void setFreq(String freq) {
        this.freq = freq;
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

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getIsSetUp() {
        return isSetUp;
    }

    public void setIsSetUp(String isSetUp) {
        this.isSetUp = isSetUp;
    }

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
}
