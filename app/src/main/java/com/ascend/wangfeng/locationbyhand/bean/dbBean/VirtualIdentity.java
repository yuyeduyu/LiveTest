package com.ascend.wangfeng.locationbyhand.bean.dbBean;

/**
 * Created by fengye on 2018/2/23.
 * email 1040441325@qq.com
 * 暂未使用
 */

public class VirtualIdentity {
    private String mac;
    private int type;
    private String identity;
    private Long time;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
