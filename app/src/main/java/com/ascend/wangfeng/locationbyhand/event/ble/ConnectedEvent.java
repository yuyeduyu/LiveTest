package com.ascend.wangfeng.locationbyhand.event.ble;

/**
 * Created by fengye on 2017/8/12.
 * email 1040441325@qq.com
 */

public class ConnectedEvent {
    private boolean connected;

    public ConnectedEvent(boolean connected) {
        this.connected = connected;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
