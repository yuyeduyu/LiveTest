package com.ascend.wangfeng.locationbyhand.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fengye on 2017/2/8.
 * email 1040441325@qq.com
 * 转化后的ap，添加了tag 是否标注
 */

public class ApVo implements Comparable<ApVo>,Serializable{

    /**
     * bssid : 02:1A:11:F8:BC:43
     * essid : tonxbo
     * num : 3
     * channel : 6
     * signal : -90
     * ltime : 23:15:40
     */

    @SerializedName("bssid")
    private String bssid;
    @SerializedName("essid")
    private String essid;
    @SerializedName("num")
    private int num;
    @SerializedName("channel")
    private int channel;
    @SerializedName("signal")
    private int signal;
    @SerializedName("ltime")
    private long ltime;
    private boolean tag;
    private String note;

    private int stas;

    public int getStas() {
        return stas;
    }

    public void setStas(int stas) {
        this.stas = stas;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getEssid() {
        return essid;
    }

    public void setEssid(String essid) {
        this.essid = essid;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public long getLtime() {
        return ltime;
    }

    public void setLtime(long ltime) {
        this.ltime = ltime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "Ap{" +
                "bssid='" + bssid + '\'' +
                ", essid='" + essid + '\'' +
                ", num=" + num +
                ", channel=" + channel +
                ", signal=" + signal +
                ", ltime='" + ltime + '\'' +
                ", isTag=" + tag +
                ", note='" + note + '\'' +
                '}';
    }



    @Override
    public int compareTo(ApVo vo) {
        return -(signal-vo.signal);
    }
}
