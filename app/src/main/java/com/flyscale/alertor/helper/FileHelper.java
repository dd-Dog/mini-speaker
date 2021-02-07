package com.flyscale.alertor.helper;

import android.annotation.SuppressLint;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.text.DecimalFormat;

/**
 * @author 高鹤泉
 * @TIME 2020/6/16 9:10
 * @DESCRIPTION 暂无
 */
public class FileHelper {

    /**
     * 接警语音文件名
     */
    public static final String S_ALARM_RESP_NAME = "alarmResp.amr";
    public static final String S_CLIENT_CRT_NAME = "alarmClient.crt";
    public static final String S_CLIENT_KEY_NAME = "alarmPk8Client.key";
    public static final String S_ROOT_CRT_NAME = "rootCa.crt";
    public static final File S_ALARM_RESP_FILE = new File(getBasePath() + S_ALARM_RESP_NAME);

    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

    /**
     * 判断内存卡是否存在
     *
     * @return
     */
    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 这里只是确定文件的目录路径
     *
     * @return
     */
    public static String getBasePath() {
        String basePath = null;
        if (hasSdcard()) {
            basePath = Environment.getExternalStorageDirectory().getPath() + getFileParent();
        } else {
            basePath = getFileParent();
        }
        return basePath;
    }

    /**
     * 获取创建的文件夹名
     *
     * @return
     */
    private static String getFileParent() {
        return "/alarm/";
    }


    /**
     * byte转文件
     *
     * @param data
     * @param fileName
     * @return
     */
    public static File byteToFile(byte[] data, String fileName) {
        File file = new File(getBasePath() + fileName);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data, 0, data.length);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return file;
        }
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            DDLog.e(FileHelper.class, "获取文件大小 , 文件不存在");
        }
        return size;
    }

    /**
     * 获取指定文件夹大小
     *
     * @param f
     * @return
     * @throws
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static String FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeLong = "";
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = new DecimalFormat("#").format((double) fileS);
                break;
            case SIZETYPE_KB:
                fileSizeLong = df.format((double) fileS / 1024);
                break;
            case SIZETYPE_MB:
                fileSizeLong = df.format((double) fileS / 1048576);
                break;
            case SIZETYPE_GB:
                fileSizeLong = df.format((double) fileS / 1073741824);
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static String getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            DDLog.e(FileHelper.class, "获取文件大小 , 获取失败");
        }
        return FormetFileSize(blockSize, sizeType);
    }


    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     */
    public static boolean fileIsExists(String filePath) {
        try {
            File f = new File(filePath);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件路径
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }

        return false;
    }

    /**
     * 从配置文件中读取KI值
     * @return
     */
    public static String getKI() {
        @SuppressLint("SdCardPath")
        String filePath = "/mnt/sdcard/flyscale/config/KI.txt";
        File file = new File(filePath);
        if (file.exists()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                return br.readLine();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
            DDLog.i("KI配置文件不存在！");
        }
        return "";
    }
}
