package com.ascend.wangfeng.locationbyhand.bean.dbBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by fengye on 2017/8/12.
 * email 1040441325@qq.com
 */
@Entity
public class NoteDo {
    @Id(autoincrement = true)
    private Long id;
    private String note;
    private String mac;
    private boolean ring;
    public boolean getRing() {
        return this.ring;
    }
    public void setRing(boolean ring) {
        this.ring = ring;
    }
    public String getMac() {
        return this.mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getNote() {
        return this.note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 1530939924)
    public NoteDo(Long id, String note, String mac, boolean ring) {
        this.id = id;
        this.note = note;
        this.mac = mac;
        this.ring = ring;
    }
    @Generated(hash = 926683366)
    public NoteDo() {
    }
}
