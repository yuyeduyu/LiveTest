package com.ascend.wangfeng.locationbyhand.event.ble;

import android.bluetooth.BluetoothDevice;

/**
 * Created by fengye on 2017/8/11.
 * email 1040441325@qq.com
 */

public class DeviceEvent {
    private BluetoothDevice mDevice;

    public DeviceEvent(BluetoothDevice device) {
        mDevice = device;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public void setDevice(BluetoothDevice device) {
        mDevice = device;
    }
}
