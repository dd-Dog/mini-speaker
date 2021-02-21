package com.flyscale.alertor.helper;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtil {
    public static final int FILE_NAME = 12;
    public static final int FILE_SIZE = 10;


    /**
     * 获取文件路径
     *
     * */
    public static String getFilePath(String data){
        String filePath = getBasePath()+getFileName(data);
        return filePath;
    }

    /**
     * 根据data数据拆解出文件名
     *
     * */
    public static String getFileName(String data){
        String fileName = data.substring(0,FILE_NAME);
        return  fileName;
    }



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
     * 获取指定文件大小
     *
     * */
    public static long getFileSize(String path){
        File f = new File(path);
        long fileSize = 0;
        if(f.exists()){
            fileSize = f.length();
        }else{
            DDLog.e(FileUtil.class , "获取文件大小 , 文件不存在");
        }
        return fileSize;
    }

    /**
     * 获取指定文件大小
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
            DDLog.e(FileUtil.class , "获取文件大小 , 文件不存在");
        }
        return size;
    }


    /**
     * 获取指定文件夹大小
     * @param f
     * @return
     * @throws
     */
    private static long getFileSizes(File f) throws Exception
    {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++){
            if (flist[i].isDirectory()){
                size = size + getFileSizes(flist[i]);
            }
            else{
                size =size + getFileSize(flist[i]);
            }
        }
        return size;
    }


    /**
     * 判断文件是否存在
     * @param filePath 文件路径
     */
    public static boolean fileIsExists(String filePath) {
        try {
            File f=new File(filePath);
            if(!f.exists()) {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 删除单个文件
     * @param   filePath    被删除文件路径
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
     * 复制单个文件
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ( (byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        }
        catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }
}
