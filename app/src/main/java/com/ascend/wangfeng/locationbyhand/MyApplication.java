package com.ascend.wangfeng.locationbyhand;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;

import com.anye.greendao.gen.DaoMaster;
import com.anye.greendao.gen.DaoSession;
import com.anye.greendao.gen.NoteDoDao;
import com.ascend.wangfeng.locationbyhand.bean.Ghz;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;
import com.ascend.wangfeng.locationbyhand.util.CrashHandler;

import java.util.List;

/**
 * Created by fengye on 2017/2/7.
 * email 1040441325@qq.com
 */
public class MyApplication extends Application {
    public static Context mContext;
    public static boolean isDev;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    public static MyApplication instances;
    public static String mDevicdID;//设备编号
    private static List<NoteDo> mNoteDos;//目标列表
    public static Ghz mGhz = Ghz.G24;//0为2.4,1为5
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

    @Override
    public void onCreate() {
        super.onCreate();
         /*初始化错误日志收集*/
        if (getVersionNo() % 2 == 1) isDev = true;
        mContext = getBaseContext();
        Config.updateConfig();
        // LeakCanary.install(this);

        if (!isDev) {
            CrashHandler crashHandler = CrashHandler.getInstance();
            crashHandler.init(getApplicationContext());
        }
        instances = this;
        setDatabase();
        mContext = this;
        initBase();
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
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
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
}
