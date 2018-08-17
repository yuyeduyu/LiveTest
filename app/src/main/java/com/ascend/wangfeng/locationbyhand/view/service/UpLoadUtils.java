package com.ascend.wangfeng.locationbyhand.view.service;

import android.app.Activity;
import android.content.Context;
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
import com.ascend.wangfeng.locationbyhand.event.FTPEvent;
import com.ascend.wangfeng.locationbyhand.util.VersionUtils;

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
                    FileData fileData = new FileData(context, filePath, fileName + ".apl", aplist, version);
                    boolean otherLoad1 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".apl");
                    //上传终端数据
                    FileData staFile = new FileData(context, filePath, fileName + ".log", stalist, version, 1);
                    boolean otherLoad2 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".log");
                    //上传连接数据
                    FileData ScFile = new FileData(context, filePath, fileName + ".net", sclist, version, "1");
                    boolean otherLoad3 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".net");
                    //上传GPS轨迹的坐标
                    FileData GpsFile = new FileData(context, filePath, fileName + ".gps", gpslist, version, "", 1);
                    boolean otherLoad4 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".gps");
                    EventBus.getDefault().post(new FTPEvent(true));
                    MyApplication.ftpConnect = true;

                    FTPClientData ourFtpClientData = new FTPClientData(Config.UpLoadFtpUrl, Config.UpLoadFtpPort
                            , Config.UpLoadFtpUser, Config.UpLoadFtpPass, Config.UpLoadFilePath);
                    FTPClient ourFtpClient = ourFtpClientData.ftpConnect();
                    //上传AP数据
                    boolean ourLoad1 = ourFtpClientData.ftpUpload(ourFtpClient, filePath, fileName + ".apl");
                    //上传终端数据
                    boolean ourLoad2 = ourFtpClientData.ftpUpload(ourFtpClient, filePath, fileName + ".log");
                    //上传连接数据
                    boolean ourLoad3 = ourFtpClientData.ftpUpload(ourFtpClient, filePath, fileName + ".net");
                    //上传GPS轨迹的坐标
                    boolean ourLoad4 = ourFtpClientData.ftpUpload(ourFtpClient, filePath, fileName + ".gps");

                    if (otherLoad1 & ourLoad1) delectLocalData(filePath,fileName + ".apl");
                    if (otherLoad2 & ourLoad2) delectLocalData(filePath,fileName + ".log");
                    if (otherLoad3 & ourLoad3) delectLocalData(filePath,fileName + ".net");
                    if (otherLoad4 & ourLoad4) delectLocalData(filePath,fileName + ".gps");
                }

            }).start();
        }
    }

    private void delectLocalData(String filePath, String fileName) {
        // 上传成功后， 删除手机上的文件
        File localFile = new File(filePath + fileName);

        if (localFile.exists()) {
            localFile.delete();
        }
    }
}
