package com.ascend.wangfeng.locationbyhand.event.ble;

/**
 * Created by fengye on 2017/8/11.
 * email 1040441325@qq.com
 * 电量
 */

public class VolEvent {
    private int vol;

    public VolEvent(int vol) {
        this.vol = vol;
    }

    public int getVol() {
        return vol;
    }

    public void setVol(int vol) {
        this.vol = vol;
    }
}
