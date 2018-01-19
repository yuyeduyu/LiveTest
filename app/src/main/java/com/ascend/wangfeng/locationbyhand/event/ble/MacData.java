package com.ascend.wangfeng.locationbyhand.event.ble;

import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;

import java.util.List;

/**
 * Created by fengye on 2017/8/11.
 * email 1040441325@qq.com
 */

public class MacData {
    private List<ApVo>  mApVos;
    private List<StaVo> mStaVos;

    public MacData(List<ApVo> apVos, List<StaVo> staVos) {
        mApVos = apVos;
        mStaVos = staVos;
    }

    public List<ApVo> getApVos() {
        return mApVos;
    }

    public void setApVos(List<ApVo> apVos) {
        mApVos = apVos;
    }

    public List<StaVo> getStaVos() {
        return mStaVos;
    }

    public void setStaVos(List<StaVo> staVos) {
        mStaVos = staVos;
    }
}
