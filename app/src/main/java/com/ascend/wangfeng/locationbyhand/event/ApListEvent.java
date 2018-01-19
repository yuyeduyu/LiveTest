package com.ascend.wangfeng.locationbyhand.event;

import com.ascend.wangfeng.locationbyhand.bean.ApVo;

import java.util.List;

/**
 * Created by fengye on 2017/2/8.
 * email 1040441325@qq.com
 */

public class ApListEvent {
    private List<ApVo> list;

    public List<ApVo> getList() {
        return list;
    }

    public void setList(List<ApVo> list) {
        this.list = list;
    }

    public ApListEvent(List<ApVo> list) {
        this.list = list;
    }
}
