package com.flyscale.alertor.helper;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HttpDownloadHelper {
    private static final String TAG = HttpDownloadHelper.class.getName();

    public static void downloadFile(String url, String parentPath, String filename, DownloadListener listener){
        DownloadTask task = new DownloadTask.Builder(url, parentPath, filename)
                .setFilename(filename)
                .setMinIntervalMillisCallbackProcess(30) // 下载进度回调的间隔时间（毫秒）
                .setPassIfAlreadyCompleted(false)// 任务过去已完成是否要重新下载
                .setPriority(10)
                .build();
        task.enqueue(listener);//异步执行任务
//        task.cancel();// 取消任务
//        task.execute(listener);// 同步执行任务
//        DownloadTask.enqueue({task}, listener); //同时异步执行多个任务
    }
/*

    public static void downloadFile(final String downloadUrl, final String localPath, final String localFilename) {
        DDLog.i("downloadFile downloadUrl=" + downloadUrl);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        DDLog.i("1111111111111");
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                DDLog.i("下载文件失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                File dir = new File(localPath);
                if (!dir.exists() || !dir.isDirectory()) {
                    boolean mkdirs = dir.mkdirs();
                    if (!mkdirs) {
                        DDLog.e("下载时创建本地文件夹失败！");
                        return;
                    }
                }
                try {
                    assert response.body() != null;
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File downloadFile = new File(localPath, localFilename);
                    fos = new FileOutputStream(downloadFile);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        DDLog.i("下载进度：" + sum + "/" + total);
                    }
                    fos.flush();
                    DDLog.i("download success,fileSize=" + downloadFile.length() / 1024 + "KB");
                    DDLog.i("localPath=" + localPath);
                } catch (Exception e) {
                    e.printStackTrace();
                    DDLog.e("download failed : " + e.getMessage());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
*/

}
