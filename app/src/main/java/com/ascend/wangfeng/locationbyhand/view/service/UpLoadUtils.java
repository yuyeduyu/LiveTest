package com.ascend.wangfeng.locationbyhand.view.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.bean.LoadError;
import com.ascend.wangfeng.locationbyhand.data.FTPClientData;
import com.ascend.wangfeng.locationbyhand.data.FileData;
import com.ascend.wangfeng.locationbyhand.data.saveData.ApData;
import com.ascend.wangfeng.locationbyhand.data.saveData.LocationData;
import com.ascend.wangfeng.locationbyhand.data.saveData.StaConInfo;
import com.ascend.wangfeng.locationbyhand.data.saveData.StaData;
import com.ascend.wangfeng.locationbyhand.event.FTPEvent;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtil;
import com.ascend.wangfeng.locationbyhand.util.VersionUtils;
import com.ascend.wangfeng.locationbyhand.view.fragment.SetFragment;
import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;
import java.io.File;
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
                    String filePath = "/mnt/sdcard/wxldData/";
                    String fileName = MyApplication.mDevicdID + "[211.211.211.211]_" + time;

                    FTPClientData otherFtpClientData = new FTPClientData(context);
                    FTPClient otherFtpClient = otherFtpClientData.ftpConnect();
                    if (otherFtpClient == null) {
                        EventBus.getDefault().post(new FTPEvent(false));
                        MyApplication.ftpConnect = false;
                    }else {
                        //重新上传失败的数据
                        List<LoadError> loadErrors = SharedPreferencesUtil.getList(context, "loadError");
                        if (loadErrors != null) {
                            for (LoadError loadError:loadErrors){
                                otherFtpClientData.ftpUpload(otherFtpClient, loadError.getFilePath(), loadError.getFileName(),true);
                            }
                        }
                        //上传AP数据
                        FileData fileData = new FileData(context, filePath, fileName + ".carapl", aplist, version);
                        boolean otherLoad1 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".carapl",false);
                        //上传终端数据
                        FileData staFile = new FileData(context, filePath, fileName + ".carmac", stalist, version, 1);
                        boolean otherLoad2 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".carmac",false);
                        //上传连接数据
                        FileData ScFile = new FileData(context, filePath, fileName + ".carnet", sclist, version, "1");
                        boolean otherLoad3 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".carnet",false);
                        //上传GPS轨迹的坐标
                        FileData GpsFile = new FileData(context, filePath, fileName + ".cargps", gpslist, version, "", 1);
                        boolean otherLoad4 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".cargps",false);
                        EventBus.getDefault().post(new FTPEvent(true));
                        MyApplication.ftpConnect = true;

//                    FTPClientData ourFtpClientData = new FTPClientData(Config.UpLoadFtpUrl, Config.UpLoadFtpPort
//                            , Config.UpLoadFtpUser, Config.UpLoadFtpPass, Config.UpLoadFilePath);
//                    FTPClient ourFtpClient = ourFtpClientData.ftpConnect();
//                    //上传AP数据
//                    boolean ourLoad1 = ourFtpClientData.ftpUpload(ourFtpClient, filePath, fileName + ".carapl");
//                    //上传终端数据
//                    boolean ourLoad2 = ourFtpClientData.ftpUpload(ourFtpClient, filePath, fileName + ".carmac");
//                    //上传连接数据
//                    boolean ourLoad3 = ourFtpClientData.ftpUpload(ourFtpClient, filePath, fileName + ".carnet");
//                    //上传GPS轨迹的坐标
//                    boolean ourLoad4 = ourFtpClientData.ftpUpload(ourFtpClient, filePath, fileName + ".cargps");

//                    if (otherLoad1)
//                        delectLocalData(filePath, fileName + ".carapl");
//                    if (otherLoad2)
//                        delectLocalData(filePath, fileName + ".carmac");
//                    if (otherLoad3)
//                        delectLocalData(filePath, fileName + ".carnet");
//                    if (otherLoad4)
//                        delectLocalData(filePath, fileName + ".cargps");
                    }
                }

            }).start();
        }
    }

    /**
     * FTP服务器连接测试
     *
     * @author lish
     * created at 2018-08-27 14:35
     */
    public static void UpLoadTest(final Context context, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FTPClientData otherFtpClientData = new FTPClientData(context);
                FTPClient otherFtpClient = otherFtpClientData.ftpConnect();
                Looper.prepare();
                if (otherFtpClient != null) {
                    EventBus.getDefault().post(new FTPEvent(true));
                    MyApplication.ftpConnect = true;
                    handler.sendEmptyMessage(SetFragment.UPLOADTESTSUCESS);
                } else {
                    EventBus.getDefault().post(new FTPEvent(false));
                    MyApplication.ftpConnect = false;
                    handler.sendEmptyMessage(SetFragment.UPLOADTESTFLASE);
                }

                Looper.loop();
            }
        }).start();
    }
    private void delectLocalData(String filePath, String fileName) {
        // 上传成功后， 删除手机上的文件
        File localFile = new File(filePath + fileName);

        if (localFile.exists()) {
            localFile.delete();
        }
    }
}
