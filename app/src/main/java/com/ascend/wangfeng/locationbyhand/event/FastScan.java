package com.ascend.wangfeng.locationbyhand.event;

/**
 * 作者：lishanhui on 2018-06-29.
 * 描述：快速侦测
 */

public class FastScan {
    public static int Start = 0;//开启快速侦测
    public static int Stop = 1;//停止侦测

    public FastScan(int statu) {
        this.statu = statu;
    }

    private int statu;

    public int getStatu() {
        return statu;
    }

    public void setStatu(int statu) {
        this.statu = statu;
    }
}
