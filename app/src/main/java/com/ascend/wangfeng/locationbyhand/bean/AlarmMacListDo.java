package com.ascend.wangfeng.locationbyhand.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by fengye on 2017/3/14.
 * email 1040441325@qq.com
 */

public class AlarmMacListDo {

    /**
     * total : 3
     * channel : a
     * count : 0
     * wxld : WXLD-C-00B
     * track : 1
     * rows : [{"mac":"8C:AB:8E:AF:A8:E8","note":"ascend10"},{"mac":"CC:20:E8:AE:F6:BB","note":"qwe"},{"mac":"0E:03:6F:00:00:06","note":"te"}]
     */

    @SerializedName("total")
    private int total;
    @SerializedName("channel")
    private String channel;
    @SerializedName("count")
    private int count;
    @SerializedName("wxld")
    private String wxld;
    @SerializedName("track")
    private int track;
    /**
     * mac : 8C:AB:8E:AF:A8:E8
     * note : ascend10
     */

    @SerializedName("rows")
    private List<NoteVo> rows;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getWxld() {
        return wxld;
    }

    public void setWxld(String wxld) {
        this.wxld = wxld;
    }

    public int getTrack() {
        return track;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public List<NoteVo> getNoteVos() {
        return rows;
    }

    public void setNoteVos(List<NoteVo> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "AlarmMacListDo{" +
                "total=" + total +
                ", channel='" + channel + '\'' +
                ", count=" + count +
                ", wxld='" + wxld + '\'' +
                ", track=" + track +
                ", rows=" + rows +
                '}';
    }
}
