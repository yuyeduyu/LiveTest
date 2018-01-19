package com.ascend.wangfeng.locationbyhand.event.ble;

import android.bluetooth.BluetoothDevice;

/**
 * Created by fengye on 2017/8/11.
 * email 1040441325@qq.com
 * 用于向service 发送命令
 */

public class MessageEvent {
    public static final int SCAN_START = 1;
    public static final int SCAN_STOP = 2;
    public static final int CONNECT = 3;
    public static final int SEND_DATA = 0;

    private int message;
    private BluetoothDevice mDevice;
    private String data;
    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public void setDevice(BluetoothDevice device) {
        mDevice = device;
    }

    public MessageEvent(int message) {
        this.message = message;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


}
