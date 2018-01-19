package com.ascend.wangfeng.locationbyhand.event.ble;

import com.ascend.wangfeng.locationbyhand.bean.Ghz;

/**
 * Created by fengye on 2017/8/30.
 * email 1040441325@qq.com
 */

public class GhzEvent {
    private Ghz mGhz;

    public Ghz getGhz() {
        return mGhz;
    }

    public void setGhz(Ghz ghz) {
        mGhz = ghz;
    }

    public GhzEvent(Ghz ghz) {
        mGhz = ghz;
    }
}
