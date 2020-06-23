package com.flyscale.alertor;

import android.util.Log;

import com.abupdate.fota_demo_iot.data.remote.NewVersionInfo;
import com.flyscale.alertor.helper.FotaHelper;

/**
 * @author 高鹤泉
 * @TIME 2020/6/23 13:49
 * @DESCRIPTION 暂无
 */
public class FotaAction implements FotaHelper.FotaHelperCallback {
    
    String TAG = "FotaAction";
    
    @Override
    public void onRemoteServiceConnected() {
        Log.i(TAG, "onRemoteServiceConnected: ");
    }

    @Override
    public void onRemoteServiceDisconnected() {
        Log.i(TAG, "onRemoteServiceDisconnected: ");
    }

    @Override
    public void hasNewVersion(NewVersionInfo newVersionInfo) {
        Log.i(TAG, "hasNewVersion: ");
    }

    @Override
    public void noNewVersion(int code) {
        Log.i(TAG, "noNewVersion: ");
    }

    @Override
    public void onDownloadStart() {
        Log.i(TAG, "onDownloadStart: ");
    }

    @Override
    public void onDownloadProgress(int progress) {
        Log.i(TAG, "onDownloadProgress: ");
    }

    @Override
    public void onDownloadFail(int code) {
        Log.i(TAG, "onDownloadFail: ");
    }

    @Override
    public void onDownloadonCancel() {
        Log.i(TAG, "onDownloadonCancel: ");
    }

    @Override
    public void onDownloadSuccess() {
        Log.i(TAG, "onDownloadSuccess: ");
    }

    @Override
    public void onDownloadPause() {
        Log.i(TAG, "onDownloadPause: ");
    }

    @Override
    public void enterRecoveryFail(int code) {
        Log.i(TAG, "enterRecoveryFail: ");
    }

    @Override
    public void upgradeProgress(int progress) {
        Log.i(TAG, "upgradeProgress: ");
    }

    @Override
    public void upgradeSuccess() {
        Log.i(TAG, "upgradeSuccess: ");
    }
}
