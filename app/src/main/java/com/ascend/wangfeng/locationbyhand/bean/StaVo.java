package com.ascend.wangfeng.locationbyhand.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by fengye on 2017/3/14.
 * email 1040441325@qq.com
 */

public class StaVo implements Comparable<StaVo> {

    /**
     * mac : 84:78:8B:BF:B5:83
     * apmac : 8C:AB:8E:AF:A8:E8
     * essid : ASCEND10
     * channel : 6
     * num : 1
     * signal : -68
     * ltime : 23:15:45
     */

    @SerializedName("mac")
    private String mac;
    @SerializedName("apmac")
    private String apmac;
    @SerializedName("essid")
    private String essid;
    @SerializedName("channel")
    private int channel;
    @SerializedName("num")
    private int num;
    @SerializedName("signal")
    private int signal;
    @SerializedName("ltime")
    private long ltime;
    private boolean tag;
    private String note;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getApmac() {
        return apmac;
    }

    public void setApmac(String apmac) {
        if ("00:00:00:00:00:00".equals(apmac)) {
            this.apmac = "未连接";
        }
        this.apmac = apmac;
    }

    public String getEssid() {
        return essid;
    }

    public void setEssid(String essid) {
        this.essid = essid;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
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

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "StaVo{" +
                "mac='" + mac + '\'' +
                ", apmac='" + apmac + '\'' +
                ", essid='" + essid + '\'' +
                ", channel=" + channel +
                ", num=" + num +
                ", signal=" + signal +
                ", ltime='" + ltime + '\'' +
                ", isTag=" + tag +
                ", note='" + note + '\'' +
                '}';
    }


    @Override
    public int compareTo(StaVo vo) {
        return -(signal - vo.getSignal());
    }
}
