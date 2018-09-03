package com.ascend.wangfeng.locationbyhand.view.service;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.data.FTPClientData;
import com.ascend.wangfeng.locationbyhand.data.FileData;
import com.ascend.wangfeng.locationbyhand.data.saveData.AllUpLoadData;
import com.ascend.wangfeng.locationbyhand.data.saveData.ApData;
import com.ascend.wangfeng.locationbyhand.data.saveData.LocationData;
import com.ascend.wangfeng.locationbyhand.data.saveData.StaConInfo;
import com.ascend.wangfeng.locationbyhand.data.saveData.StaData;
import com.ascend.wangfeng.locationbyhand.dialog.LoadingDialog;
import com.ascend.wangfeng.locationbyhand.event.FTPEvent;
import com.ascend.wangfeng.locationbyhand.util.VersionUtils;
import com.ascend.wangfeng.locationbyhand.view.fragment.SetFragment;

import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 作者：lish on 2018-08-16.
 * 描述：上传数据
 */

public class UpLoadUtils {
    private String version = "";

    //点击上传文件
    public void UpLoad(final Context context, final List<ApData> aplist, final List<StaData> stalist
            , final List<StaConInfo> sclist, final List<LocationData> gpslist) {
        if (MyApplication.mDevicdID != null) {                               //连接成功
            version = VersionUtils.getVersion(context).toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long time = (System.currentTimeMillis() / 1000);
                    String filePath = "/mnt/sdcard/";
                    String fileName = MyApplication.mDevicdID + "[211.211.211.211]_" + time;

                    FTPClientData otherFtpClientData = new FTPClientData();
                    FTPClient otherFtpClient = otherFtpClientData.ftpConnect();
                    if (otherFtpClient == null) {
                        EventBus.getDefault().post(new FTPEvent(false));
                        MyApplication.ftpConnect = false;
                        return;
                    }
                    try {
                        otherFtpClient.makeDirectory(MyApplication.UpLoadFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //上传AP数据
                    FileData fileData = new FileData(context, filePath, fileName + ".carapl", aplist, version);
                    boolean otherLoad1 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".carapl");
                    //上传终端数据
                    FileData staFile = new FileData(context, filePath, fileName + ".carmac", stalist, version, 1);
                    boolean otherLoad2 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".carmac");
                    //上传连接数据
                    FileData ScFile = new FileData(context, filePath, fileName + ".carnet", sclist, version, "1");
                    boolean otherLoad3 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".carnet");
                    //上传GPS轨迹的坐标
                    FileData GpsFile = new FileData(context, filePath, fileName + ".cargps", gpslist, version, "", 1);
                    boolean otherLoad4 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".cargps");
                    EventBus.getDefault().post(new FTPEvent(true));
                    MyApplication.ftpConnect = true;

                    FTPClientData ourFtpClientData = new FTPClientData(Config.UpLoadFtpUrl, Config.UpLoadFtpPort
                            , Config.UpLoadFtpUser, Config.UpLoadFtpPass, Config.UpLoadFilePath);
                    FTPClient ourFtpClient = ourFtpClientData.ftpConnect();
                    //上传AP数据
                    boolean ourLoad1 = ourFtpClientData.ftpUpload(ourFtpClient, filePath, fileName + ".carapl");
                    //上传终端数据
                    boolean ourLoad2 = ourFtpClientData.ftpUpload(ourFtpClient, filePath, fileName + ".carmac");
                    //上传连接数据
                    boolean ourLoad3 = ourFtpClientData.ftpUpload(ourFtpClient, filePath, fileName + ".carnet");
                    //上传GPS轨迹的坐标
                    boolean ourLoad4 = ourFtpClientData.ftpUpload(ourFtpClient, filePath, fileName + ".cargps");

                    if (otherLoad1 & ourLoad1) delectLocalData(filePath,fileName + ".carapl");
                    if (otherLoad2 & ourLoad2) delectLocalData(filePath,fileName + ".carmac");
                    if (otherLoad3 & ourLoad3) delectLocalData(filePath,fileName + ".carnet");
                    if (otherLoad4 & ourLoad4) delectLocalData(filePath,fileName + ".cargps");
                }

            }).start();
        }
    }
    /**
     * FTP服务器连接测试
     * @author lish
     * created at 2018-08-27 14:35
     */
    public static void UpLoadTest(final Context context, final Handler handler) {
//        if (MyApplication.mDevicdID != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FTPClientData otherFtpClientData = new FTPClientData();
                    FTPClient otherFtpClient = otherFtpClientData.ftpConnect();
                    Looper.prepare();
                    if (otherFtpClient != null) {
                        EventBus.getDefault().post(new FTPEvent(true));
                        MyApplication.ftpConnect = true;
                        handler.sendEmptyMessage(SetFragment.UPLOADTESTSUCESS);
                    } else {
                        //上传失败，信息有问题
//                        Toast.makeText(context, "服务器连接失败", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new FTPEvent(false));
                        MyApplication.ftpConnect = false;
                        handler.sendEmptyMessage(SetFragment.UPLOADTESTFLASE);
                    }

                    Looper.loop();
                }
            }).start();
//        }else {
//            Toast.makeText(context, "请先连接设备", Toast.LENGTH_SHORT).show();
//        }
    }
    private void delectLocalData(String filePath, String fileName) {
        // 上传成功后， 删除手机上的文件
        File localFile = new File(filePath + fileName);

        if (localFile.exists()) {
            localFile.delete();
        }
    }
}
