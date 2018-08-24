package com.ascend.wxldcmenu;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.AppVersionConfig;
import com.ascend.wangfeng.locationbyhand.BuildConfig;
import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.NoteDoDeal;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;
import com.ascend.wangfeng.locationbyhand.event.FTPEvent;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.ble.AppVersionEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.ConnectedEvent;
import com.ascend.wangfeng.locationbyhand.keeplive.LiveService;
import com.ascend.wangfeng.locationbyhand.login.LoginActivity;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtil;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;
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

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 便携式汽车采集app首页
 *
 * @author lish
 *         created at 2018-08-06 11:22
 */
public class MenuMainActivity extends BaseActivity {

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
    @BindView(R.id.loadstatu)
    TextView loadstatu;
    @BindView(R.id.dev_text)
    TextView devText;
    @BindView(R.id.ll_dev)
    RelativeLayout llDev;
    @BindView(R.id.upload_text)
    TextView uploadText;
    @BindView(R.id.rl_upload)
    RelativeLayout rlUpload;

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
        initService();

        listenConnect();
        initTool();
        if (!MyApplication.isDev) {
            checkIsLogin();
        } else {
            initBleActivity();
        }
        //获取动态权限
        getPermissions();
        //版本更新监测
        AppVersionUitls.checkVersion(this, AppVersionConfig.appVersion
                , AppVersionConfig.appName, null, MenuMainActivity.class);
        //每天第一次打开app，同步网络布控目标
        if (checkFirst()){
            getTargets();
        }
    }
    /**
     * 判断每天第一次打开app，用于网络布控目标数据同步
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
        }else if (!SharedPreferencesUtil.getString(MenuMainActivity.this, "daytime").equals(dayTime)){
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
            bluestatu.setBackground(
                    ContextCompat.getDrawable(MenuMainActivity.this, R.drawable.statu_green));
        } else {
            mToolbar.setBackgroundColor(getResources().getColor(R.color.gray));
            mToolbar.setBackgroundColor(getResources().getColor(R.color.gray));
            bluestatu.setBackground(
                    ContextCompat.getDrawable(MenuMainActivity.this, R.drawable.statu_red));
        }

        //监听连接状态
        RxBus.getDefault().toObservable(ConnectedEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<ConnectedEvent>() {
                    @Override
                    public void onNext(ConnectedEvent event) {
                        if (event.isConnected()) {
                            mToolbar.setBackgroundColor(getResources().getColor(R.color.primary));
                            bluestatu.setBackground(
                                    ContextCompat.getDrawable(MenuMainActivity.this, R.drawable.statu_green));
                            //重载ap设置的密码
                            Config.reLoadApPassword();
                        } else {
                            mToolbar.setBackgroundColor(getResources().getColor(R.color.gray));
                            bluestatu.setBackground(
                                    ContextCompat.getDrawable(MenuMainActivity.this, R.drawable.statu_red));
                        }
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void FTPEvent(FTPEvent event) {
        if (event.isContent()) {
            uploadText.setText("");
            loadstatu.setBackground(
                    ContextCompat.getDrawable(MenuMainActivity.this, R.drawable.statu_green));
        } else {
            uploadText.setText("连接服务器失败");
            loadstatu.setBackground(
                    ContextCompat.getDrawable(MenuMainActivity.this, R.drawable.statu_red));
        }
    }

    private void initBleActivity() {
        if (!BleService.mConnected)                         //未连接跳转到蓝牙连接页面
            startActivity(new Intent(this, BLEActivity.class));
        else {
            mToolbar.setBackgroundColor(getResources().getColor(R.color.primary));
            bluestatu.setBackground(
                    ContextCompat.getDrawable(MenuMainActivity.this, R.drawable.statu_green));
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
        devText.setText("编号:" + (MyApplication.mDevicdID == null ? "未连接" : MyApplication.mDevicdID));
        if (MyApplication.ftpConnect)
            loadstatu.setBackground(
                    ContextCompat.getDrawable(MenuMainActivity.this, R.drawable.statu_green));
        else {
            uploadText.setText("连接服务器失败");
            loadstatu.setBackground(
                    ContextCompat.getDrawable(MenuMainActivity.this, R.drawable.statu_red));
        }
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
//                            if (AppVersionConfig.appTitle.equals("便携式移动采集")) {
//                                //便携式移动采集
//                                isKaiZhan();
//                            }
                        } else if (mToolbar != null & event.getAppVersion() == Config.C_PLUS) {
                            mToolbar.setTitle(R.string.app_name_cplus);
                        } else if (mToolbar != null & event.getAppVersion() == -1) {
                            mToolbar.setTitle("请连接本app专用设备蓝牙");
                        } else if (mToolbar != null & MyApplication.getAppVersion() == Config.C) {
                            mToolbar.setTitle(R.string.app_name_c);
                        } else if (mToolbar != null) {
                            mToolbar.setTitle(AppVersionConfig.title);
                        }
                        devText.setText("编号:" + (MyApplication.mDevicdID == null ? "未连接" : MyApplication.mDevicdID));
                    }
                });
    }
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION"};

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
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.ll_log:
                //监测日志
                startActivity(new Intent(this, NewLogAllActivity.class));
//                dl.close();
                break;
            case R.id.ll_bukong:
                //布控目标
                startActivity(new Intent(this, TargetActivity.class));
//                dl.close();
                break;
            case R.id.ll_fenxi:
                //数据分析
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
     * @author lish
     * created at 2018-08-21 16:21
     */
    public void getTargets(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.TargetUrl)
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
                        for (int i=0;i<array.length();i++){
                            JSONObject object = array.getJSONObject(i);
                            NoteDo note = new NoteDo();
                            note.setMac(object.getString("valueStr")
                                    .replaceAll("-",":")
                                    .toUpperCase());
                            note.setNote(object.getString("name"));
                            note.setType(1);
                            targets.add(note);
                        }
                        NoteDoDeal.saveToSqlite(targets);
                        EventBus.getDefault().post(targets);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("e",e.toString());
                    }
                    if (loadingDialog!=null)
                        loadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });

    }
}
