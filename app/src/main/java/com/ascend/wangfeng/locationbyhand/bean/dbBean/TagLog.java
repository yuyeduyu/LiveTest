package com.ascend.wangfeng.locationbyhand.bean.dbBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by fengye on 2017/6/27.
 * email 1040441325@qq.com
 * 布控目标日志
 */
@Entity
public class TagLog {
    @Id(autoincrement = true)
    private Long id;
    private String mac;
    private int distance;//距离
    private double latitude;
    private double longitude;
    private String note;//备注
    private long ltime;//最后更新时间
    private int appVersion;//不同版本区分
    /**
     * 0:ap, 1:sta
     */
    private int type;
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getAppVersion() {
        return this.appVersion;
    }
    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }
    public long getLtime() {
        return this.ltime;
    }
    public void setLtime(long ltime) {
        this.ltime = ltime;
    }
    public String getNote() {
        return this.note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public double getLongitude() {
        return this.longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLatitude() {
        return this.latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public int getDistance() {
        return this.distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public String getMac() {
        return this.mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 73201728)
    public TagLog(Long id, String mac, int distance, double latitude,
            double longitude, String note, long ltime, int appVersion, int type) {
        this.id = id;
        this.mac = mac;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
        this.note = note;
        this.ltime = ltime;
        this.appVersion = appVersion;
        this.type = type;
    }
    @Generated(hash = 527245880)
    public TagLog() {
    }
}
