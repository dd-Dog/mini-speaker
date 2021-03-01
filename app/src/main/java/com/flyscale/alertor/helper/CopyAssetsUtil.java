package com.flyscale.alertor.helper;

import android.content.Context;
import android.util.Log;

import com.flyscale.alertor.base.BaseApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liChang on 2021/2/26
 */
public class CopyAssetsUtil {
    private static final String MEDIA_PATH = "/mnt/sdcard/flyscale/media/";
    private static final String EMR_MEDIA_PATH = "/mnt/sdcard/flyscale/media/";
    private static final String COMMON_PATH = "/mnt/sdcard/flyscale/";

    public static void initFile() {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                copyAssetsDirPhone(BaseApplication.sContext, "normal", MEDIA_PATH);
                copyAssetsDirPhone(BaseApplication.sContext, "emr", EMR_MEDIA_PATH);
                copyAssetsDirPhone(BaseApplication.sContext, "common", COMMON_PATH);
            }
        });

    }

    /**
     * 从assets目录中复制整个文件夹内容,考贝到 /data/data/包名/files/目录中
     *
     * @param context  上下文
     * @param filePath assets中的路径
     */
    public static void copyAssetsDirPhone(Context context, String filePath, String path) {
        try {
//            String MEDIA_PATH = "/mnt/sdcard/flyscale/media/";
            String[] fileList = context.getAssets().list(filePath);
            if (fileList.length > 0) {//如果是目录
                File file = new File(path + filePath);
                Log.i("TAG", "copyAssetsDirPhone: " + file);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileList) {
                    filePath = filePath + File.separator + fileName;
                    Log.i("TAG", "copyAssetsDirPhone: 111" + fileName);
                    copyAssetsDirPhone(context, filePath, path);
                    filePath = filePath.substring(0, filePath.lastIndexOf(File.separator));
                    Log.e("oldPath", filePath);
                }
            } else {//如果是文件
                InputStream inputStream = context.getAssets().open(filePath);
                File file = new File(path + File.separator + filePath);
                Log.i("TAG", "copyAssetsDirPhone: 222" + file);
                if (!file.exists() || file.length() == 0) {
                    FileOutputStream fos = new FileOutputStream(file);
                    int len = -1;
                    byte[] buffer = new byte[1024];
                    while ((len = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.flush();
                    inputStream.close();
                    fos.close();
                    Log.i("TAG", "copyAssetsDirPhone: 完成");
                } else {
                    Log.i("TAG", "copyAssetsDirPhone: 不用");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将文件从assets目录，考贝到 /data/data/包名/files/ 目录中。
     * assets 目录中的文件，会不经压缩打包至APK包中，使用时还应从apk包中导出来
     *
     * @param fileName 文件名,如aaa.txt
     */
    public static void copyAssetsFilePhone(Context context, String fileName) {
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            //getFilesDir() 获得当前APP的安装路径 /data/data/包名/files 目录
            File file = new File(context.getFilesDir().getAbsolutePath() + File.separator + fileName);
            if (!file.exists() || file.length() == 0) {
                FileOutputStream fos = new FileOutputStream(file);//如果文件不存在，FileOutputStream会自动创建文件
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();//刷新缓存区
                inputStream.close();
                fos.close();
                Log.i("TAG", "copyAssetsFilePhone: 完成");
            } else {
                Log.i("TAG", "copyAssetsFilePhone: 不用");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
