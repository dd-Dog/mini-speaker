package com.abupdate.fota_demo_iot.data.local;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author fighter_lee
 * @date 2018/7/17
 */
public class DeviceAidlInfo implements Parcelable {

    public String mid;
    public String oem;
    public String models;
    public String platform;
    public String deviceType;
    public String productId;
    public String productSecret;
    public String version;

    public DeviceAidlInfo() {

    }

    protected DeviceAidlInfo(Parcel in) {
        mid = in.readString();
        oem = in.readString();
        models = in.readString();
        platform = in.readString();
        deviceType = in.readString();
        productId = in.readString();
        productSecret = in.readString();
        version = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mid);
        dest.writeString(oem);
        dest.writeString(models);
        dest.writeString(platform);
        dest.writeString(deviceType);
        dest.writeString(productId);
        dest.writeString(productSecret);
        dest.writeString(version);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DeviceAidlInfo> CREATOR = new Creator<DeviceAidlInfo>() {
        @Override
        public DeviceAidlInfo createFromParcel(Parcel in) {
            return new DeviceAidlInfo(in);
        }

        @Override
        public DeviceAidlInfo[] newArray(int size) {
            return new DeviceAidlInfo[size];
        }
    };
}
