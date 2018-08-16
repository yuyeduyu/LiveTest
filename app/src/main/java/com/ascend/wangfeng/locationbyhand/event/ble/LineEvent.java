package com.ascend.wangfeng.locationbyhand.event.ble;

import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;

/**
 * Created by fengye on 2017/8/11.
 * email 1040441325@qq.com
 */

public class LineEvent {
    private ApVo mApVo;
    private StaVo mStaVo;
    private int type;//0:ap,1:终端

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ApVo getApVo() {
        return mApVo;
    }

    public void setApVo(ApVo apVo) {
        mApVo = apVo;
    }

    public StaVo getStaVo() {
        return mStaVo;
    }

    public void setStaVo(StaVo staVo) {
        mStaVo = staVo;
    }

    public LineEvent(StaVo staVo) {
        mStaVo = staVo;
        type =1;
    }

    public LineEvent(ApVo apVo) {
        type =0;
        mApVo = apVo;
    }
}
