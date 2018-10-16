package com.ascend.wangfeng.locationbyhand.view.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.ascend.wangfeng.locationbyhand.BuildConfig;
import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.bean.LoadError;
import com.ascend.wangfeng.locationbyhand.data.FTPClientData;
import com.ascend.wangfeng.locationbyhand.data.FileData;
import com.ascend.wangfeng.locationbyhand.data.saveData.ApData;
import com.ascend.wangfeng.locationbyhand.data.saveData.LocationData;
import com.ascend.wangfeng.locationbyhand.data.saveData.StaConInfo;
import com.ascend.wangfeng.locationbyhand.data.saveData.StaData;
import com.ascend.wangfeng.locationbyhand.event.FTPEvent;
import com.ascend.wangfeng.locationbyhand.util.LogUtils;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtil;
import com.ascend.wangfeng.locationbyhand.util.VersionUtils;
import com.ascend.wangfeng.locationbyhand.util.ZipHelper;
import com.ascend.wangfeng.locationbyhand.util.ZipUtils;
import com.ascend.wangfeng.locationbyhand.view.activity.SetftpActivity;
import com.ascend.wangfeng.locationbyhand.view.fragment.SetFragment;

import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
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
                    if (BuildConfig.Adress.equals("台州")) {
                        //台州地区，移动网络与其他网络服务器地址不同
                        if (otherFtpClient == null) {
                            otherFtpClientData = new FTPClientData(context, Config.URL_YIDONG, MyApplication.UpLoadFtpPort
                                    , MyApplication.UpLoadFtpUser, MyApplication.UpLoadFtpPass, MyApplication.UpLoadFilePath);
                            otherFtpClient = otherFtpClientData.ftpConnect();
                        }
                    }

                    if (otherFtpClient == null) {
                        EventBus.getDefault().post(new FTPEvent(false));
                        MyApplication.ftpConnect = false;
                    }else {
                        //重新上传失败的数据
                        List<LoadError> loadErrors = SharedPreferencesUtil.getList(context, "loadError");
                        if (loadErrors != null) {
                            for (LoadError loadError : loadErrors) {
                                otherFtpClientData.ftpUpload(otherFtpClient, loadError.getFilePath(), loadError.getFileName(), true, true);
                            }
                        }
                    }

                    FileData fileData = new FileData();

                    //上传AP数据
                    File apFile = fileData.FileData(context, filePath, fileName + ".carapl", aplist, version);
//                    boolean otherLoad1 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".carapl", false);
                    //上传终端数据
                    File staFile = fileData.FileData(context, filePath, fileName + ".carmac", stalist, version, 1);
//                    boolean otherLoad2 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".carmac", false);
                    //上传连接数据
                    File ScFile = fileData.FileData(context, filePath, fileName + ".carnet", sclist, version, "1");
//                    boolean otherLoad3 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".carnet", false);
                    //上传GPS轨迹的坐标
                    File GpsFile = fileData.FileData(context, filePath, fileName + ".cargps", gpslist, version, "", 1);
//                    boolean otherLoad4 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".cargps", false);

                    File file[] = {apFile, staFile, ScFile, GpsFile};
                    ZipUtils zipUtils = new ZipUtils();
                    zipUtils.compressFile(file,filePath+ fileName + ".zap");

                    //try {
//                        zipUtils.upZipFile(new File("/mnt/sdcard/test.zap"),"/mnt/sdcard/fileName.carapl");
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

//                    ZipHelper.zipFiles(file, filePath + fileName + ".zap");//压缩文件
////                    execLinuxCommand("zip -q -r "+fileName + ".zap "+filePath+fileName+".carapl");
//
//                    ZipHelper.unZipFile(filePath,filePath+fileName+".zap");//解压缩文件
//                    ZipHelper.unZipFile("/mnt/sdcard/","/mnt/sdcard/test.zap");//解压缩文件

                    //上传AP数据
                    if (!MyApplication.UpLoadFtpUrl.equals(Config.UpLoadFtpUrl)) {
                        //非公司阿里云服务器，则将数据提交到阿里云平台
                        FTPClientData ourFtpClientData = new FTPClientData(context, Config.UpLoadFtpUrl, Config.UpLoadFtpPort
                                , Config.UpLoadFtpUser, Config.UpLoadFtpPass, Config.UpLoadFilePath);
                        FTPClient ourFtpClient = ourFtpClientData.ftpConnect();
                        boolean ourLoad1 = ourFtpClientData.ftpUpload(ourFtpClient, filePath, fileName + ".zap", false, false);
                        ourFtpClientData.ftpDisconnect();
                    }
                    boolean otherLoad1 = otherFtpClientData.ftpUpload(otherFtpClient, filePath, fileName + ".zap", false, true);
                    otherFtpClientData.ftpDisconnect();
                    if (otherLoad1) {
                        EventBus.getDefault().post(new FTPEvent(true));
                    } else {
                        EventBus.getDefault().post(new FTPEvent(false));
                    }
                    MyApplication.ftpConnect = true;
                }

            }).start();
        }
    }
    /**
     * android 调用linux命令 需要root权限，
     * @Author lish
     * @Date 2018-10-11 9:38
     */
    private void execLinuxCommand(String cmd){
        Runtime runtime = Runtime.getRuntime();
        try {
            Process localProcess = runtime.exec("su");
            OutputStream localOutputStream = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);
            localDataOutputStream.writeBytes(cmd);
            localDataOutputStream.flush();
        } catch (IOException e) {
            LogUtils.e("e","strLine:"+e.getMessage());
            e.printStackTrace();
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
                if (otherFtpClient == null) {
                    otherFtpClientData = new FTPClientData(context, Config.URL_YIDONG, MyApplication.UpLoadFtpPort
                            , MyApplication.UpLoadFtpUser, MyApplication.UpLoadFtpPass, MyApplication.UpLoadFilePath);
                    otherFtpClient = otherFtpClientData.ftpConnect();
                    otherFtpClientData.ftpDisconnect();
                }
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
