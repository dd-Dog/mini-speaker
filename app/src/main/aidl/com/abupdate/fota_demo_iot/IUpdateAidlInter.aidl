// IUpdateAidlInter.aidl
package com.abupdate.fota_demo_iot;

interface IUpdateAidlInter {

    void enterRecoveryFail(int code);

    void upgradeProgress(int progress);

    void upgradeSuccess();
}
