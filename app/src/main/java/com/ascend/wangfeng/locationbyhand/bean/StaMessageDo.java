package com.ascend.wangfeng.locationbyhand.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by fengye on 2017/3/14.
 * email 1040441325@qq.com
 */

public class StaMessageDo {

    /**
     * total : 3
     * electric : 40%
     * terminals : [{"mac":"84:78:8B:BF:B5:83","apmac":"8C:AB:8E:AF:A8:E8","essid":"ASCEND10","channel":6,"num":1,"signal":-68,"ltime":"23:15:45"},{"mac":"B0:89:00:A8:D3:11","apmac":"8C:AB:8E:AF:A8:E8","essid":"ASCEND10","channel":6,"num":3,"signal":-78,"ltime":"23:15:45"},{"mac":"54:EA:A8:54:51:55","apmac":"00:00:00:00:00:00","essid":"","channel":0,"num":1,"signal":-92,"ltime":"23:15:25"}]
     */

    @SerializedName("total")
    private int total;
    @SerializedName("electric")
    private String electric;
    /**
     * mac : 84:78:8B:BF:B5:83
     * apmac : 8C:AB:8E:AF:A8:E8
     * essid : ASCEND10
     * channel : 6
     * num : 1
     * signal : -68
     * ltime : 23:15:45
     */

    @SerializedName("terminals")
    private List<StaVo> terminals;

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

    public List<StaVo> getStaVos() {
        return terminals;
    }

    public void setStaVos(List<StaVo> terminals) {
        this.terminals = terminals;
    }

}
