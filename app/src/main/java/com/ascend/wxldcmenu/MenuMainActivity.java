package com.ascend.wxldcmenu;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anye.greendao.gen.DaoSession;
import com.anye.greendao.gen.LogDao;
import com.ascend.wangfeng.locationbyhand.AppVersionConfig;
import com.ascend.wangfeng.locationbyhand.BuildConfig;
import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.KaiZhanBean;
import com.ascend.wangfeng.locationbyhand.bean.MounthCollectData;
import com.ascend.wangfeng.locationbyhand.bean.NoteDoDeal;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;
import com.ascend.wangfeng.locationbyhand.event.FTPEvent;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.ble.AppVersionEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.ConnectedEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.MessageEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.VolEvent;
import com.ascend.wangfeng.locationbyhand.keeplive.LiveService;
import com.ascend.wangfeng.locationbyhand.login.LoginActivity;
import com.ascend.wangfeng.locationbyhand.util.CustomDatePickerUtils.GetDataUtils;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtil;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;
import com.ascend.wangfeng.locationbyhand.util.TimeUtil;
import com.ascend.wangfeng.locationbyhand.util.network.NetStatusWatch;
import com.ascend.wangfeng.locationbyhand.util.network.NetworkStatus;
import com.ascend.wangfeng.locationbyhand.util.versionUpdate.AppVersionUitls;
import com.ascend.wangfeng.locationbyhand.view.activity.AboutActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.AnalyseActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.BLEActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.BaseActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.MainActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.NewLogAllActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.PermissionListener;
import com.ascend.wangfeng.locationbyhand.view.activity.SetActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.StatisticsActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.TargetActivity;
import com.ascend.wangfeng.locationbyhand.view.service.BleService;
import com.ascend.wangfeng.locationbyhand.view.service.LocationService;
import com.ascend.wangfeng.locationbyhand.view.service.UploadService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.ascend.wangfeng.locationbyhand.view.activity.StatisticsActivity.getSql;

/**
 * 便携式汽车采集app首页
 *
 * @author lish
 *         created at 2018-08-06 11:22
 */
public class MenuMainActivity extends BaseActivity implements NetStatusWatch.OnNetStatusChangedListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.layout_main)
    LinearLayout layoutMain;
    @BindView(R.id.ll_log)
    LinearLayout llLog;
    @BindView(R.id.ll_bukong)
    LinearLayout llBukong;
    @BindView(R.id.ll_fenxi)
    LinearLayout llFenxi;
    @BindView(R.id.ll_tongji)
    LinearLayout llTongji;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.set)
    LinearLayout set;
    @BindView(R.id.about)
    LinearLayout about;
    @BindView(R.id.bluestatu)
    TextView bluestatu;
    @BindView(R.id.dev_text)
    TextView devText;
    @BindView(R.id.ll_dev)
    RelativeLayout llDev;
    @BindView(R.id.upload_text)
    TextView uploadText;
    @BindView(R.id.rl_upload)
    LinearLayout rlUpload;
    @BindView(R.id.iv_collect)
    ImageView ivCollect;
    @BindView(R.id.iv_log)
    ImageView ivLog;
    @BindView(R.id.iv_bukong)
    ImageView ivBukong;
    @BindView(R.id.iv_fenxi)
    ImageView ivFenxi;
    @BindView(R.id.iv_tongji)
    ImageView ivTongji;
    @BindView(R.id.pb_volue)
    ProgressBar pbVolue;
    @BindView(R.id.tv_volue)
    TextView tvVolue;
    @BindView(R.id.ll_notice)
    LinearLayout llNotice;
    @BindView(R.id.ll_limit)
    LinearLayout llLimit;
    private DaoSession daoSession;

    Runnable runnable = null;//更新电量
    private Subscription mVolRxBus;
    private long rate = 5000;//第一次获取电量间隔
    public static final long VOL_RATE = 1 * 60 * 1000;//获取电量后 每次获取电量时间间隔
    final static Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_menu_main;
    }

    @Override
    protected void initView() {
        daoSession = MyApplication.instances.getDaoSession();
        if (!BuildConfig.isManger)
            initMangerView();
        initService();
        initVol();
        listenConnect();
        initTool();
        initBleActivity();
        //获取动态权限
        getPermissions();
        //版本更新监测
        AppVersionUitls.checkVersion(this, AppVersionConfig.appVersion
                , AppVersionConfig.appName, null, MenuMainActivity.class, false);
        //网络状态监听
        NetStatusWatch.getInstance().regisiterListener(this);
    }

    //更新电量
    private void initVol() {
        mVolRxBus = RxBus.getDefault().toObservable(VolEvent.class)         //接受数据
                //在io线程进行订阅，可以执行一些耗时操作
                .subscribeOn(Schedulers.io())
                //在主线程中进行观察，可做UI更新操作
                .observeOn(AndroidSchedulers.mainThread())
                //观察的对象   BaseSubcribe<VolEvent>继承Subscriber
                .subscribe(new BaseSubcribe<VolEvent>() {
                    @Override
                    public void onNext(VolEvent event) {
                        pbVolue.setProgress(event.getVol());
                        tvVolue.setText(event.getVol() + "%");
                        rate = VOL_RATE;
                    }
                });
        //定时获取电量
        runnable = new Runnable() {
            @Override
            public void run() {
                MessageEvent event = new MessageEvent(MessageEvent.SEND_DATA);
                event.setData("GETVOL");
                RxBus.getDefault().post(event);
                handler.postDelayed(this, rate);
            }
        };

        handler.post(runnable);
    }

    /**
     * 低权限版本更换主页图标
     *
     * @author lish
     * created at 2018-08-27 15:18
     */
    private void initMangerView() {
            llLimit.setVisibility(View.GONE);
//        ivCollect.setImageResource(R.mipmap.caiji_l);
//        ivLog.setImageResource(R.mipmap.rizhi_l);
//        ivBukong.setImageResource(R.mipmap.bukong_l);
//        ivFenxi.setImageResource(R.mipmap.fenxi_l);
    }

    /**
     * 第一次打开app，统计采集数量累加到当月数据中保存
     *
     * @author lish
     * created at 2018-08-24 16:14
     */
    static long oneDay = 24 * 60 * 60 * 1000;

    private void mounthCollectData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String mounth = "";
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
                String now = sdf.format(new Date());
                String startTime = "";
                String endTime = GetDataUtils.getDateStrByDay(now, -1);
                if (GetDataUtils.getNowTime("dd").equals("01")) {
                    //当月第一天则统计上月数据
                    mounth = GetDataUtils.getLastDate(GetDataUtils.getNowTime("yyyy-MM-dd"), 1, "yyyy-MM-dd");
                } else {
                    mounth = GetDataUtils.getNowTime("yyyy-MM");
                }
                MounthCollectData data = SharedPreferencesUtil.getObject(MenuMainActivity.this, mounth);
                int diffDays = 0;
                if (data == null) {
                    //没有保存记录，则统计上一天数据，存储到当月数据中
                    startTime = endTime;
                    data = new MounthCollectData();
                } else {
                    //测试数据
//                    data.setApCount("11");
//                    data.setStaCount("11");
//                    data.setDayTime("2018-09-02");
//                    data = new MounthCollectData();
//                    SharedPreferencesUtil.putObject(MenuMainActivity.this,mounth,data);
                    startTime = data.getDayTime();
                    if (startTime == null)
                        startTime = endTime;
                }
                // 跨月份要分别统计 开始时间和 mounth 不在同一个月 则要统计2次
                if (GetDataUtils.compare_date(startTime.substring(0, 8), mounth, "yyyy-MM") == 0) {
                    //同一个月
                    diffDays = GetDataUtils.differentDaysByMillisecond(startTime, endTime, Config.timeTypeByYear);
                    getByTimeData(data, daoSession, startTime, endTime, diffDays + 1, mounth);
                } else {
                    //不同月份，统计上月份数据
                    String lastMounth = GetDataUtils.getLastDate(GetDataUtils.getNowTime("yyyy-MM-dd"), 1, "yyyy-MM-dd");
                    //上个月 最后一天日期
                    String lastEndTime = TimeUtil.getLastLastDay("yyyy-MM-dd");
                    diffDays = GetDataUtils.differentDaysByMillisecond(startTime, lastEndTime, Config.timeTypeByYear);
                    getByTimeData(data, daoSession, startTime, lastEndTime, diffDays + 1, lastMounth);

                    //统计本月数据
                    //本月第一天日期
                    String firstStartTime = TimeUtil.getFirstDay("yyyy-MM-dd");
                    diffDays = GetDataUtils.differentDaysByMillisecond(firstStartTime, endTime, Config.timeTypeByYear);
                    data = new MounthCollectData();
                    getByTimeData(data, daoSession, firstStartTime, endTime, diffDays + 1, mounth);
                }

                //删除指定天数前的数据
                delectLogData();
            }
        }).start();
    }

    /**
     * 根据开始结束时间 查询过滤重复mac数
     *
     * @author lish
     * created at 2018-07-17 11:01
     */
    public void getByTimeData(MounthCollectData data, DaoSession session, String startTime
            , String endTime, int diffDays, String mounth) {
        Cursor c;
        //查询条件 开始 结束的0点时间戳
        long startLongTime = GetDataUtils.getLongTimeByDay(startTime, Config.timeTypeByYear);
        long endLongTime = GetDataUtils.getLongTimeByDay(endTime, Config.timeTypeByYear);
        for (int i = 1; i < diffDays; i++) {
            c = session.getDatabase().rawQuery(getSql(i, startLongTime, endLongTime, diffDays - 2, 0), null);
            data.setApCount(String.valueOf(Integer.valueOf(data.getApCount() == null ? "0" : data.getApCount()) + c.getCount()));
            Log.e("getTimeDataAp", c.getCount() + "");
            c = session.getDatabase().rawQuery(getSql(i, startLongTime, endLongTime, diffDays - 2, 1), null);
            data.setStaCount(String.valueOf(Integer.valueOf(data.getStaCount() == null ? "0" : data.getStaCount()) + c.getCount()));
            Log.e("getTimeDataSat", c.getCount() + "");
            data.setDayTime(TimeUtil.getTime(startLongTime + (i * oneDay), Config.timeTypeByYear));
        }
        SharedPreferencesUtil.putObject(MenuMainActivity.this, mounth, data);
    }

    /**
     * 判断每天第一次打开app，用于网络布控目标数据同步
     *
     * @return true 第一次打开  flase不是
     * @author lish
     * created at 2018-08-22 14:16
     */
    private boolean checkFirst() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        String dayTime = simpleDateFormat.format(date);
        if (TextUtils.isEmpty(SharedPreferencesUtil.getString(MenuMainActivity.this, "daytime"))) {
            SharedPreferencesUtil.putString(MenuMainActivity.this, "daytime", dayTime);
            return true;
        } else if (!SharedPreferencesUtil.getString(MenuMainActivity.this, "daytime").equals(dayTime)) {
            SharedPreferencesUtil.putString(MenuMainActivity.this, "daytime", dayTime);
            return true;
        }
        return false;
    }

    private void checkIsLogin() {
        String password = (String) SharedPreferencesUtils
                .getParam(getBaseContext(), "passwordOfApp", "null");
        if (password.equals("null")) {
            startActivity(new Intent(MenuMainActivity.this, LoginActivity.class));
            finish();
        } else {
            initBleActivity();
        }
    }

    private void initService() {
        //启动服务
        startService(new Intent(MenuMainActivity.this, BleService.class));
    }

    private void listenConnect() {
        if (MyApplication.connectStation) {
            mToolbar.setBackgroundColor(getResources().getColor(R.color.primary));
            mToolbar.setBackgroundColor(getResources().getColor(R.color.gray));
            bluestatu.setTextColor(ContextCompat.getColor(MenuMainActivity.this, R.color.primary));
            bluestatu.setText("已连接");
            if (MyApplication.ftpConnect) {
                uploadText.setText("已连接");
                uploadText.setTextColor(ContextCompat.getColor(MenuMainActivity.this, R.color.primary));
            } else {
                uploadText.setText("连接异常");
                uploadText.setTextColor(ContextCompat.getColor(MenuMainActivity.this, R.color.statu_red));
            }
            //每天第一次打开app，同步网络布控目标
            if (checkFirst()) {
                //同步网络布控目标
                getTargets();
                //统计每月ap/终端采集数
                mounthCollectData();
            }
        } else {
            mToolbar.setBackgroundColor(getResources().getColor(R.color.gray));
            mToolbar.setBackgroundColor(getResources().getColor(R.color.gray));
            bluestatu.setTextColor(ContextCompat.getColor(MenuMainActivity.this, R.color.text3));
            bluestatu.setText("未连接");
            uploadText.setTextColor(ContextCompat.getColor(MenuMainActivity.this, R.color.text3));
            uploadText.setText("未连接");
        }

        //监听连接状态
        RxBus.getDefault().toObservable(ConnectedEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<ConnectedEvent>() {
                    @Override
                    public void onNext(ConnectedEvent event) {
                        if (event.isConnected()) {
                            mToolbar.setBackgroundColor(getResources().getColor(R.color.primary));
                            bluestatu.setTextColor(ContextCompat.getColor(MenuMainActivity.this, R.color.primary));
                            bluestatu.setText("已连接");
                            llNotice.setVisibility(View.GONE);
                            setDevText();
                            //重载ap设置的密码
                            Config.reLoadApPassword();
                        } else {
                            mToolbar.setBackgroundColor(getResources().getColor(R.color.gray));
                            bluestatu.setText("连接异常");
                            llNotice.setVisibility(View.VISIBLE);
                            bluestatu.setTextColor(ContextCompat.getColor(MenuMainActivity.this, R.color.statu_red));
                        }
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void FTPEvent(FTPEvent event) {
        if (event.isContent()) {
            uploadText.setText("已连接");
            uploadText.setTextColor(ContextCompat.getColor(MenuMainActivity.this, R.color.primary));
        } else {
            uploadText.setText("连接异常");
            uploadText.setTextColor(ContextCompat.getColor(MenuMainActivity.this, R.color.statu_red));
        }
    }

    private void initBleActivity() {
        if (!BleService.mConnected)                         //未连接跳转到蓝牙连接页面
            startActivity(new Intent(this, BLEActivity.class));
        else {
            mToolbar.setBackgroundColor(getResources().getColor(R.color.primary));
            bluestatu.setTextColor(ContextCompat.getColor(MenuMainActivity.this, R.color.primary));
            bluestatu.setText("已连接");
        }
    }

    private void initTool() {

        if (mToolbar != null & MyApplication.getAppVersion() == Config.C_MINI) {
            mToolbar.setTitle(BuildConfig.appTitle);
            rlUpload.setVisibility(View.VISIBLE);
        } else if (mToolbar != null & MyApplication.getAppVersion() == Config.C_PLUS) {
            mToolbar.setTitle(R.string.app_name_cplus);
        } else if (mToolbar != null & MyApplication.getAppVersion() == Config.C) {
            mToolbar.setTitle(R.string.app_name_c);
        } else if (mToolbar != null) {
            mToolbar.setTitle(AppVersionConfig.title);
        }
        setDevText();

        setSupportActionBar(mToolbar);

        RxBus.getDefault().toObservable(AppVersionEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<AppVersionEvent>() {
                    @Override
                    public void onNext(AppVersionEvent event) {
                        //更新界面 MIni显示上传按钮
                        if (mToolbar != null & event.getAppVersion() == Config.C_MINI) {
                            mToolbar.setTitle(BuildConfig.appTitle);
                            rlUpload.setVisibility(View.VISIBLE);
                            //保活线程
                            LiveService.toLiveService(MenuMainActivity.this);
                            startService(new Intent(MenuMainActivity.this, LocationService.class));
                            startService(new Intent(MenuMainActivity.this, UploadService.class));
                        } else if (mToolbar != null & event.getAppVersion() == Config.C_PLUS) {
                            mToolbar.setTitle(R.string.app_name_cplus);
                            rlUpload.setVisibility(View.INVISIBLE);
                        } else if (mToolbar != null & event.getAppVersion() == -1) {
                            mToolbar.setTitle("请连接本app专用设备蓝牙");
                            rlUpload.setVisibility(View.INVISIBLE);
                        } else if (mToolbar != null & MyApplication.getAppVersion() == Config.C) {
                            mToolbar.setTitle(R.string.app_name_c);
                            rlUpload.setVisibility(View.INVISIBLE);
                        } else if (mToolbar != null) {
                            mToolbar.setTitle(AppVersionConfig.title);
                            rlUpload.setVisibility(View.INVISIBLE);
                        }
                        if (event.getAppVersion() != -1) {
                            //每天第一次打开app，同步网络布控目标
                            if (checkFirst()) {
                                //同步网络布控目标
                                getTargets();
                                //统计每月ap/终端采集数
                                mounthCollectData();
                            }
                        }
                        setDevText();
                    }
                });
    }

    /**
     * 删除指定天数前的log日志
     *
     * @author lish
     * created at 2018-08-27 11:35
     */
    private void delectLogData() {
        //获取N天前 时间戳
        long delectTime = TimeUtil.getLastmorning(Config.SAVEDATATIME);

        List<com.ascend.wangfeng.locationbyhand.bean.dbBean.Log> delectLogs =
                daoSession.getLogDao().queryBuilder().where(LogDao.Properties.Ltime.lt(delectTime)).list();
        daoSession.getLogDao().deleteInTx(delectLogs);
    }

    private void setDevText() {
        if (BuildConfig.AppName.equals("便携式移动采集")) {
            List<KaiZhanBean> devs = SharedPreferencesUtil.getList(MenuMainActivity.this, "kaizhan");
            if (devs != null & MyApplication.mDevicdMac != null) {
                for (KaiZhanBean dev : devs) {
                    if (dev.getMac().equals(MyApplication.mDevicdMac)) {
                        devText.setText(dev.getName() + "("
                                + (MyApplication.mDevicdID == null ? "请连接设备" : MyApplication.mDevicdID) + ")");
                    }
                }
            } else {
                devText.setText(MyApplication.mDevicdID == null ? "请连接设备" : MyApplication.mDevicdID);
            }
        } else
            devText.setText("编号:" + (MyApplication.mDevicdID == null ? "请连接设备" : MyApplication.mDevicdID));
    }

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_PHONE_STATE",
            "android.permission.ACCESS_COARSE_LOCATION"};

    /**
     * 获取动态权限
     *
     * @author lish
     * created at 2018-07-18 13:58
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {//判断当前系统是不是Android6.0
            requestRuntimePermissions(PERMISSIONS_STORAGE, new PermissionListener() {
                @Override
                public void granted() {
                    //权限申请通过
                }

                @Override
                public void denied(List<String> deniedList) {
                    //权限申请未通过
                    for (String denied : deniedList) {
//                        if (denied.equals("android.permission.ACCESS_FINE_LOCATION")) {
//                            Toast.makeText(MainActivity.this, "定位失败，请检查是否打开定位权限！", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(MainActivity.this, denied, Toast.LENGTH_SHORT).show();
//                        }
                        Toast.makeText(MenuMainActivity.this, "获取权限失败,部分功能不可使用", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @OnClick({R.id.layout_main, R.id.ll_log, R.id.ll_bukong, R.id.ll_fenxi, R.id.ll_tongji
            , R.id.textView2, R.id.set, R.id.about, R.id.ll_dev})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_main:
                if (!BuildConfig.isManger) {
                    Toast.makeText(MenuMainActivity.this, "权限不足", Toast.LENGTH_SHORT).show();
                    return;
                } else
                    startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.ll_log:
                //监测日志
                if (!BuildConfig.isManger) {
                    Toast.makeText(MenuMainActivity.this, "权限不足", Toast.LENGTH_SHORT).show();
                    return;
                } else
                    startActivity(new Intent(this, NewLogAllActivity.class));
//                dl.close();
                break;
            case R.id.ll_bukong:
                //布控目标
                if (!BuildConfig.isManger) {
                    Toast.makeText(MenuMainActivity.this, "权限不足", Toast.LENGTH_SHORT).show();
                    return;
                } else
                    startActivity(new Intent(this, TargetActivity.class));
//                dl.close();
                break;
            case R.id.ll_fenxi:
                //数据分析
                if (!BuildConfig.isManger) {
                    Toast.makeText(MenuMainActivity.this, "权限不足", Toast.LENGTH_SHORT).show();
                    return;
                } else
                    startActivity(new Intent(this, AnalyseActivity.class));
//                dl.close();
                break;
            case R.id.ll_tongji:
                //统计
                startActivity(new Intent(this, StatisticsActivity.class));
//                dl.close();
                break;
            case R.id.set:
                //设置
                startActivity(new Intent(this, SetActivity.class));
//                dl.close();
                break;
            case R.id.about:
                //关于
                startActivity(new Intent(this, AboutActivity.class));
//                dl.close();
                break;
            case R.id.ll_dev:
                //蓝牙
                startActivity(new Intent(this, BLEActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (runnable != null)
            handler.removeCallbacks(runnable);
        if (mVolRxBus != null) mVolRxBus.unsubscribe();
        NetStatusWatch.getInstance().unRegisiterListener(this);
    }

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if (System.currentTimeMillis() - firstTime > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            } else {
                finish();
//                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 同步网络布控目标
     *
     * @author lish
     * created at 2018-08-21 16:21
     */
    public void getTargets() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.getTargetUrl())
                .build();
        TargetActivity.GetTarget service = retrofit.create(TargetActivity.GetTarget.class);
        Call<ResponseBody> call = service.getTarget();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    List<NoteDo> targets = new ArrayList<>();
                    try {
                        JSONArray array = new JSONArray(response.body().string());
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            NoteDo note = new NoteDo();
                            note.setMac(object.getString("valueStr")
                                    .replaceAll("-", ":")
                                    .toUpperCase());
                            note.setNote(object.getString("name"));
                            note.setType(1);
                            targets.add(note);
                        }
                        NoteDoDeal.saveToSqlite(targets);
                        EventBus.getDefault().post(targets);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("e", e.toString());
                    }
                    if (loadingDialog != null)
                        loadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });

    }

    @Override
    public void onNetStatusChanged(NetworkStatus currNetStatus) {
        if (currNetStatus.equals(NetworkStatus.NETWORK_NONE)) {
            //没有网络
            uploadText.setText("连接异常");
            uploadText.setTextColor(ContextCompat.getColor(MenuMainActivity.this, R.color.statu_red));
        } else {
            uploadText.setText("已连接");
            uploadText.setTextColor(ContextCompat.getColor(MenuMainActivity.this, R.color.primary));
        }
    }
}
