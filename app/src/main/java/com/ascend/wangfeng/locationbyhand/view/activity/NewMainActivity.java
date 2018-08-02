package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.TabMainAdapter;
import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.SearchEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.AppVersionEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.ConnectedEvent;
import com.ascend.wangfeng.locationbyhand.login.LoginActivity;
import com.ascend.wangfeng.locationbyhand.resultBack.AppVersionBack;
import com.ascend.wangfeng.locationbyhand.util.AppVersionUitls;
import com.ascend.wangfeng.locationbyhand.util.LogUtils;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;
import com.ascend.wangfeng.locationbyhand.view.fragment.ApListFragment;
import com.ascend.wangfeng.locationbyhand.view.fragment.StaListFragment;
import com.ascend.wangfeng.locationbyhand.view.myview.draglayout.DragLayout;
import com.ascend.wangfeng.locationbyhand.view.service.BleService;
import com.ascend.wangfeng.locationbyhand.view.service.RestartUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

//import com.ascend.wangfeng.locationbyhand.view.service.LocationService;
//import com.ascend.wangfeng.locationbyhand.view.service.UploadService;

//
public class NewMainActivity extends BaseActivity {

    @BindView(R.id.dl)
    DragLayout dl;
    @BindView(R.id.uesrName)
    TextView uesrName;
    @BindView(R.id.ll_log)
    LinearLayout llLog;
    @BindView(R.id.ll_bukong)
    LinearLayout llBukong;
    @BindView(R.id.ll_fenxi)
    LinearLayout llFenxi;
    @BindView(R.id.set)
    LinearLayout set;
    @BindView(R.id.about)
    LinearLayout about;
    @BindView(R.id.ll_tongji)
    LinearLayout llTongji;
    private String TAG = getClass().getCanonicalName();    //com.ascend.wangfeng.locationbyhand.view.activity;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.tab)
    TabLayout mTab;

    private TabMainAdapter adapter;  //主界面加载fragment的适配器
    private BroadcastReceiver connectionReceiver;


    @Override
    protected int setContentView() {
        return R.layout.activity_main_new;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.i(TAG, "保存活动注销时 当前活动的相关状态");
        //注销活动时 保存当前的连接状态
//        SharedPreferences.Editor editor = getSharedPreferences("Station" ,MODE_PRIVATE).edit();
//        editor.putBoolean("station",MyApplication.connectStation);
    }

    @Override
    protected void initView() {

        Log.i(TAG, "initView: ");
        initService();
        //initBroadcast();              //监听wifi连接情况
        //initConfig();
        listenConnect();
        initData();
        initTool();
        initStart();
        initDragLayout();
        if (!MyApplication.isDev) {
            checkIsLogin();
        } else {
            initBleActivity();
        }
        //获取动态权限
        getPermissions();
        //版本更新监测
        checkVersion();
    }

    /**
     * 初始化侧拉控件
     */
    private void initDragLayout() {
        dl.setDragListener(new DragLayout.DragListener() {
            @Override
            public void onOpen() {
//                lv.smoothScrollToPosition(new Random().nextInt(30));
            }

            @Override
            public void onClose() {
//				shake();
            }

            @Override
            public void onDrag(float percent) {
//                ViewHelper.setAlpha(ivIcon, 1 - percent);
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
                        Toast.makeText(NewMainActivity.this, "获取权限失败,部分功能不可使用", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void initService() {                            //启动服务
        startService(new Intent(NewMainActivity.this, BleService.class));
    }

    private void listenConnect() {
        if (MyApplication.connectStation)
            mToolbar.setBackgroundColor(getResources().getColor(R.color.primary));
        else
            mToolbar.setBackgroundColor(getResources().getColor(R.color.gray));
        //监听连接状态
        RxBus.getDefault().toObservable(ConnectedEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<ConnectedEvent>() {
                    @Override
                    public void onNext(ConnectedEvent event) {
                        if (event.isConnected()) {
                            mToolbar.setBackgroundColor(getResources().getColor(R.color.primary));
                            //重载ap设置的密码
                            Config.reLoadApPassword();
                        } else {
                            mToolbar.setBackgroundColor(getResources().getColor(R.color.gray));
                        }
                    }
                });
    }

    private void initBleActivity() {
        if (!BleService.mConnected)                         //未连接跳转到蓝牙连接页面
            startActivity(new Intent(this, BLEActivity.class));
        else
            mToolbar.setBackgroundColor(getResources().getColor(R.color.primary));
    }


    /**
     * 监听wifi连接情况
     */
    private void initBroadcast() {
        connectionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager)
                        getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo wifiNetInfo = connectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (!wifiNetInfo.isConnected())
                    mToolbar.setBackgroundColor(getResources().getColor(R.color.gray));
                else initConfig();
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionReceiver, intentFilter);
    }

    private void checkIsLogin() {
        String password = (String) SharedPreferencesUtils
                .getParam(getBaseContext(), "passwordOfApp", "null");
        if (password.equals("null")) {
            startActivity(new Intent(NewMainActivity.this, LoginActivity.class));
            finish();
        } else {
            initBleActivity();
        }
    }

    private void initConfig() {
        AppClient.getWiFiApi().setTime(System.currentTimeMillis())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<String>() {
                               @Override
                               public void onNext(String aBoolean) {
                                   mToolbar.setBackgroundColor(getResources().getColor(R.color.primary));
                               }

                               @Override
                               public void onError(Throwable e) {
                                   super.onError(e);
                                   try {
                                       if (mToolbar == null) {
                                           Log.e(TAG, "onError: mtool ==null");
                                       }
                                       mToolbar.setBackgroundColor(getResources().getColor(R.color.gray));
                                       Snackbar.make(mToolbar, "设备连接异常，请检查设备后重启", Snackbar.LENGTH_LONG)
                                               .setAction("重启", new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       RestartUtil.restartApp(getBaseContext());
                                                   }
                                               })
                                               .show();
                                   } catch (Exception s) {
                                       Log.e("s", s.getMessage());
                                   }
                               }
                           }
                );
    }

    private void initStart() {
        mViewpager.setAdapter(adapter);
        mViewpager.addOnPageChangeListener(adapter);
        mTab.setupWithViewPager(mViewpager);
    }

    private void initData() {
        String[] titles = new String[]{"AP", "终端"};
        Fragment[] fragments = new Fragment[]{
                new ApListFragment(), new StaListFragment()
        };
        adapter = new TabMainAdapter(getSupportFragmentManager(), 2, titles, fragments, dl);
    }

    private void initTool() {

        if (mToolbar != null & MyApplication.getAppVersion() == Config.C_MINI) {
            mToolbar.setTitle(R.string.app_name_mini);
        } else if (mToolbar != null & MyApplication.getAppVersion() == Config.C_PLUS) {
            mToolbar.setTitle(R.string.app_name_cplus);
        } else if (mToolbar != null) {
            mToolbar.setTitle(R.string.app_name);
        }
        uesrName.setText("设备编号:"+(MyApplication.mDevicdID == null ? "未连接" : MyApplication.mDevicdID ));
        setSupportActionBar(mToolbar);

        RxBus.getDefault().toObservable(AppVersionEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<AppVersionEvent>() {
                    @Override
                    public void onNext(AppVersionEvent event) {
                        //更新界面 MIni显示上传按钮
                        if (mToolbar != null & event.getAppVersion() == Config.C_MINI) {
                            mToolbar.setTitle(R.string.app_name_mini);
                        } else if (mToolbar != null & event.getAppVersion() == Config.C_PLUS) {
                            mToolbar.setTitle(R.string.app_name_cplus);
                        } else if (mToolbar != null & event.getAppVersion() == -1) {
                            mToolbar.setTitle("请连接本app专用设备蓝牙");
                        } else if (mToolbar != null) {
                            mToolbar.setTitle(R.string.app_name);
                        }
                        uesrName.setText("设备编号:"+(MyApplication.mDevicdID == null ? "未连接" : MyApplication.mDevicdID ));
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(NewMainActivity.this, SetActivity.class));
        } else if (item.getItemId() == R.id.action_ble) {
            startActivity(new Intent(this, BLEActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_set, menu);
        MenuItem searchItem = menu.findItem(R.id.ab_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //搜索内容监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("onQueryTextSubmit", query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: " + newText);
                RxBus.getDefault().post(new SearchEvent(newText));
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectionReceiver != null) {
            unregisterReceiver(connectionReceiver);
        }
    }

    /**
     * 检测app版本是否需要更新
     *
     * @author lish
     * created at 2018-07-24 11:57
     */
    private void checkVersion() {
        AppClient.getAppVersionApi().getAppVersion("wxldCVersion.txt")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AppVersionBack>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("getAppVersion:", e.toString());
                    }

                    @Override
                    public void onNext(AppVersionBack appVersion) {
                        if (AppVersionUitls.getVersionNo(NewMainActivity.this) < appVersion.getData().getVersionCode()) {
                            SharedPreferencesUtils.setParam(NewMainActivity.this, "appVersion", true);
                            shownUpdataDialog(appVersion.getData().getDes());
                        } else
                            SharedPreferencesUtils.setParam(NewMainActivity.this, "appVersion", false);
                    }
                });
    }

    /**
     * 更新dialog
     *
     * @author lish
     * created at 2018-07-24 12:35
     */
    private void shownUpdataDialog(String des) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(NewMainActivity.this);
        normalDialog.setTitle("版本更新");
        normalDialog.setMessage(des);
        normalDialog.setCancelable(false);
        normalDialog.setPositiveButton("下载安装",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        downApk();
                        dialog.dismiss();
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        dialog.dismiss();
                    }
                });
        // 显示
        normalDialog.show();
    }

    private void downApk() {
        AppClient.getAppVersionApi().updateApp("wxldC.apk")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(NewMainActivity.this, "更新失败", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(ResponseBody body) {
                        Log.i("a", "onNext: ");
                        try {
                            // todo change the file location/name according to your needs
                            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                                    + "/AscendLog/LocationShow/");
                            File futureStudioIconFile = new File(dir, "location.apk");

                            InputStream inputStream = null;
                            OutputStream outputStream = null;

                            try {
                                byte[] fileReader = new byte[4096];

                                long fileSize = body.contentLength();
                                long fileSizeDownloaded = 0;

                                inputStream = body.byteStream();
                                outputStream = new FileOutputStream(futureStudioIconFile);

                                while (true) {
                                    int read = inputStream.read(fileReader);

                                    if (read == -1) {
                                        break;
                                    }

                                    outputStream.write(fileReader, 0, read);

                                    fileSizeDownloaded += read;

                                }

                                outputStream.flush();
                                Intent intent = new Intent();
                                //执行动作
                                intent.setAction(Intent.ACTION_VIEW);
                                //执行的数据类型
                                intent.setDataAndType(Uri.fromFile(futureStudioIconFile), "application/vnd.android.package-archive");
                                startActivity(intent);

                                return;
                            } catch (IOException e) {
                                return;
                            } finally {
                                if (inputStream != null) {
                                    inputStream.close();
                                }

                                if (outputStream != null) {
                                    outputStream.close();
                                }
                            }
                        } catch (IOException e) {
                            return;
                        }
                    }
                });
    }

    @OnClick({R.id.ll_log, R.id.ll_bukong, R.id.ll_fenxi, R.id.set, R.id.about,R.id.ll_tongji})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_log:
                //监测日志
                startActivity(new Intent(NewMainActivity.this, NewLogAllActivity.class));
//                dl.close();
                break;
            case R.id.ll_bukong:
                //布控目标
                startActivity(new Intent(NewMainActivity.this, TargetActivity.class));
//                dl.close();
                break;
            case R.id.ll_fenxi:
                //数据分析
                startActivity(new Intent(NewMainActivity.this, AnalyseActivity.class));
//                dl.close();
                break;
                case R.id.ll_tongji:
                //统计
                startActivity(new Intent(NewMainActivity.this, StatisticsActivity.class));
//                dl.close();
                break;
            case R.id.set:
                //设置
                startActivity(new Intent(NewMainActivity.this, SetActivity.class));
//                dl.close();
                break;
            case R.id.about:
                //关于
                startActivity(new Intent(NewMainActivity.this, AboutActivity.class));
//                dl.close();
                break;
        }
    }

    //记录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (dl.getStatus().equals(DragLayout.Status.Open)) {
                //如果侧拉界面打开，点击返回则收起侧拉界面
                dl.close();
                return true;
            }
            if (System.currentTimeMillis() - firstTime > 2000) {
                Toast.makeText(NewMainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            } else {
                finish();
//                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
