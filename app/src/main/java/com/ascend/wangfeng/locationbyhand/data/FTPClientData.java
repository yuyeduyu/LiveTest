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

    public FTPClientData(){
        this.url = MyApplication.UpLoadFtpUrl;
        this.port = MyApplication.UpLoadFtpPort;
        this.user = MyApplication.UpLoadFtpUser;
        this.password = MyApplication.UpLoadFtpPass;
        this.path = MyApplication.UpLoadFilePath;
    }

    public FTPClientData(String url, int port, String user, String password, String path){
        this.url = url;
        this.port = port;
        this.user = user;
        this.password = password;
        this.path = path;
    }

    /**
     * 连接Ftp
     * @return
     */
    public FTPClient ftpConnect(){
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
     * @param ftpClient
     * @param filePath
     * @param fileName
     * @param type
     * @return
     */
    public boolean ftpUpload(FTPClient ftpClient, String filePath, String fileName, String type){
        if (ftpClient==null){
            return false;
        }
        try {
            ftpClient.changeWorkingDirectory(path);                     //跳转到FTP的执行文件中
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.enterLocalPassiveMode();
            fis = new FileInputStream(filePath + fileName);
            if (type.equals("apl")) {
                String remoteFileName = fileName + ".carapl";
                ftpClient.storeFile(remoteFileName, fis);
            }else if (type.equals("log")){
                String remoteFileName = fileName + ".carmac";
                ftpClient.storeFile(remoteFileName, fis);
            }else if (type.equals("net")){
                String remoteFileName = fileName + ".carnet";
                ftpClient.storeFile(remoteFileName, fis);
            }else if (type.equals("gps")){
                String remoteFileName = fileName + ".cargps";
                ftpClient.storeFile(remoteFileName, fis);
            }else{
                String remoteFileName = fileName ;

                ftpClient.storeFile(remoteFileName, fis);
            }

            // 上传成功后， 删除手机上的文件
            File localFile = new File(filePath + fileName);

            if (localFile.exists()) {
                localFile.delete();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "错误原因：-->:" + e.toString());
        }
        return false;
    }

}
