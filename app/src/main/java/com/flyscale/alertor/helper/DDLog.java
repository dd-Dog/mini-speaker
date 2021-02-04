package com.flyscale.alertor.helper;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by bian on 2018/12/10.
 */

public class DDLog {

    private static boolean APP_DBG = true; // 是否是debug模式
    private static boolean MAKE_LOG_FILE = false;

    private static final String SDCARD_PATH;
    private static final String RELATIVE_PATH = "/flyscale/";
    private static final String LOG_FILE = "core.log";
    private static final String LOG_PATH;   //LOG路径
    private static final String LOG_CACHE;  //log的cachey文件路径
    private static final long LOG_LINE_LENGTH = 1024 * 5; //log一行的限制大小
    private static final long LOG_CACHE_SIZE = LOG_LINE_LENGTH * 200; //log的缓冲大小(超出限制后删除大小)
    private static final long LOG_FILE_SIZE = 1024 * 1024 * 20 + LOG_LINE_LENGTH; //log文件大小

    private static int mStatckDeep = 2; //打印log方法的栈深度

    private static CopyOnWriteArrayList<String> mLogCacheList = new CopyOnWriteArrayList<>();

    static {
        SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(SDCARD_PATH);
        stringBuilder.append(RELATIVE_PATH);
        stringBuilder.append(LOG_FILE);
        LOG_PATH = stringBuilder.toString();
        LOG_CACHE = LOG_PATH + ".cache";

        File relativeDir = new File(SDCARD_PATH + RELATIVE_PATH);
        if (!relativeDir.exists() || !relativeDir.isDirectory()) {
            relativeDir.mkdirs();
        }

        /*Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mLogCacheList != null && mLogCacheList.size() > 0) {
                    if (checkAvailableStorageSize()) {
                        writeCacheToFile();
                    }
                }
            }
        }, 500, 1000);*/
    }


    private static boolean checkAvailableStorageSize() {
        if (getAvailableSize() < LOG_LINE_LENGTH) {
            Log.e(DDLog.class.getSimpleName(), "There is no space for log on sdcard!");
            return false;
        }
        return true;
    }

    private static long getAvailableSize() {
        String externalStorageState = Environment.getExternalStorageState();
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long blockSizeLong = statFs.getBlockSizeLong();
            long availableBlocksLong = statFs.getAvailableBlocksLong();
            return blockSizeLong * availableBlocksLong;
        } else {
            Log.e(DDLog.class.getSimpleName(), "Sdcard no found!");
            return 0;
        }
    }

    private static void writeCacheToFile() {
        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e(DDLog.class.getSimpleName(), "current thread=" + Thread.currentThread().getName());
                    File relativeDir = new File(SDCARD_PATH + RELATIVE_PATH);
                    if (!relativeDir.exists() || !relativeDir.isDirectory()) {
                        relativeDir.mkdirs();
                    }

                    //log文件
                    File logFile = new File(LOG_PATH);
                    if (!logFile.exists()) {
                        logFile.createNewFile();
                    }
                    //如果Log文件超过限制，删除旧Log,添加新log
                    if (logFile.length() >= LOG_FILE_SIZE) {
                        Log.e(DDLog.class.getName(), "size is out of max length,remove old log!");
                        if (getAvailableSize() < 2 * logFile.length()) {
                            Log.e(DDLog.class.getName(), "No enough space for copy! log printer was terminate!!!");
                            return;
                        }
                        //打开log文件输入流
                        FileInputStream fis = new FileInputStream(logFile);
                        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                        BufferedReader br = new BufferedReader(isr);

                        //打开cache文件输出流
                        File logCache = new File(LOG_CACHE);
                        if (logCache.exists()) {
                            logCache.delete();//删除原来的cache文件
                        }
                        logCache.createNewFile();
                        FileOutputStream fos = new FileOutputStream(logCache, true);
                        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                        BufferedWriter bw = new BufferedWriter(osw);

                        int deleteLength = 0;
                        String readLine = br.readLine();
                        if (readLine != null) {
                            deleteLength += readLine.length();
                        }
                        while (readLine != null) {
                            if (deleteLength >= LOG_CACHE_SIZE) {
                                //到达要删除的位置,开始写入缓冲文件
                                bw.write(readLine + "\r\n");
                                readLine = br.readLine();
                                continue;
                            }
                            readLine = br.readLine();
                            deleteLength += readLine.length();
                        }
                        //写入Log
                        for (String s : mLogCacheList) {
                            bw.write(s + "\r\n");
                        }
                        //清空缓存
                        mLogCacheList.clear();

                        //删除原文件
                        logFile.delete();
                        //重命名cahe文件
                        logCache.renameTo(logFile);

                        br.close();
                        isr.close();
                        fis.close();
                        bw.close();
                        osw.close();
                        fos.close();
                    } else {
                        //打开log文件输出流
                        FileOutputStream fis = new FileOutputStream(logFile, true);
                        OutputStreamWriter osw = new OutputStreamWriter(fis, "UTF-8");
                        BufferedWriter bw = new BufferedWriter(osw);
                        for (String s : mLogCacheList) {
                            bw.write(s + "\r\n");
                        }
                        //清空缓存
                        mLogCacheList.clear();
                        bw.close();
                        osw.close();
                        fis.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("写入文件失败！");
                }
            }
        });

    }

    /**
     * 日志输出到缓存
     *
     * @param text
     */
    private static void writeLineToCache(String text) {
        if (text.length() > LOG_LINE_LENGTH) {
            text = "line is out of size " + text.length() + "," + LOG_LINE_LENGTH +
                    " is allowed!";
            Log.e(DDLog.class.getName(), text);
        }
        mLogCacheList.add(text);
    }

    /**
     * 注意mStatckDeep不是固定的，根据方法调用深度决定
     *
     * @return
     */
    private static String getTraceInfo() {
        StringBuffer logMsg = new StringBuffer();
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        logMsg.append(" ");
        logMsg.append(stackTrace[mStatckDeep].getFileName());
        logMsg.append("/");
        logMsg.append(stackTrace[mStatckDeep].getMethodName());
        logMsg.append("(");
        StringBuilder lineNUm = new StringBuilder();
        lineNUm.append(stackTrace[mStatckDeep].getLineNumber());
        lineNUm.append(")");
        logMsg.append(lineNUm.toString());
        return logMsg.toString();
    }


    private static String getLog(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(getTime());
        sb.append(getTraceInfo());
        sb.append("]");
        sb.append(" : " + msg);
        return sb.toString();
    }

    @Deprecated
    private static String getLog(String tag, String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(getTime());
        sb.append("]");
        sb.append(" " + android.os.Process.myPid()+ " ");
        sb.append(" " + tag);
        sb.append("   : " + msg);
        return sb.toString();
    }

    public static void resetlog() {
        File logFile = new File(LOG_PATH);
        if (logFile.exists()) {
            logFile.delete();
        }
    }


    public static String getTime() {
        long timeMillis = System.currentTimeMillis();
        return new SimpleDateFormat("MM-dd HH:mm:ss.SSS").format(new Date(timeMillis));
    }

    public static void i(String msg) {
        String traceInfo = null;
        if (APP_DBG) {
            traceInfo = getTraceInfo();
            Log.i(traceInfo, msg);
        }

        if (MAKE_LOG_FILE) {
            if (traceInfo == null) {
                traceInfo = getTraceInfo();
            }
            writeLineToCache(getLog(traceInfo, msg));
        }
    }


    public static void d(String msg) {
        String traceInfo = null;
        if (APP_DBG) {
            traceInfo = getTraceInfo();
            Log.d(traceInfo, msg);
        }

        if (MAKE_LOG_FILE) {
            if (traceInfo == null) {
                traceInfo = getTraceInfo();
            }
            writeLineToCache(getLog(traceInfo, msg));
        }
    }


    public static void w(String msg) {
        String traceInfo = null;
        if (APP_DBG) {
            traceInfo = getTraceInfo();
            Log.w(traceInfo, msg);
        }

        if (MAKE_LOG_FILE) {
            if (traceInfo == null) {
                traceInfo = getTraceInfo();
            }
            writeLineToCache(getLog(traceInfo, msg));
        }
    }


    public static void e(String msg) {
        String traceInfo = null;
        if (APP_DBG) {
            traceInfo = getTraceInfo();
            Log.e(traceInfo, msg);
        }

        if (MAKE_LOG_FILE) {
            if (traceInfo == null) {
                traceInfo = getTraceInfo();
            }
            writeLineToCache(getLog(traceInfo, msg));
        }
    }

    @Deprecated
    public static void i(Class c, String msg) {
        String traceInfo = null;
        if (APP_DBG) {
            traceInfo = getTraceInfo();
            Log.i(traceInfo, msg);
        }

        if (MAKE_LOG_FILE) {
            if (traceInfo == null) {
                traceInfo = getTraceInfo();
            }
            writeLineToCache(getLog(traceInfo, msg));
        }
    }

    @Deprecated
    public static void d(Class c, String msg) {
        String traceInfo = null;
        if (APP_DBG) {
            traceInfo = getTraceInfo();
            Log.d(traceInfo, msg);
        }

        if (MAKE_LOG_FILE) {
            if (traceInfo == null) {
                traceInfo = getTraceInfo();
            }
            writeLineToCache(getLog(traceInfo, msg));
        }
    }

    @Deprecated
    public static void w(Class c, String msg) {
        String traceInfo = null;
        if (APP_DBG) {
            traceInfo = getTraceInfo();
            Log.w(traceInfo, msg);
        }

        if (MAKE_LOG_FILE) {
            if (traceInfo == null) {
                traceInfo = getTraceInfo();
            }
            writeLineToCache(getLog(traceInfo, msg));
        }
    }

    @Deprecated
    public static void e(Class c, String msg) {
        String traceInfo = null;
        if (APP_DBG) {
            traceInfo = getTraceInfo();
            Log.e(traceInfo, msg);
        }

        if (MAKE_LOG_FILE) {
            if (traceInfo == null) {
                traceInfo = getTraceInfo();
            }
            writeLineToCache(getLog(traceInfo, msg));
        }
    }


    public static String printArrayHex(long[] array) {
        if (array == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]).append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String printArrayHex(int[] array) {
        if (array == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]).append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String printArrayHex(String[] array) {
        if (array == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]).append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String printArray(byte[] array) {
        if (array == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]).append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String printArrayHex(byte[] array) {
        if (array == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(Integer.toHexString(byteToInteger(array[i]))).append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static Integer byteToInteger(Byte b) {
        return 0xff & b;
    }

    public static String printArrayHex(char[] array) {
        if (array == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]).append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static String printArrayHex(Object[] array) {
        if (array == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i].toString()).append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static <T> String printArrayHex(List<T> array) {
        if (array == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.size(); i++) {
            sb.append("\n").append(array.get(i).toString());
        }
        sb.append("]");
        return sb.toString();
    }
}