package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.AlarmMacListDo;
import com.ascend.wangfeng.locationbyhand.bean.Ghz;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.ble.MainServiceEvent;
import com.ascend.wangfeng.locationbyhand.util.ChannelConvert;
import com.ascend.wangfeng.locationbyhand.view.fragment.FormAsTargetFragment;
import com.ascend.wangfeng.locationbyhand.view.fragment.LineFragment;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChartActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.chart_frame)
    FrameLayout mChartFrame;
    private String TAG = getClass().getCanonicalName();

    private boolean Tag;// 是否是布控目标
    private  Bundle bundle;
    @Override
    protected int setContentView() {
        return R.layout.activity_chart;
    }

    @Override
    protected void initView() {
        initTool();

        init();
        initFrame();
    }

    private void init() {
        Intent intent = getIntent();
        String mac = intent.getStringExtra("mac");
        int type = intent.getIntExtra("type", 0);
        int channel = intent.getIntExtra("channel", 0);
        Tag = intent.getBooleanExtra("tag", false);
        bundle = new Bundle();
        bundle.putBoolean("tag", Tag);
        MainServiceEvent event = new MainServiceEvent(MainServiceEvent.LOCK);
        if (MyApplication.mGhz == Ghz.G24) {
            event.setChannel(channel);
        } else {
            event.setChannel(ChannelConvert.convertChannel(channel));
        }
        event.setMac(mac);
        event.setType(type);
        RxBus.getDefault().post(event);
        Log.i(TAG, "init: " + channel);
    }

    private void initFrame() {
        Fragment fragment = new LineFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.chart_frame, fragment)
                .commit();
    }

    /**
     * 初始化 toolbar 侧滑菜单
     */
    private void initTool() {
        mToolbar.setTitle(R.string.chart);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_chart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_line:
                goFragment(0);
                return true;
            case R.id.action_form:
                goFragment(2);
        }
        return super.onOptionsItemSelected(item);
    }

    private void goFragment(int i) {
        Fragment fragment;
        switch (i) {
            case 0:
                fragment = new LineFragment();
                fragment.setArguments(bundle);
                break;

            case 2:
                fragment = new FormAsTargetFragment();
                break;
            default:
                fragment = new LineFragment();
                fragment.setArguments(bundle);
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.chart_frame, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        goBack();
        super.onBackPressed();
    }

    private void goBack() {
        finish();
        AppClient.getWiFiApi().getAlarmMacList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<AlarmMacListDo>() {
                    @Override
                    public void onNext(AlarmMacListDo aDo) {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().post(new MainServiceEvent(MainServiceEvent.UNLOCK));
    }
}
