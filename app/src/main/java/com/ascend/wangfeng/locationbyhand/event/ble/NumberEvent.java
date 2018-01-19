package com.ascend.wangfeng.locationbyhand.event.ble;

/**
 * Created by fengye on 2017/8/11.
 * email 1040441325@qq.com
 * 设备编号
 */

public class NumberEvent {
    private String num;

    public NumberEvent(String num) {
        this.num = num;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
