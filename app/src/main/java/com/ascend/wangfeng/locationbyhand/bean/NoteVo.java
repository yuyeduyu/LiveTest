package com.ascend.wangfeng.locationbyhand.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fengye on 2017/3/14.
 * email 1040441325@qq.com
 * 添加了note的mac对象
 */

public class NoteVo {

    /**
     * mac : 8C:AB:8E:AF:A8:E8
     * note : ascend10
     */

    @SerializedName("mac")
    private String mac;
    @SerializedName("note")
    private String note;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "NoteVo{" +
                "mac='" + mac + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
