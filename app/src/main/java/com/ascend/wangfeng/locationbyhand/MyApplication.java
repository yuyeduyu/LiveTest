package com.ascend.wangfeng.locationbyhand;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.anye.greendao.gen.DaoMaster;
import com.anye.greendao.gen.DaoSession;
import com.anye.greendao.gen.NoteDoDao;
import com.ascend.wangfeng.locationbyhand.bean.Ghz;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;
import com.ascend.wangfeng.locationbyhand.util.CrashHandler;
//import com.ascend.wangfeng.locationbyhand.view.service.LocationService;
import java.util.List;

/**
 * Created by fengye on 2017/2/7.
 * email 1040441325@qq.com
 */
public class MyApplication extends Application {

    private String TAG = getClass().getCanonicalName();

    public static Context mContext;
    public static boolean isDev;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    public static MyApplication instances;

    public static String mDevicdID;//设备编号
    public static String mDevicdMac;//设备mac
    private static List<NoteDo> mNoteDos;//目标列表
    public static Ghz mGhz = Ghz.G24;//0为2.4,1为5

    public static boolean connectStation = false;         //连接状态

    public static int AppVersion = -2;//app版本  -C,-Cmini，-Cplus
    /**
     * 获取数据的请求是否发送
     */
    private static Boolean isDataRun =true;

    public static List<NoteDo> getmNoteDos() {
        return mNoteDos;
    }

    public static void setmNoteDos(List<NoteDo> mNoteDos) {
        MyApplication.mNoteDos = mNoteDos;
    }

    public static void setAppVersion(int AppVersion){
        MyApplication.AppVersion = AppVersion;
    }
    public static int getAppVersion(){
        return AppVersion;
    }
    @Override
    public void onCreate() {
        super.onCreate();

         /*初始化错误日志收集*/
        if (getVersionNo() % 2 == 1) isDev = true;
        mContext = getBaseContext();
        Config.updateConfig();
        if (!isDev) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext());
        }
        instances = this;
        setDatabase();
        mContext = this;
        initBase();
        ftpData();
    }

    public static Boolean getIsDataRun() {
        return isDataRun;
    }

    public static void setIsDataRun(Boolean isDataRun) {
        MyApplication.isDataRun = isDataRun;
    }

    /**
     * 加载基础数据
     */
    private void initBase() {
        NoteDoDao dao = getInstances().getDaoSession().getNoteDoDao();
        mNoteDos =dao.loadAll();
    }

    public static MyApplication getInstances() {
        return instances;
    }

    private void setDatabase() {

        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }
    public DaoSession getDaoSession() {
        return mDaoSession;
    }


    public SQLiteDatabase getDb() {
        return db;
    }

    //获取版本号
    private Integer getVersionNo() {
        Integer version = 0;
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            version = info.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 是否有网判断
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取上传时间间隔
     * @return
     */
    public static int GetUpLoadTime(){
        int upLoadTime;
        try {
            SharedPreferences read = mContext.getSharedPreferences("uploadTime", MODE_PRIVATE);
            if (read != null) {
                String value = read.getString("time", "");
                if (value != null)
                    upLoadTime = Integer.parseInt(value);
                else
                    upLoadTime = 5 * 60 * 1000;
            } else {
                upLoadTime = 5 * 60 * 1000;                 //默认为5分钟
            }
        }catch (Exception e){
            upLoadTime = 5 * 60 * 1000;                 //默认为5分钟
        }
        return upLoadTime;
    }

    public static String UpLoadFtpUrl = "123.57.175.155";                   //上传的地址
    public static int UpLoadFtpPort = 21 ;                                   //端口号
    public static String UpLoadFtpUser = "test123" ;                         //ftp帐号
    public static String UpLoadFtpPass = "test123" ;                         //ftp密码
    public static String UpLoadFilePath = "wificar" ;                        //文件名

    public static void ftpData(){
        SharedPreferences ftpSp = mContext.getSharedPreferences("ftpData",MODE_PRIVATE);
        if (ftpSp.getString("url","")!="" &&
                ftpSp.getInt("port",-1) != -1 &&
                ftpSp.getString("user","")!="" &&
                ftpSp.getString("password","")!=""
//                && ftpSp.getString("path","")!=""
                ) {
            UpLoadFtpUrl = ftpSp.getString("url", "");
            UpLoadFtpPort = ftpSp.getInt("port",-1);
            UpLoadFtpUser = ftpSp.getString("user","");
            UpLoadFtpPass = ftpSp.getString("password","");
            UpLoadFilePath = ftpSp.getString("path","");
            Log.e("FTP","存在SharePreferences-->"+UpLoadFtpUrl+">>"+UpLoadFtpPort+">>"+UpLoadFtpUser+">>"+UpLoadFtpPass+">>"+UpLoadFilePath);
        }else{
            Log.e("FTP","bu存在SharePreferences");
//            UpLoadFtpUrl = "123.57.175.155";                   //上传的地址
//            UpLoadFtpPort = 21;                                   //端口号
//            UpLoadFtpUser = "test123";                         //上传的地址
//            UpLoadFtpPass = "test123";                         //上传的地址
//            UpLoadFilePath = "wificartest";                        //文件名

        }

    }
}
