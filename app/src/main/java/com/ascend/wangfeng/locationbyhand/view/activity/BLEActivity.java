package com.ascend.wangfeng.locationbyhand.view.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.BleAdapter;
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.ble.DeviceEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.MainServiceEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.MessageEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.ScanEvent;
import com.ascend.wangfeng.locationbyhand.view.service.MainService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BLEActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.list)
    RecyclerView mList;
    @BindView(R.id.activity_ble)
    LinearLayout mActivityBle;
    @BindView(R.id.swipe)
    SwipeRefreshLayout mSwipe;
    private ArrayList<BluetoothDevice> mDevices;
    private BleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        ButterKnife.bind(this);
        initData();
        initView();
        scanDevice();
        initScanListener();
    }

    private void initScanListener() {
        RxBus.getDefault().toObservable(ScanEvent.class)
                .subscribe(new BaseSubcribe<ScanEvent>() {
                    @Override
                    public void onNext(ScanEvent event) {
                        mSwipe.setRefreshing(false);
                    }
                });
        RxBus.getDefault().toObservable(DeviceEvent.class)
                .subscribe(new BaseSubcribe<DeviceEvent>() {
                    @Override
                    public void onNext(DeviceEvent event) {
                        if (!mDevices.contains(event.getDevice())){
                            mDevices.add(event.getDevice());
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void initData() {
        mDevices = new ArrayList<>();
        adapter = new BleAdapter(mDevices);
    }

    private void initView() {
        mToolbar.setTitle("搜索设备");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        adapter.setOnItemListener(new BleAdapter.OnItemListener() {
            @Override
            public void onClick(View view, int position) {
                RxBus.getDefault().post(new MainServiceEvent(MainServiceEvent.CLEAE_DATA));
                stopService(new Intent(BLEActivity.this, MainService.class));
                MessageEvent event = new MessageEvent(MessageEvent.CONNECT);
                event.setDevice(mDevices.get(position));
                RxBus.getDefault().post(event);
                finish();
            }
        });
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setAdapter(adapter);
        mList.addItemDecoration(new MyItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scanDevice();
            }
        });
    }

    private void scanDevice() {
        RxBus.getDefault().post(new MessageEvent(MessageEvent.SCAN_START));
        mSwipe.setRefreshing(true);
    }
}
