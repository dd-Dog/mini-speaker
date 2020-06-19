// IOtaInter.aidl
package com.abupdate.fota_demo_iot;

import com.abupdate.fota_demo_iot.ICheckVersionAidlInter;
import com.abupdate.fota_demo_iot.IDownloadAidlInter;
import com.abupdate.fota_demo_iot.IGetDeviceInfoAidlInter;
import com.abupdate.fota_demo_iot.IUpdateAidlInter;

interface IOtaInter {

    void getDeviceInfo(IGetDeviceInfoAidlInter iGetDeviceInfoAidlInter);

    String getOtaStatus();

    void checkVersion(ICheckVersionAidlInter iCheckVersionListener);

    void download(IDownloadAidlInter iDownloadListener);

    void pauseDownload();

    void install(IUpdateAidlInter iUpdateAidlInter);
}
