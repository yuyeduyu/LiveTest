package com.ascend.wangfeng.locationbyhand.bean;

import java.io.Serializable;

/**
 * 作者：lish on 2018-08-24.
 * 描述：最近三个月采集ap 终端数
 */

public class MounthCollectData implements Serializable{
    private String apCount;
    private String staCount;
    private String dayTime;

    public String getApCount() {
        return apCount;
    }

    public void setApCount(String apCount) {
        this.apCount = apCount;
    }

    public String getStaCount() {
        return staCount;
    }

    public void setStaCount(String staCount) {
        this.staCount = staCount;
    }

    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }
}
