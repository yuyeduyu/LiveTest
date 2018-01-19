package com.ascend.wangfeng.locationbyhand.bean;

/**
 * Created by fengye on 2017/2/7.
 * email 1040441325@qq.com
 */
public class SignalLineChart {
    /**
     * signal : -27
     * ltime : 16:58:49(-27,0,0,0 = 180)
     */

    private int signal;
    private String ltime;

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public String getLtime() {
        return ltime;
    }

    public void setLtime(String ltime) {
        this.ltime = ltime;
    }
}
