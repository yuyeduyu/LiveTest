package com.ascend.wangfeng.locationbyhand.view.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.data.FTPClientData;
import com.ascend.wangfeng.locationbyhand.data.FileData;
import com.ascend.wangfeng.locationbyhand.data.saveData.AllUpLoadData;
import com.ascend.wangfeng.locationbyhand.data.saveData.ApData;
import com.ascend.wangfeng.locationbyhand.data.saveData.LocationData;
import com.ascend.wangfeng.locationbyhand.data.saveData.StaConInfo;
import com.ascend.wangfeng.locationbyhand.data.saveData.StaData;
import com.ascend.wangfeng.locationbyhand.data.saveData.UpLoadData;
import com.ascend.wangfeng.locationbyhand.view.receiver.AlarmReceiver;

import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsw on 2018/5/10.
 */


public class UploadService extends Service {

    private String TAG = getClass().getCanonicalName();
    private boolean first = true;
    List<ApData> aplist = new ArrayList<>();
    List<StaData> stalist = new ArrayList<>();
    List<StaConInfo> sclist = new ArrayList<>();
    List<LocationData> gpslist = new ArrayList<>();
    private PowerManager.WakeLock wakeLock = null;
    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        //休眠时 唤醒cpu
//        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, UploadService.class.getName());
//        wakeLock.acquire();
    }

    //   获取需要上传的数据
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(UpLoadData upLoadData_) {                            //-->UpLoadData upLoadData
        UpLoadData upLoadData = sendfilterData(upLoadData_);                //将全部数据进行处理
        this.aplist = upLoadData.getAplist();
        this.stalist = upLoadData.getStalist();
        this.sclist = upLoadData.getsClist();
        this.gpslist = upLoadData.getGpslist();
//        Log.e(TAG,"UP>>-->Service  上传的数据 aplist:"+aplist.size()
//                +"stalist:"+stalist.size()+"  sclist:"+sclist.size()+"   gpslist:"+gpslist.size());

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //第一次启动服务 不上传数据
        if (first){
            first = false;
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long time = (System.currentTimeMillis() / 1000);
                    String filePath = "/mnt/sdcard/";
                    String fileName = MyApplication.mDevicdID + "[211.211.211.211]_" + time;
                    if (MyApplication.mDevicdID != null && MyApplication.isConnected(getBaseContext())) {                               //连接成功

                        FTPClientData ftpClientData = new FTPClientData();

                        FTPClient ftpClient = ftpClientData.ftpConnect();
                        if (ftpClient ==null){
                            Looper.prepare();
                            Toast.makeText(UploadService.this,"连接FTP服务器失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            return;

                        }
                        try {
                            ftpClient.makeDirectory(MyApplication.UpLoadFilePath); //如果FTP上不存在该文件，则创建文件
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //上传AP数据
                        FileData fileData = new FileData(getBaseContext(), filePath, fileName, aplist, getVersion().toString());        //将数据写入本地文件中
                        ftpClientData.ftpUpload(ftpClient, filePath, fileName, "apl");
                        //上传终端数据
                        FileData staFile = new FileData(getBaseContext(), filePath, fileName, stalist, getVersion().toString(), 1);
                        ftpClientData.ftpUpload(ftpClient, filePath, fileName, "log");
                        //上传连接数据
                        FileData ScFile = new FileData(getBaseContext(), filePath, fileName, sclist, getVersion().toString(), "1");
                        ftpClientData.ftpUpload(ftpClient, filePath, fileName, "net");
                        //发送需要剔除的数据信息
                        EventBus.getDefault().post(new AllUpLoadData(aplist, stalist, sclist));
                        //上传GPS轨迹的坐标
                        FileData GpsFile = new FileData(getBaseContext(), filePath, fileName, gpslist, getVersion().toString(), "", 1);
                        ftpClientData.ftpUpload(ftpClient, filePath, fileName, "gps");

                    }
                }
            }).start();
        }
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // 上传时间间隔（ms）
        int anHour = MyApplication.GetUpLoadTime();
        //返回系统启动到现在的毫秒数，包含休眠时间。
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;                //获取从设备boot后经历的时间值
//        Log.e(TAG,"现在的上传间隔为:"+anHour+"    boot后经历的时间值："+triggerAtTime);

        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
//        return super.onStartCommand(intent, flags, startId);

       /* Notification notification = new Notification.Builder(this.getApplicationContext())
                .setContentTitle("无线雷达mini")
                .setContentText("数据上传服务")
                .setSmallIcon(R.mipmap.upload)
                .setWhen(System.currentTimeMillis())
                .build();
        startForeground(3, notification);*/
        return Service.START_STICKY;
    }

    public UploadService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            return "未知";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        Log.e(TAG, "销毁服务");
    }


    /**
     * 过滤10分钟未更新的数据
     *
     * @param aps
     * @param stas
     * @param staCons
     * @return
     */
    private int INTERVAL = 60 * 5;             //2分钟内数据未更新过

    private UpLoadData sendfilterData(UpLoadData upLoadData) {
        //十分钟剔除一次未更新数据
//        int INTERVAL = 10;                                      //测试 一分钟剔除一次
        long nowTime = System.currentTimeMillis() / 1000;                 //获取当前时间
        List<ApData> upAps = new ArrayList<>();
        List<StaData> upStas = new ArrayList<>();
        List<StaConInfo> upStaCons = new ArrayList<>();
        List<LocationData> upGps = new ArrayList<>();
        for (int i = 0; i < upLoadData.getAplist().size(); i++) {
            if ((nowTime - upLoadData.getAplist().get(i).getlTime()) >= INTERVAL) {
                upAps.add(upLoadData.getAplist().get(i));
            }
        }
        for (int i = 0; i < upLoadData.getStalist().size(); i++) {
            if ((nowTime - upLoadData.getStalist().get(i).getlTime()) >= INTERVAL) {
                upStas.add(upLoadData.getStalist().get(i));
            }
        }
        for (int i = 0; i < upLoadData.getsClist().size(); i++) {
            if ((nowTime - upLoadData.getsClist().get(i).getlTime()) >= INTERVAL) {
                upStaCons.add(upLoadData.getsClist().get(i));
            }
        }
        for (int i = 0; i < upLoadData.getGpslist().size(); i++) {
            if ((nowTime - upLoadData.getGpslist().get(i).getTime()) < (5 * 60)) {          //记录五分钟以内的数据
                upGps.add(upLoadData.getGpslist().get(i));
            }
        }

        return new UpLoadData(upAps, upStas, upStaCons, upGps);
    }

}

