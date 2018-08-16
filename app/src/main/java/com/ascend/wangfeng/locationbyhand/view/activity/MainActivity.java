package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.AppVersionConfig;
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
import com.ascend.wangfeng.locationbyhand.util.versionUpdate.AppVersionUitls;
import com.ascend.wangfeng.locationbyhand.util.LogUtils;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;
import com.ascend.wangfeng.locationbyhand.view.fragment.ApListFragment;
import com.ascend.wangfeng.locationbyhand.view.fragment.StaListFragment;
import com.ascend.wangfeng.locationbyhand.view.service.BleService;
//import com.ascend.wangfeng.locationbyhand.view.service.LocationService;
import com.ascend.wangfeng.locationbyhand.view.service.RestartUtil;
//import com.ascend.wangfeng.locationbyhand.view.service.UploadService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

//
public class MainActivity extends BaseActivity {

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
        return R.layout.activity_main;
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
//        listenConnect();
        initData();
        initTool();
        initStart();
        initBleActivity();
    }

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION"};


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
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
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
        mTab.setupWithViewPager(mViewpager);
    }

    private void initData() {
        String[] titles = new String[]{"AP", "终端"};
        Fragment[] fragments = new Fragment[]{
                new ApListFragment(), new StaListFragment()
        };
        adapter = new TabMainAdapter(getSupportFragmentManager(), 2, titles, fragments, null);
    }

    private void initTool() {

        if (mToolbar != null & MyApplication.getAppVersion() == Config.C_MINI) {
            mToolbar.setTitle(AppVersionConfig.appTitle);
        } else if (mToolbar != null & MyApplication.getAppVersion() == Config.C_PLUS) {
            mToolbar.setTitle(R.string.app_name_cplus);
        }else if (mToolbar != null & MyApplication.getAppVersion() == Config.C) {
            mToolbar.setTitle(R.string.app_name_c);
        }else if (mToolbar != null) {
            mToolbar.setTitle(AppVersionConfig.title);
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        RxBus.getDefault().toObservable(AppVersionEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<AppVersionEvent>() {
                    @Override
                    public void onNext(AppVersionEvent event) {
                        //更新界面 MIni显示上传按钮
                        if (mToolbar != null & event.getAppVersion() == Config.C_MINI) {
                            mToolbar.setTitle(AppVersionConfig.appTitle);
                        } else if (mToolbar != null & event.getAppVersion() == Config.C_PLUS) {
                            mToolbar.setTitle(R.string.app_name_cplus);
                        } else if (mToolbar != null & event.getAppVersion() == -1) {
                            mToolbar.setTitle("请连接本app专用设备蓝牙");
                        } else if (mToolbar != null & event.getAppVersion() == Config.C) {
                            mToolbar.setTitle(R.string.app_name_c);
                        }else if (mToolbar != null) {
                            mToolbar.setTitle(AppVersionConfig.title);
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SetActivity.class));
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
                        if (AppVersionUitls.getVersionNo(MainActivity.this) < appVersion.getData().getVersionCode()) {
                            SharedPreferencesUtils.setParam(MainActivity.this, "appVersion", true);
                            shownUpdataDialog(appVersion.getData().getDes());
                        } else
                            SharedPreferencesUtils.setParam(MainActivity.this, "appVersion", false);
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
                new AlertDialog.Builder(MainActivity.this);
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
                        Toast.makeText(MainActivity.this, "更新失败", Toast.LENGTH_LONG).show();
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

}
