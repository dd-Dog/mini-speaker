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

    /**
     * 下载文件
     * @param url 下载文件地址
     * @param parentPath 本地保存文件的目录
     * @param filename 本地保存文件名
     * @param listener 下载监听器
     */
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

}
