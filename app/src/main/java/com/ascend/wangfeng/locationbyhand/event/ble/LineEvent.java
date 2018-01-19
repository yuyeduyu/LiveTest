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
    }

    public LineEvent(ApVo apVo) {

        mApVo = apVo;
    }
}
