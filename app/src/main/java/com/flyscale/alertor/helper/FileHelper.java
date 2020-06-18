package com.flyscale.alertor.helper;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
    public static final File S_ALARM_RESP_FILE = new File(getBasePath() + S_ALARM_RESP_NAME);

    /**
     * 判断内存卡是否存在
     * @return
     */
    public static boolean hasSdcard(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 这里只是确定文件的目录路径
     * @return
     */
    public static String getBasePath(){
        String basePath = null;
        if(hasSdcard()){
            basePath = Environment.getExternalStorageDirectory().getPath() + getFileParent();
        }else{
            basePath = getFileParent();
        }
        return basePath;
    }

    /**
     * 获取创建的文件夹名
     * @return
     */
    private static String getFileParent(){
        return "/alarm/";
    }


    /**
     * byte转文件
     * @param data
     * @param fileName
     * @return
     */
    public static File byteToFile(byte[] data,String fileName){
        File file = new File(getBasePath() + fileName);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data,0,data.length);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return file;
        }
    }


}
