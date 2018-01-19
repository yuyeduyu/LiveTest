package com.ascend.wangfeng.locationbyhand.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by fengye on 2017/3/14.
 * email 1040441325@qq.com
 */

public class ApMessageDo {

    /**
     * total : 2
     * electric : 40%
     * routers : [{"bssid":"02:1A:11:F8:BC:43","essid":"tonxbo","num":3,"channel":6,"signal":-90,"ltime":"23:15:40"},{"bssid":"9C:D2:1E:C9:6B:C7","essid":"HP-Print-C7-LaserJet 1025","num":8,"channel":6,"signal":-91,"ltime":"23:15:45"}]
     */

    @SerializedName("total")
    private int total;
    @SerializedName("electric")
    private String electric;
    /**
     * bssid : 02:1A:11:F8:BC:43
     * essid : tonxbo
     * num : 3
     * channel : 6
     * signal : -90
     * ltime : 23:15:40
     */

    @SerializedName("routers")
    private List<ApVo> routers;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getElectric() {
        return electric;
    }

    public void setElectric(String electric) {
        this.electric = electric;
    }

    public List<ApVo> getApVos() {
        return routers;
    }

    public void setApVos(List<ApVo> routers) {
        this.routers = routers;
    }

    @Override
    public String toString() {
        return "ApMessageDo{" +
                "total=" + total +
                ", electric='" + electric + '\'' +
                ", routers=" + routers +
                '}';
    }
}
