package com.ascend.wangfeng.locationbyhand.event;

import com.ascend.wangfeng.locationbyhand.bean.StaVo;

import java.util.List;

/**
 * Created by fengye on 2017/2/8.
 * email 1040441325@qq.com
 */

public class StaListEvent {
    private List<StaVo> mList;

    public List<StaVo> getList() {
        return mList;
    }

    public void setList(List<StaVo> list) {
        mList = list;
    }

    public StaListEvent(List<StaVo> list) {
        mList = list;
    }
}
