package com.ascend.wangfeng.locationbyhand.data;

import android.content.Context;
import android.util.Log;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.bean.LoadError;
import com.ascend.wangfeng.locationbyhand.util.LogUtils;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtil;
import com.ascend.wangfeng.locationbyhand.view.activity.SetftpActivity;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private Context context;

    public FTPClientData(Context context) {
        this.context = context;
        this.url = MyApplication.UpLoadFtpUrl;
        this.port = MyApplication.UpLoadFtpPort;
        this.user = MyApplication.UpLoadFtpUser;
        this.password = MyApplication.UpLoadFtpPass;
        this.path = MyApplication.UpLoadFilePath;
    }

    public FTPClientData(Context context, String url, int port, String user, String password, String path) {
        this.context = context;
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
     * @param loadError true 上传失败再次上传的数据，false 第一次上传的数据，上传失败则缓存上传失败数据
     * @param isDelect  true ,提交成功后 则删除文件 false ,提交成功后，不删除文件
     * @return
     */
    public boolean ftpUpload(FTPClient ftpClient, String filePath, String fileName, boolean loadError, boolean isDelect) {
        if (ftpClient == null) {
            if (!loadError)
                saveLoadError(filePath, fileName);
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
            if (isDelect)
                delectLocalData(filePath, fileName);
            if (loadError) {
                //清除上传错误缓存数据
                List<LoadError> loadErrors = SharedPreferencesUtil.getList(context, "loadError");
                if (loadErrors!=null){
                    LoadError loadError1 = new LoadError(filePath, fileName);
                    loadErrors.remove(loadError1);
                    SharedPreferencesUtil.putList(context, "loadError", loadErrors);
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            if (!loadError)
                saveLoadError(filePath, fileName);
            //找不到文件则从错误集合中删除
            if (e.toString().contains("java.io.FileNotFoundException")) {
                if (loadError) {
                    //清除上传错误缓存数据
                    List<LoadError> loadErrors = SharedPreferencesUtil.getList(context, "loadError");
                    LoadError loadError1 = new LoadError(filePath, fileName);
                    loadErrors.remove(loadError1);
                    SharedPreferencesUtil.putList(context, "loadError", loadErrors);
                }
            }
            Log.e(TAG, "错误原因：-->:" + e.toString());
            return false;
        }
    }

    private void delectLocalData(String filePath, String fileName) {
        // 上传成功后， 删除手机上的文件
        File localFile = new File(filePath + fileName);
        LogUtils.e("delectfile", filePath + fileName);
        if (localFile.exists()) {
            localFile.delete();
        }
    }

    private void saveLoadError(String filePath, String fileName) {
        List<LoadError> loadError = SharedPreferencesUtil.getList(context, "loadError");
        if (loadError == null) loadError = new ArrayList<>();
        loadError.add(new LoadError(filePath, fileName));
        SharedPreferencesUtil.putList(context, "loadError", loadError);
    }
}
