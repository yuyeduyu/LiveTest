package com.ascend.wangfeng.locationbyhand.view.service;

import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.Ghz;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.event.FastScan;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.ble.ConnectedEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.DeviceEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.GhzEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.MacData;
import com.ascend.wangfeng.locationbyhand.event.ble.MessageEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.ScanEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.VolEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.WorkMode;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;
import com.ascend.wangfeng.locationbyhand.util.ble.BLEDataUtil;
import com.ascend.wangfeng.locationbyhand.util.ble.BluetoothLeClass;
import com.ascend.wangfeng.locationbyhand.util.ble.Utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by fengye on 2017/8/10.
 * email 1040441325@qq.com
 */

public class BleService extends Service implements BluetoothAdapter.LeScanCallback,
        BluetoothLeClass.OnConnectListener, BluetoothLeClass.OnDisconnectListener,
        BluetoothLeClass.OnServiceDiscoverListener, BluetoothLeClass.OnDataAvailableListener {
    private final static String TAG = BleService.class.getSimpleName();
    private final static String UUID_KEY_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";
    private static final long SCAN_PERIOD = 10000;
    private static final byte SEPARATOR_START = 0x1a;
    private static final byte SEPARATOR_END = 0x1b;
    private static final String SEPARATOR_ROW = "\r\n";
    private static final String SEPARATOR_ELEMENT = ",";
    private BluetoothAdapter mBleAdapter;
    private BluetoothLeClass mBLE;
    private boolean mIsScan;//是否正在扫描BLE
    public static boolean mConnected;//是否处于连接中
    private BluetoothGattCharacteristic gattCharacteristic;
    private List<Byte> mList = new ArrayList<>();
    private Runnable mScanRunable;
    private Handler mScanHandle;
    private String adress;//当前连接的adress
    private Handler mConnectHandler;
    private java.lang.Runnable mConnectRunable;

    private LinkedBlockingQueue<String> mStringLinkedBlockingQueue = new LinkedBlockingQueue();
    /**
     * 是否已经关闭
     */
    private volatile boolean mIsShutdown = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initBle();
        initScan();
        initCMD();
        initWriteThread();
        //initTest();
        //scanDevice(true);
        //mBLE.connect(address);
    }

    // 初始化写线程
    private void initWriteThread() {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        pool.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (gattCharacteristic != null && !mStringLinkedBlockingQueue.isEmpty()) {
                        gattCharacteristic.setValue(mStringLinkedBlockingQueue.poll());
                        mBLE.writeCharacteristic(gattCharacteristic);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void initTest() {//发送测试数据
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                sendData("END");
                handler.postDelayed(this, 2000);
            }
        };
        handler.post(runnable);
    }

    private void initScan() {
        mScanHandle = new Handler();
        mScanRunable = new Runnable() {

            @Override
            public void run() {
                stopScan();
            }
        };
        mConnectHandler = new Handler();
        mConnectRunable = new Runnable() {
            @Override
            public void run() {
                if (mBLE != null && adress != null) {
                    mBLE.connect(adress);
                    mConnectHandler.postDelayed(mConnectRunable, 2000);
                }
            }
        };

    }

    private void initCMD() {
        RxBus.getDefault().toObservable(MessageEvent.class)
                .subscribe(new BaseSubcribe<MessageEvent>() {
                    @Override
                    public void onNext(MessageEvent event) {
                        switch (event.getMessage()) {
                            case MessageEvent.SCAN_START:
                                scanDevice(true);
                                break;
                            case MessageEvent.SCAN_STOP:
                                scanDevice(false);
                                break;
                            case MessageEvent.CONNECT:
                                // before connect ,clear connected
                                adress = null;
                                mBLE.disconnect();
                                adress = event.getDevice().getAddress();
                                mBLE.connect(adress);
                                break;
                            case MessageEvent.SEND_DATA:
                                sendData(event.getData());
                                break;
                        }
                    }
                });
    }

    private void initBle() {
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBleAdapter = manager.getAdapter();
        if (mBleAdapter == null) {
            Toast.makeText(this, R.string.ble_support_no, Toast.LENGTH_SHORT).show();
        }
        mBleAdapter.enable();
        mBLE = new BluetoothLeClass(this);
        if (!mBLE.initialize()) {
            Log.e(TAG, "Unable to initialize Bluetooth");
        }
        //BLE连接时回调
        mBLE.setOnConnectListener(this);
        //BLE断开时回调
        mBLE.setOnDisconnectListener(this);
        //发现BLE终端的Service时回调
        mBLE.setOnServiceDiscoverListener(this);
        //收到BLE终端数据交互的事件
        mBLE.setOnDataAvailableListener(this);

    }

    /**
     * @param enable 扫描BLE 开启,关闭
     */
    public void scanDevice(boolean enable) {
        Log.i(TAG, "scanDevice: ");
        if (enable == mIsScan) return;
        if (enable) {
            mIsScan = true;
            mScanHandle.postDelayed(mScanRunable, SCAN_PERIOD);
            mBleAdapter.startLeScan(BleService.this);
        } else {
            mScanHandle.removeCallbacks(mScanRunable);
            stopScan();
        }
    }

    private void stopScan() {
        mIsScan = false;
        mBleAdapter.stopLeScan(BleService.this);
        RxBus.getDefault().post(new ScanEvent(false));
    }

    /**
     * 弹出重连窗口
     */
    @Deprecated
    private void alert() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                final AlertDialog dialog = new AlertDialog.Builder(BleService.this).setTitle("重新连接")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface anInterface, int i) {
                                //重连功能
                                mBLE.connect(adress);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface anInterface, int i) {
                            }
                        })
                        .create();
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                dialog.show();
            }
        });

    }

    @Override
    public void onLeScan(BluetoothDevice device, int i, byte[] bytes) {
        //扫描蓝牙回调
        RxBus.getDefault().post(new DeviceEvent(device));
    }

    @Override
    public void onConnect(BluetoothGatt gatt) {
        mConnected = true;
        RxBus.getDefault().post(new ConnectedEvent(true));
        mConnectHandler.removeCallbacks(mConnectRunable);
        toast("连接成功");
    }

    private void toast(final String message) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                handler.removeCallbacks(this);
            }
        });
    }

    @Override
    public void onDisconnect(BluetoothGatt gatt) {
        mConnected = false;
        gattCharacteristic = null;
        toast(getString(R.string.hint_disconnect));

        // when diconnected, reconnect
        if (adress != null) {
            mBLE.connect(adress);
            // mConnectHandler.post(mConnectRunable);
        }
        RxBus.getDefault().post(new ConnectedEvent(false));
    }

    @Override
    public void onServiceDiscover(BluetoothGatt gatt) {
        displayGattSercices(gatt.getServices());
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            //byte数组,包装类,转化集合;组合
            try {
                String testdata = new String(characteristic.getValue(), "UTF-8");
                Log.i(TAG, "formBLE: " + testdata);
                //返回 1111,SETAPMODE  升级模式  1212,SETMOMODE采集模式  格式不固定，所以单独处理
                //释放布控返回 1010,STOPBK
                if (testdata.indexOf("SETAPMODE") > -1) {
                    //转换升级模式成功
                    RxBus.getDefault().post(new WorkMode(WorkMode.SETAPMODE));
                } else if (testdata.indexOf("SETMOMODE") > -1) {
                    //转换采集模式成功
                    RxBus.getDefault().post(new WorkMode(WorkMode.SETMOMODE));
                } else if (testdata.indexOf("STOPBK") > -1) {
                    //停止快速侦测
                    RxBus.getDefault().post(new FastScan(FastScan.Stop));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            byte[] bytes = characteristic.getValue();
            Byte[] bytes1 = new Byte[bytes.length];
            Log.i(TAG, "onCharacteristicRead: " + mList.toString());
            for (int i = 0; i < bytes.length; i++) {
                bytes1[i] = bytes[i];
            }
            List<Byte> byteList = Arrays.asList(bytes1);
            mList.addAll(byteList);

            int start = mList.lastIndexOf(SEPARATOR_START);
            int end = mList.lastIndexOf(SEPARATOR_END);
            if (start == -1 || end == -1) return;
            List<Byte> results = new ArrayList<>();
            for (int i = start + 1; i < end; i++) {//首尾的标识符不需要
                results.add(mList.get(i));
            }
            for (int i = end; i > start - 1; i--) {//保存的数据中删除取出元素,并去除间隔符
                mList.remove(i);
            }
            Byte[] resultBytes2 = results.toArray(new Byte[results.size()]);
            byte[] resultBytes1 = new byte[resultBytes2.length];
            for (int i = 0; i < resultBytes2.length; i++) {
                resultBytes1[i] = resultBytes2[i];
            }
            String request = null;
            try {
                request = new String(resultBytes1, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (request == null || request.length() <= 0) return;
            format(request);//处理数据并发送
            Log.i(TAG, "onCharacteristicAfte: " + request);
        }
    }

    private void format(String request) {
        Integer a = formatInt(request.substring(0, 1));
        Log.e(TAG, "format: " + a + "\n" + request);
        switch (a) {
            case 1://sta
                Log.i(TAG, "case: 1");
                MacData data = collectMac(request);
                RxBus.getDefault().post(data);
                break;
            case 3://电量
                int vol = getVol(request);
                RxBus.getDefault().post(new VolEvent(vol));
                Log.i(TAG, "case: 3");
                break;
            case 4://编号
                String num = getNumber(request);
                MyApplication.mDevicdID = num;
                //获取编号后,获取频段
                sendData("GETMOD");
                Log.i(TAG, "case: 4");
                break;
            case 5://频段
                Integer ghz = getGhz(request);
                MyApplication.setIsDataRun(true);
                if (ghz == 1) {
                    MyApplication.mGhz = Ghz.G24;
                    RxBus.getDefault().post(new GhzEvent(Ghz.G24));
                } else {
                    MyApplication.mGhz = Ghz.G58;
                    RxBus.getDefault().post(new GhzEvent(Ghz.G58));
                }
                break;
            case 6://set ap's name success
                if (isApNameSuccess(request)) {
                    String password = (String) SharedPreferencesUtils.getParam(MyApplication.mContext,
                            "pa_ap_password", "");
                    sendData("PASSWD:" + password);
                } else {
                    Config.clearApPassword();
                    toast(getResources().getString(R.string.set_passwor_error));
                }
                break;
            case 7: // set ap's password success
                if (isApPasswordSuccess(request)) {
                    toast(getResources().getString(R.string.set_password_success));
                } else {
                    Config.clearApPassword();
                    toast(getResources().getString(R.string.set_password_success));
                }
                break;
            case 8:
                toast(getResources().getString(R.string.delte_password));
                break;
            case 9:
                RxBus.getDefault().post(new FastScan(FastScan.Start));
                break;
            case 10:
                toast(10 + "");
                break;
        }
    }

    private boolean isApPasswordSuccess(String request) {
        String[] rows = request.split(SEPARATOR_ROW);
        if (rows.length > 1) {
            String[] eles = rows[1].split(SEPARATOR_ELEMENT);
            String result = eles[1];
            if (result.equals(SharedPreferencesUtils.getParam(MyApplication.mContext, "pa_ap_password", ""))) {
                return true;
            }
        }
        return false;
    }

    private boolean isApNameSuccess(String request) {
        String[] rows = request.split(SEPARATOR_ROW);
        if (rows.length > 1) {
            String[] eles = rows[1].split(SEPARATOR_ELEMENT);
            String result = eles[1];
            if (result.equals(SharedPreferencesUtils.getParam(MyApplication.mContext, "pa_ap_name", ""))) {
                return true;
            }
        }
        return false;
    }

    private String getNumber(String request) {
        String[] rows = request.split(SEPARATOR_ROW);
        //第二行数据
        String[] elements = rows[1].split(SEPARATOR_ELEMENT);
        String num = elements[1];
        return num;
    }

    private Integer getGhz(String request) {
        String[] rows = request.split(SEPARATOR_ROW);
        //第二行数据
        String[] elements = rows[1].split(SEPARATOR_ELEMENT);
        int num = formatInt(elements[1]);
        return num;
    }

    private MacData collectMac(String request) {
        long time = System.currentTimeMillis();
        List<ApVo> aps = new ArrayList<>();
        List<StaVo> stas = new ArrayList<>();
        String[] rows = request.split(SEPARATOR_ROW);
        for (int i = 1; i < rows.length; i++) {
            String row = rows[i];
            int a = formatInt(row.substring(0, 1));
            String[] elements = row.split(SEPARATOR_ELEMENT);
            switch (a) {
                case 1://sta
                    StaVo sta = new StaVo();
                    sta.setMac(elements[1]);
                    sta.setApmac(elements[2]);
                    sta.setSignal(formatInt(elements[3]));
                    sta.setLtime(time);
                    stas.add(sta);
                    break;
                case 2://ap
                    ApVo ap = new ApVo();
                    ap.setChannel(formatInt(elements[1]));
                    ap.setBssid(elements[2]);
                    ap.setEssid(elements[3]);
                    ap.setSignal(formatInt(elements[4]));
                    ap.setLtime(time);
                    aps.add(ap);
                    break;
                case 3:// 虚拟身份
                    Integer type = formatInt(elements[1]);
                    String mac = elements[2];
                    String identity = elements[4];
                    for (int j = 0; j < stas.size(); j++) {
                        if (stas.get(j).getMac().equals(mac)) {
                            stas.get(j).addIdentity(type, identity);
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return new MacData(aps, stas);
    }

    public int getVol(String request) {
        String[] rows = request.split(SEPARATOR_ROW);
        //第二行数据
        String[] elements = rows[1].split(SEPARATOR_ELEMENT);
        int vol = formatInt(elements[1]);
        return vol;
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

    }

    private void displayGattSercices(List<BluetoothGattService> services) {
        if (services == null) return;
        for (BluetoothGattService gattService : services) {
            //-----Service的字段信息-----//
            int type = gattService.getType();
            Log.e(TAG, "-->service type:" + Utils.getServiceType(type));
            Log.e(TAG, "-->includedServices size:" + gattService.getIncludedServices().size());
            Log.e(TAG, "-->service uuid:" + gattService.getUuid());

            //-----Characteristics的字段信息-----//
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                Log.e(TAG, "---->char uuid:" + gattCharacteristic.getUuid());

                int permission = gattCharacteristic.getPermissions();
                Log.e(TAG, "---->char permission:" + Utils.getCharPermission(permission));

                int property = gattCharacteristic.getProperties();
                Log.e(TAG, "---->char property:" + Utils.getCharPropertie(property));

                byte[] data = gattCharacteristic.getValue();
                if (data != null && data.length > 0) {
                    Log.e(TAG, "---->char value:" + new String(data));
                }
                if (gattCharacteristic.getUuid().toString().equals(UUID_KEY_DATA)) {
                    this.gattCharacteristic = gattCharacteristic;
                    mBLE.setCharacteristicNotification(gattCharacteristic, true);
                    //读写uuid获取;获取设备id,获取当前频段
                    sendData("GETMAC");
                    mList.clear();
                    startService(new Intent(BleService.this, MainService.class));
                }
                //-----Descriptors的字段信息-----//
                List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic.getDescriptors();
                for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
                    Log.e(TAG, "-------->desc uuid:" + gattDescriptor.getUuid());
                    int descPermission = gattDescriptor.getPermissions();
                    Log.e(TAG, "-------->desc permission:" + Utils.getDescPermission(descPermission));

                    byte[] desData = gattDescriptor.getValue();
                    if (desData != null && desData.length > 0) {
                        Log.e(TAG, "-------->desc value:" + new String(desData));
                    }
                }
            }
        }
    }

    private void sendData(String value) {
        //Ble一次性最多发送20个字节
        if (value.length() > 18) {
            //分包发送
            MyApplication.setIsDataRun(false);
            sendBigData(value);
        } else {
            mStringLinkedBlockingQueue.offer(value);
        }
        Log.e(TAG, "sendData: " + value);
       /* if (gattCharacteristic != null) {
            gattCharacteristic.setValue(value);
            mBLE.writeCharacteristic(gattCharacteristic);
        }*/
    }

    /**
     * 蓝牙传送大于18个字节数据
     *
     * @author lishanhui
     * created at 2018-06-29 11:12
     */
    private void sendBigData(String data) {
        byte[][] packets = BLEDataUtil.encode(data);
        int tryCount = 3;//最多重发次数
        boolean sendSuccess = true;
        while (--tryCount > 0) {
            sendSuccess = true;
            for (int i = 0; i < packets.length; i++) {
                byte[] bytes = packets[i];
                int onceTryCount = 3;//单次重发次数
                boolean onceSendSuccess = true;
//                BluetoothGattService service = mBLE.getService(UUID.fromString(sServiceUUID));
                if (mBLE != null) {
//                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(sCharacteristicUUID));
                    gattCharacteristic.setValue(bytes);
                    while (--onceTryCount > 0) {

                        if (mIsShutdown) {
                            break;
                        }

                        if (mBLE.writeCharacteristic(gattCharacteristic)) {
                            Log.d("BLE", "Write Success, DATA: " + Arrays.toString(gattCharacteristic.getValue()));
                            onceSendSuccess = true;
                            //避免数据发送太快丢失，需要分包延迟发送
                            SystemClock.sleep(200);
                            break;
                        } else {
                            Log.d("BLE", "Write failed, DATA: " + Arrays.toString(gattCharacteristic.getValue()) + ", and left times = " + onceTryCount);
                            onceSendSuccess = false;
                            //避免数据发送太快丢失，需要分包延迟发送
                            SystemClock.sleep(400);//失败的时候，把时间调大
                        }

                    }

                }

                if (!onceSendSuccess) {//一次发送，重试三次都未成功则，跳出重发这个数据
                    sendSuccess = false;
                    break;
                }

                //避免数据发送太快丢失，需要分包延迟发送
                SystemClock.sleep(200);
            }

            if (sendSuccess) {
                break;
            } else {
                Log.d("BLE", "send msg failed, and try times = " + tryCount);
            }

            if (mIsShutdown) {
                Log.d("BLE", "give up for disconnected by user");
                break;
            }

            //避免数据发送太快丢失，需要分包延迟发送
            SystemClock.sleep(200);
        }
    }

    private int formatInt(String value) {
        int result = 0;
        try {
            result = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Log.e(TAG, "formatInt Error: " + value);
        }
        return result;
    }

    @Override
    public void onDestroy() {
        mScanHandle.removeCallbacks(mScanRunable);
        mConnectHandler.removeCallbacks(mConnectRunable);
        super.onDestroy();
    }
}
