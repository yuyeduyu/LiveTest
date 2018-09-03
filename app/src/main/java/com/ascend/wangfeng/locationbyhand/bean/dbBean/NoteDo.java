package com.ascend.wangfeng.locationbyhand.bean.dbBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * Created by fengye on 2017/8/12.
 * email 1040441325@qq.com
 * 布控目标
 */
@Entity
public class NoteDo implements Serializable {
    @Id(autoincrement = true)
    private Long id;
    private String note;
    private String mac;
    private boolean ring;
    private int type;//0:本地布控，1:网络布控
//          "name": "测试全域2",
//          "valueStr": "48-db-50-85-c0-e2"
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

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Generated(hash = 1310977789)
    public NoteDo(Long id, String note, String mac, boolean ring, int type) {
        this.id = id;
        this.note = note;
        this.mac = mac;
        this.ring = ring;
        this.type = type;
    }

    @Generated(hash = 926683366)
    public NoteDo() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NoteDo) {
            NoteDo bean = (NoteDo) obj;
            return bean.getMac().equals(this.mac)
                    &bean.getType()==this.type;
        }
        return super.equals(obj);
    }
}
