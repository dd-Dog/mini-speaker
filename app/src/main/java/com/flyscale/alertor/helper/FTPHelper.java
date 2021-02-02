package com.flyscale.alertor.helper;

import com.flyscale.alertor.data.persist.PersistConfig;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

public class FTPHelper {

    private static Session session = null;
    private static ChannelSftp channel = null;

    /**
     * 下载文件
     *
     * @param remoteFile FTP服务器文件相对路径
     * @param remotePath FTP服务器文件名称
     * @param localFile  本地文件全路径
     * @throws Exception
     */
    public static void downloadFile(String remoteFile, String remotePath, String localFile) throws Exception {
        DDLog.i("sftp download File remotePath :" + remotePath + File.separator + remoteFile + " to localPath : " + localFile + " !");
        OutputStream output = null;
        File file = null;
        try {
            file = new File(localFile);
            if (!checkFileExist(localFile)) {
                file.createNewFile();
            }
            output = new FileOutputStream(file);
            channel.cd(remotePath);
            channel.get(remoteFile, output);
            DDLog.i("下载成功！");
        } catch (Exception e) {
            DDLog.e("Download file error " + e);
            throw new Exception("Download file error.");
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    throw new Exception("Close stream error.");
                }
            }

        }
    }

    public static ChannelSftp getConnect() {
        String ftpHost = PersistConfig.findConfig().getFtpHostIPDebugFly();
        String port = PersistConfig.findConfig().getFtpHostPortDebugFly() + "";
        String ftpUserName = PersistConfig.findConfig().getFtpUsernameFly();
        String ftpPassword = PersistConfig.findConfig().getFtpPasswordFly();

        return getConnectInternal(ftpHost, port, ftpUserName, ftpPassword);
    }

    public static ChannelSftp getConnectInternal(String ftpHost, String port, String ftpUserName, String ftpPassword) {
        //默认的端口22 此处我是定义到常量类中；
        int ftpPort = 22;
        //判断端口号是否为空，如果不为空，则赋值
        if (port != null && !port.equals("")) {
            ftpPort = Integer.valueOf(port);
        }
        JSch jsch = new JSch(); // 创建JSch对象
        // 按照用户名,主机ip,端口获取一个Session对象
        try {
            DDLog.i("sftp [ ftpHost = " + ftpHost + "  ftpPort = " + ftpPort + "  ftpUserName = " + ftpUserName + "  ftpPassword = " + ftpPassword + " ]");
            session = jsch.getSession(ftpUserName, ftpHost, ftpPort);
            DDLog.i("Session created.");
            if (ftpPassword != null) {
                session.setPassword(ftpPassword); // 设置密码
            }
            String ftpTO = "30000";
            if (!(ftpTO == null || "".equals(ftpTO))) {
                int ftpTimeout = Integer.parseInt(ftpTO);
                session.setTimeout(ftpTimeout); // 设置timeout时候
            }
            //并且一旦计算机的密匙发生了变化，就拒绝连接。
            session.setConfig("StrictHostKeyChecking", "no");
            //默认值是 “yes” 此处是由于我们SFTP服务器的DNS解析有问题，则把UseDNS设置为“no”
            session.setConfig("UseDNS", "no");
            session.connect(); // 经由过程Session建树链接
            DDLog.i("Session connected.");
            DDLog.i("Opening SFTP Channel.");
            channel = (ChannelSftp) session.openChannel("sftp"); // 打开SFTP通道
            channel.connect(); // 建树SFTP通道的连接
            DDLog.i("Connected successfully to ftpHost = " + ftpHost + ",as ftpUserName = "
                    + ftpUserName + ", returning: " + channel);
        } catch (JSchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            DDLog.e("sftp getConnect error : " + e);
        }
        return channel;
    }

    public static void closeChannel() throws Exception {
        try {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        } catch (Exception e) {
            DDLog.e("close sftp error：" + e);
            throw new Exception("close ftp error.");
        }
    }


    /**
     * 上传文件
     *
     * @param localFile
     * @param newName
     * @param remoteFoldPath
     * @throws Exception
     */
    public static void uploadFile(String localFile, String newName, String remoteFoldPath) throws Exception {
        InputStream input = null;
        try {
            input = new FileInputStream(new File(localFile));
            // 改变当前路径到指定路径
            channel.cd(remoteFoldPath);
            channel.put(input, newName);
        } catch (Exception e) {
            DDLog.e("Upload file error " + e);
            throw new Exception("Upload file error.");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    throw new Exception("Close stream error.");
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    public static Vector listFiles(String remotePath) throws Exception {
        Vector vector = null;
        try {
            vector = channel.ls(remotePath);
        } catch (SftpException e) {
            DDLog.e("List file error " + e);
            throw new Exception("list file error.");
        }
        return vector;
    }


    private static boolean checkFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

}
