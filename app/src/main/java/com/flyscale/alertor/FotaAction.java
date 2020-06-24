package com.flyscale.alertor;

import android.util.Log;

import com.abupdate.fota_demo_iot.data.remote.NewVersionInfo;
import com.flyscale.alertor.data.up.UUpdateVersion;
import com.flyscale.alertor.helper.FotaHelper;
import com.flyscale.alertor.netty.NettyHelper;

/**
 * @author 高鹤泉
 * @TIME 2020/6/23 13:49
 * @DESCRIPTION 暂无
 */
public class FotaAction implements FotaHelper.FotaHelperCallback {
    
    String TAG = "FotaAction";
    String mTotal,mNum,mTradeNum;

    public void setTotal(String total) {
        mTotal = total;
    }

    public void setNum(String num) {
        mNum = num;
    }

    public void setTradeNum(String tradeNum) {
        mTradeNum = tradeNum;
    }

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
        String message = mTotal + "@" + mNum + "@1@" + code;
        NettyHelper.getInstance().send(new UUpdateVersion(message,mTradeNum));
    }

    @Override
    public void upgradeProgress(int progress) {
        Log.i(TAG, "upgradeProgress: ");
    }

    @Override
    public void upgradeSuccess() {
        //总包数@包序号@接收状态@失败原因
        //总包数:语音报分包总数
        //包序号:包序号,比如是第几个包
        //接收状态:0接收成功，1接收失败
        //失败原因：成功不填写（长度为0），失败填写原因
        String message = mTotal + "@" + mNum + "@0@";
        NettyHelper.getInstance().send(new UUpdateVersion(message,mTradeNum));
        Log.i(TAG, "upgradeSuccess: ");
    }
}
