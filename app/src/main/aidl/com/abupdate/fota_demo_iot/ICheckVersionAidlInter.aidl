// ICheckVersionAidlInter.aidl
package com.abupdate.fota_demo_iot;

import com.abupdate.fota_demo_iot.data.remote.NewVersionInfo;

interface ICheckVersionAidlInter {

    void hasNewVersion(in NewVersionInfo versionInfo);

    void noNewVersion(int code);
}
