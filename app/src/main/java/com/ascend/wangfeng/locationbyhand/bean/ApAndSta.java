package com.ascend.wangfeng.locationbyhand.bean;

/**
 * Created by fengye on 2017/6/27.
 * email 1040441325@qq.com
 */

public class ApAndSta {
    private ApMessageDo aDo;
    private StaMessageDo sDo;

    public ApMessageDo getaDo() {
        return aDo;
    }

    public void setaDo(ApMessageDo aDo) {
        this.aDo = aDo;
    }

    public StaMessageDo getsDo() {
        return sDo;
    }

    public void setsDo(StaMessageDo sDo) {
        this.sDo = sDo;
    }

    public ApAndSta(ApMessageDo aDo, StaMessageDo sDo) {
        this.aDo = aDo;
        this.sDo = sDo;
    }
}
