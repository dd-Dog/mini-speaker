// IGetDeviceInfoAidlInter.aidl
package com.abupdate.fota_demo_iot;

import com.abupdate.fota_demo_iot.data.local.DeviceAidlInfo;

interface IGetDeviceInfoAidlInter {

    void onSuccess(in DeviceAidlInfo deviceAidlInfo);

}
