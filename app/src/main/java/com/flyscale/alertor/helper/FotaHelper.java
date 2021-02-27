package com.flyscale.alertor.helper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.abupdate.fota_demo_iot.ICheckVersionAidlInter;
import com.abupdate.fota_demo_iot.IDownloadAidlInter;
import com.abupdate.fota_demo_iot.IOtaInter;
import com.abupdate.fota_demo_iot.IUpdateAidlInter;
import com.abupdate.fota_demo_iot.data.remote.NewVersionInfo;
import com.flyscale.alertor.FotaAction;

import org.jetbrains.annotations.NotNull;

public class FotaHelper {
    private static final String TAG = "FotaHelper";
    private Context mContext;
    private IOtaInter mIOtaInter;
    private FotaHelperCallback mCallback;

    public FotaHelper(Context context, @NotNull FotaHelperCallback fotaHelperCallback) {
        this.mContext = context;
        mCallback = fotaHelperCallback;
        bindService();
    }

    /**
     * FOTA远程服务的连接
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIOtaInter = IOtaInter.Stub.asInterface(service);
            if (mCallback !=null){
                mCallback.onRemoteServiceConnected();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIOtaInter = null;
            if (mCallback !=null){
                mCallback.onRemoteServiceDisconnected();
            }
        }
    };

    /**
     * 绑定FOTA服务
     */
    private void bindService() {
        Intent aidlIntent = new Intent();
        //绑定服务端的service
        aidlIntent.setAction("com.abupdate.fota_demo_iot.service.OtaAidlService");
        //新版本（5.0后） 必须显式intent启动 绑定服务
        aidlIntent.setComponent(new ComponentName("com.abupdate.fota_demo_iot", "com.abupdate.fota_demo_iot.service.OtaAidlService"));
        //绑定的时候服务端自动创建
//        mContext.bindService(aidlIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解绑FOTA服务
     */
    private void unbindAidlService() {
        mContext.unbindService(mConnection);
        mConnection.onServiceDisconnected(null);
    }

    private void getOtaStatus() {
        try {
            mIOtaInter.getOtaStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查版本
     * 版本信息包含：
     * ①.源版本名称
     * ②.目标版本名称
     * ③.版本别名
     * ④.升级包 md5值
     * ⑤.下载的差分包的 url
     * ⑥.差分关系对应的 id
     * ⑦.版本发布日期
     * ⑧.版本更新内容
     * ⑨.升级包文件大小
     */
    public void checkVersion() {
        try {
            mIOtaInter.checkVersion(new ICheckVersionAidlInter.Stub() {
                @Override
                public void hasNewVersion(final NewVersionInfo newVersionInfo) throws RemoteException {
                    //发现新版本
                    if (mCallback !=null){
                        mCallback.hasNewVersion(newVersionInfo);
                    }
                    Log.i(TAG, "hasNewVersion: " + newVersionInfo.toString());

                    //检测到新版本，开始下载
                    startDownload();
                }

                @Override
                public void noNewVersion(final int code) throws RemoteException {
                    //没有新版本
                    if (mCallback !=null){
                        mCallback.noNewVersion(code);
                    }
                    Log.i(TAG, "noNewVersion: " + code);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始下载
     */
    private void startDownload() {
        try {
            mIOtaInter.download(new IDownloadAidlInter.Stub() {
                @Override
                public void onStart() throws RemoteException {
                    if (mCallback !=null){
                        mCallback.onDownloadStart();
                    }
                }

                @Override
                public void onProgress(int progress) throws RemoteException {
                    if (mCallback !=null){
                        mCallback.onDownloadProgress(progress);
                    }
                }

                @Override
                public void onFail(int code) throws RemoteException {
                    if (mCallback !=null){
                        mCallback.onDownloadFail(code);
                    }
                }

                @Override
                public void onCancel() throws RemoteException {
                    if (mCallback !=null){
                        mCallback.onDownloadonCancel();
                    }
                }

                @Override
                public void onSuccess() throws RemoteException {
                    if (mCallback !=null){
                        mCallback.onDownloadSuccess();
                    }
                    //下载成功，开始升级
                    update();
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载暂停
     */
    private void stopDownload() {
        try {
            mIOtaInter.pauseDownload();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 升级
     */
    private void update() {
        try {
            mIOtaInter.install(new IUpdateAidlInter.Stub() {
                @Override
                public void enterRecoveryFail(final int code) throws RemoteException {
                    //进入 recovery 失败返回状态码， 可根据Error.getErrorMessage(code) 获取错误信息
                    if (mCallback !=null){
                        mCallback.enterRecoveryFail(code);
                    }
                }

                @Override
                public void upgradeProgress(int progress) throws RemoteException {
                    if (mCallback !=null){
                        mCallback.upgradeProgress(progress);
                    }
                }

                @Override
                public void upgradeSuccess() throws RemoteException {
                    if (mCallback !=null){
                        mCallback.upgradeSuccess();
                    }
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public interface FotaHelperCallback {
        /**
         * fota远程服务连接成功
         */
        void onRemoteServiceConnected();
        /**
         * fota远程服务连接断开
         */
        void onRemoteServiceDisconnected();

        /**
         * 检测到新版本
         * @param newVersionInfo
         */
        void hasNewVersion(NewVersionInfo newVersionInfo);

        /**
         * 没有新版本
         * @param code
         */
        void noNewVersion(final int code);

        /**
         * 开始下载升级包
         */
        void onDownloadStart();

        /**
         * 正在下载升级包
         * @param progress
         */
        void onDownloadProgress(int progress);

        /**
         * 升级包下载失败
         * @param code
         */
        void onDownloadFail(int code);

        /**
         * 升级下载被取消
         */
        void onDownloadonCancel();

        /**
         * 升级包下载成功
         */
        void onDownloadSuccess();

        /**
         * 暂停下载升级包
         */
        void onDownloadPause();

        /**
         * 升级时进入recovery失败
         * @param code
         */
        void enterRecoveryFail(final int code);

        /**
         * 正在升级
         * @param progress
         */
        void upgradeProgress(int progress);

        /**
         * 升级成功
         */
        void upgradeSuccess();
    }

    public abstract static class FotaHelperCallbackAdapter implements FotaHelperCallback{

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

        }

        @Override
        public void onDownloadStart() {

        }

        @Override
        public void onDownloadProgress(int progress) {

        }

        @Override
        public void onDownloadFail(int code) {

        }

        @Override
        public void onDownloadonCancel() {

        }

        @Override
        public void onDownloadSuccess() {

        }

        @Override
        public void onDownloadPause() {

        }

        @Override
        public void enterRecoveryFail(int code) {

        }

        @Override
        public void upgradeProgress(int progress) {

        }

        @Override
        public void upgradeSuccess() {

        }
    }
}
