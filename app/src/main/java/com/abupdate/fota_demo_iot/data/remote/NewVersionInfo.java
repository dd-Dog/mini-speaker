package com.abupdate.fota_demo_iot.data.remote;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author fighter_lee
 * @date 2018/7/16
 */
public class NewVersionInfo implements Parcelable {

    public String oldVersionName;
    public String versionName;
    public String versionAlias;
    public String md5sum;
    public String deltaUrl;
    public String deltaID;
    public String publishDate;
    public String content;
    public long fileSize;

    public NewVersionInfo() {

    }

    protected NewVersionInfo(Parcel in) {
        oldVersionName = in.readString();
        versionName = in.readString();
        versionAlias = in.readString();
        md5sum = in.readString();
        deltaUrl = in.readString();
        deltaID = in.readString();
        publishDate = in.readString();
        content = in.readString();
        fileSize = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(oldVersionName);
        dest.writeString(versionName);
        dest.writeString(versionAlias);
        dest.writeString(md5sum);
        dest.writeString(deltaUrl);
        dest.writeString(deltaID);
        dest.writeString(publishDate);
        dest.writeString(content);
        dest.writeLong(fileSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NewVersionInfo> CREATOR = new Creator<NewVersionInfo>() {
        @Override
        public NewVersionInfo createFromParcel(Parcel in) {
            return new NewVersionInfo(in);
        }

        @Override
        public NewVersionInfo[] newArray(int size) {
            return new NewVersionInfo[size];
        }
    };

    @Override
    public String toString() {
        return "NewVersionInfo{" +
                "oldVersionName='" + oldVersionName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionAlias='" + versionAlias + '\'' +
                ", md5sum='" + md5sum + '\'' +
                ", deltaUrl='" + deltaUrl + '\'' +
                ", deltaID='" + deltaID + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", content='" + content + '\'' +
                ", fileSize=" + fileSize +
                '}';
    }
}
