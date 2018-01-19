package com.ascend.wangfeng.locationbyhand.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by fengye on 2017/4/10.
 * email 1040441325@qq.com
 */

public class ApAssociatedDo {

    /**
     * total : 2
     * rows : [{"mac":"DC:EE:06:55:28:9E","apmac":"8C:AB:8E:AF:A8:E8","essid":"ASCEND10","channel":6,"num":3,"signal":-78,"ltime":"23:15:45"},{"mac":"B0:89:00:A8:D3:11","apmac":"8C:AB:8E:AF:A8:E8","essid":"ASCEND10","channel":6,"num":3,"signal":-78,"ltime":"23:15:45"}]
     * rows_ap : {"bssid":"8C:AB:8E:AF:A8:E8","essid":"ASCEND10","num":3,"channel":6,"signal":-90,"ltime":"23:15:40"}
     */

    @SerializedName("total")
    private int total;
    /**
     * bssid : 8C:AB:8E:AF:A8:E8
     * essid : ASCEND10
     * num : 3
     * channel : 6
     * signal : -90
     * ltime : 23:15:40
     */

    @SerializedName("rows_ap")
    private ApVo ap;
    /**
     * mac : DC:EE:06:55:28:9E
     * apmac : 8C:AB:8E:AF:A8:E8
     * essid : ASCEND10
     * channel : 6
     * num : 3
     * signal : -78
     * ltime : 23:15:45
     */

    @SerializedName("rows")
    private ArrayList<StaVo> staVos;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ApVo getAp() {
        return ap;
    }

    public void setAp(ApVo ap) {
        this.ap = ap;
    }

    public ArrayList<StaVo> getStaVos() {
        return staVos;
    }

    public void setStaVos(ArrayList<StaVo> staVos) {
        this.staVos = staVos;
    }
}
