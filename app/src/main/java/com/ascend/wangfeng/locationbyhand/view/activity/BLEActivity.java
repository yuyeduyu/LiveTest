package com.ascend.wangfeng.locationbyhand.view.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.AppVersionConfig;
import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.BleAdapter;
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.KaiZhanBean;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.ble.AppVersionEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.DeviceEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.MainServiceEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.MessageEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.ScanEvent;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtil;
import com.ascend.wangfeng.locationbyhand.util.versionUpdate.VersionUpdateService;
import com.ascend.wangfeng.locationbyhand.view.service.LocationService;
import com.ascend.wangfeng.locationbyhand.view.service.MainService;
import com.ascend.wxldcmenu.MenuMainActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BLEActivity extends AppCompatActivity {

    private String TAG = getClass().getCanonicalName();
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
                        if (!mDevices.contains(event.getDevice())) {
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
                if (mDevices.get(position).getName() == null) {
                    Toast.makeText(BLEActivity.this, "请连接本APP专用蓝牙设备", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    MyApplication.mDevicdID = mDevices.get(position).getName().trim();
                    MyApplication.mDevicdMac = mDevices.get(position).getAddress();
                    if (AppVersionConfig.appTitle.equals("便携式移动采集")) {
                        startService(new Intent(BLEActivity.this, LocationService.class));
                        if (!mDevices.get(position).getName().trim().startsWith("504")) {
                            Toast.makeText(BLEActivity.this, "请连接本APP专用蓝牙设备", Toast.LENGTH_SHORT).show();
                        } else {
                            //便携式移动采集
                            isKaiZhan(mDevices.get(position));
                        }
                    } else {
                        connectBLE(mDevices.get(position));
                        finish();
                    }
                }
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

    /**
     * 连接蓝牙
     *
     * @param bluetoothDevice
     * @author lish
     * created at 2018-08-22 14:35
     */
    private void connectBLE(BluetoothDevice bluetoothDevice) {
        RxBus.getDefault().post(new MainServiceEvent(MainServiceEvent.CLEAE_DATA));
        stopService(new Intent(BLEActivity.this, MainService.class));
//                stopService(new Intent(BLEActivity.this, UploadService.class));
        MessageEvent event = new MessageEvent(MessageEvent.CONNECT);
        event.setDevice(bluetoothDevice);
        RxBus.getDefault().post(event);
        if (bluetoothDevice.getName() != null) {
            setAppVersion(bluetoothDevice.getName().trim());
        } else {
            RxBus.getDefault().post(new AppVersionEvent(-1));
            MyApplication.mDevicdID = "";
        }
    }

    /**
     * 如果设备没有开站信息 则跳转开站界面
     *
     * @param bluetoothDevice
     * @author lish
     * created at 2018-08-10 11:52
     */
    private void isKaiZhan(BluetoothDevice bluetoothDevice) {
        List<KaiZhanBean> devs = SharedPreferencesUtil.getList(BLEActivity.this
                , "kaizhan");
        if (devs == null)
            //跳转开站界面
            startActivity(new Intent(BLEActivity.this, KaiZhanActivity.class));
        else {
            boolean isKaiZhan = false;
            for (KaiZhanBean dev : devs) {
                if (dev.getMac().equals(bluetoothDevice.getAddress()))
                    isKaiZhan = true;
            }
            if (!isKaiZhan) {
                //手机没有设备开站数据，则提醒开站
                shownKaizhanDialog(this);
            } else {
                connectBLE(bluetoothDevice);
                finish();
            }
        }
    }

    private void scanDevice() {
        RxBus.getDefault().post(new MessageEvent(MessageEvent.SCAN_START));
        mSwipe.setRefreshing(true);
    }

    /**
     * 根据设备号，判断app版本
     *
     * @param num 5开头为nimi版
     * @author lishanhui
     * created at 2018-07-09 9:23
     */
    private void setAppVersion(String num) {
// p01c对应的编号前几位p01c201，
// p01cplus对应的编号前几位p01c502,
// p01cmini对应的编号前几位504

        if (num == null)
            return;
        int appVersion = Config.C;
        if (num.startsWith("504")) {
            //mini
            appVersion = Config.C_MINI;

        } else if (num.startsWith("P01C502")) {
            //cplus
            appVersion = Config.C_PLUS;
        } else if (num.startsWith("P01C201") || num.startsWith("P01C17")) {
            //C
            appVersion = Config.C;
        }
//        toast(num+"---->"+appVersion);
        MyApplication.setAppVersion(appVersion);
        //通知更改界面aplistFragment stalistFragment
        RxBus.getDefault().post(new AppVersionEvent(appVersion));
    }

    /**
     * 开站确认dialog
     *
     * @author lish
     * created at 2018-08-22 15:11
     */
    public static void shownKaizhanDialog(final Context context) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setTitle("提交开站信息");
        normalDialog.setMessage("手机中没有该设备开站信息,是否提交该设备开站信息");
        normalDialog.setCancelable(false);
        normalDialog.setPositiveButton("确认",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //跳转开站界面
                        context.startActivity(new Intent(context, KaiZhanActivity.class));
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        // 显示
        normalDialog.show();
    }
}
