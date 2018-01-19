package com.ascend.wangfeng.locationbyhand.event.ble;

import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;

import java.util.List;

/**
 * Created by fengye on 2017/8/12.
 * email 1040441325@qq.com
 */

public class RelationEvent {
    private ApVo mApVo;
    private List<StaVo>mStaVos;

    public RelationEvent(ApVo apVo, List<StaVo> staVos) {
        mApVo = apVo;
        mStaVos = staVos;
    }

    public ApVo getApVo() {
        return mApVo;
    }

    public void setApVo(ApVo apVo) {
        mApVo = apVo;
    }

    public List<StaVo> getStaVos() {
        return mStaVos;
    }

    public void setStaVos(List<StaVo> staVos) {
        mStaVos = staVos;
    }
}
