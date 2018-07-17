package com.ascend.wangfeng.locationbyhand.event.ble;

/**
 * 作者：lishanhui on 2018-07-09.
 * 描述：
 */

public class AppVersionEvent {
    private int appVersion;

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    public AppVersionEvent(int appVersion) {
        this.appVersion = appVersion;
    }
}
