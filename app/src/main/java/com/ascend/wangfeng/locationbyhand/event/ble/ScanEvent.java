package com.ascend.wangfeng.locationbyhand.event.ble;

/**
 * Created by fengye on 2017/8/11.
 * email 1040441325@qq.com
 */

public class ScanEvent {
    private boolean scan;

    public boolean isScan() {
        return scan;
    }

    public void setScan(boolean scan) {
        this.scan = scan;
    }

    public ScanEvent(boolean scan) {
        this.scan = scan;
    }
}
