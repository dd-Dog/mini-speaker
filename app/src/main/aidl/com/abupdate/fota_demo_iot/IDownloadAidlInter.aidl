// IDownloadAidlInter.aidl
package com.abupdate.fota_demo_iot;

// Declare any non-default types here with import statements

interface IDownloadAidlInter {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onStart();

    void onProgress(int progress);

    void onFail(int code);

    void onCancel();

    void onSuccess();
}
