package com.ascend.wangfeng.locationbyhand.event.ble;

/**
 * 作者：lishanhui on 2018-06-27.
 * 描述：切换工作模式
 */

public class WorkMode {
    public static int SETAPMODE = 1;//升级模式
    public static int SETMOMODE = 0;//采集模式

    public WorkMode(int mode) {
        this.mode = mode;
    }

    private int mode;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
