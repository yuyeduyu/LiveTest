package com.ascend.wangfeng.locationbyhand.event;

/**
 * Created by fengye on 2017/1/4.
 * email 1040441325@qq.com
 * 响铃事件
 */
public class RingEvent {
    private boolean b;
    public RingEvent(Boolean b) {
        this.b=b;
    }

    public boolean isB() {
        return b;
    }

    public void setB(boolean b) {
        this.b = b;
    }
}
