package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.TabMainAdapter;
import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.SearchEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.ConnectedEvent;
import com.ascend.wangfeng.locationbyhand.login.LoginActivity;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;
import com.ascend.wangfeng.locationbyhand.view.fragment.ApListFragment;
import com.ascend.wangfeng.locationbyhand.view.fragment.StaListFragment;
import com.ascend.wangfeng.locationbyhand.view.service.BleService;
import com.ascend.wangfeng.locationbyhand.view.service.RestartUtil;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.tab)
    TabLayout mTab;
    private String TAG = getClass().getCanonicalName();
    private TabMainAdapter adapter;
    private BroadcastReceiver connectionReceiver;

    @Override
    protected int setContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initService();
        //initBroadcast();
        //initConfig();
        listenConnect();
        initData();
        initTool();
        initStart();

        if (!MyApplication.isDev) {
            checkIsLogin();
        } else {
            initBleActivity();
        }
    }

    private void listenConnect() {
        mToolbar.setBackgroundColor(getResources().getColor(R.color.gray));
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
        if (!BleService.mConnected)
            startActivity(new Intent(this, BLEActivity.class));
    }

    private void initService() {
        startService(new Intent(MainActivity.this, BleService.class));
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
        }else {
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
        adapter = new TabMainAdapter(getSupportFragmentManager(), 2, titles, fragments);
    }

    private void initTool() {
        if (mToolbar != null) {
            mToolbar.setTitle(R.string.app_name);
        }
        setSupportActionBar(mToolbar);
       /* getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SetActivity.class));
        }else if (item.getItemId() ==R.id.action_ble){
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
}
