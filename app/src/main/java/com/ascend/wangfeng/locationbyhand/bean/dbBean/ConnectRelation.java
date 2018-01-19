package com.ascend.wangfeng.locationbyhand.bean.dbBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by fengye on 2017/6/27.
 * email 1040441325@qq.com
 */
@Entity
public class ConnectRelation {
    @Id(autoincrement = true)
    private Long id;
    private String ap;
    private String mac;
    private long timeStart;
    private long timeEnd;
    private Integer count;
    public Integer getCount() {
        return this.count;
    }
    public void setCount(Integer count) {
        this.count = count;
    }
    public long getTimeEnd() {
        return this.timeEnd;
    }
    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }
    public long getTimeStart() {
        return this.timeStart;
    }
    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }
    public String getMac() {
        return this.mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getAp() {
        return this.ap;
    }
    public void setAp(String ap) {
        this.ap = ap;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 1118701511)
    public ConnectRelation(Long id, String ap, String mac, long timeStart,
            long timeEnd, Integer count) {
        this.id = id;
        this.ap = ap;
        this.mac = mac;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.count = count;
    }
    @Generated(hash = 488558346)
    public ConnectRelation() {
    }
}
