package com.ascend.wangfeng.locationbyhand.data;

import android.util.Log;

import com.ascend.wangfeng.locationbyhand.MyApplication;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by zsw on 2018/5/7.
 */

public class FTPClientData {

    private String TAG = getClass().getCanonicalName();

    FTPClient ftpClient = null;
    FileInputStream fis = null;

    private String url;                     //服务器地址

    private int port;                       //端口号

    private String user;                    //用户

    private String password;                //密码

    private String path;                    //服务器磁盘路径

    public FTPClientData() {
        this.url = MyApplication.UpLoadFtpUrl;
        this.port = MyApplication.UpLoadFtpPort;
        this.user = MyApplication.UpLoadFtpUser;
        this.password = MyApplication.UpLoadFtpPass;
        this.path = MyApplication.UpLoadFilePath;
    }

    public FTPClientData(String url, int port, String user, String password, String path) {
        this.url = url;
        this.port = port;
        this.user = user;
        this.password = password;
        this.path = path;
    }

    /**
     * 连接Ftp
     *
     * @return
     */
    public FTPClient ftpConnect() {
        ftpClient = new FTPClient();
        try {
            ftpClient.setConnectTimeout(5000); // 一秒钟，如果超过就判定超时了
            ftpClient.connect(url, port);
            boolean loginResult = ftpClient.login(user, password);
            int returnCode = ftpClient.getReplyCode();
            if (loginResult && FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功

                return ftpClient;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 上传到FTP
     *
     * @param ftpClient
     * @param filePath
     * @param fileName
     * @return
     */
    public boolean ftpUpload(FTPClient ftpClient, String filePath, String fileName) {
        if (ftpClient == null) {
            return false;
        }
        try {
            // 设置存储路径
            ftpClient.changeWorkingDirectory("/");//每次创建文件都返回根目录
            ftpClient.makeDirectory(path);
            ftpClient.changeWorkingDirectory(path);
//            ftpClient.changeWorkingDirectory(path);                     //跳转到FTP的执行文件中
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.enterLocalPassiveMode();
            fis = new FileInputStream(filePath + fileName);
            ftpClient.storeFile(fileName, fis);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "错误原因：-->:" + e.toString());
        }
        return false;
    }

}
