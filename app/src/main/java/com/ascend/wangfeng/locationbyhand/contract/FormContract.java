package com.ascend.wangfeng.locationbyhand.contract;

import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;

import java.util.List;

/**
 * Created by fengye on 2017/4/10.
 * email 1040441325@qq.com
 */

public class FormContract {
public interface View{
    void update(ApVo apVo, List<StaVo> staVos);
    void updateStaSum(int count);
}

public interface Presenter{
    void update(final String mac, final int type);
    void receiveUpdate(ApVo apVo, List<StaVo> staVos);
    void reciiveStaNum(int count);
    void stopUpdate();
}

public interface Model{
    void getData(final String mac, final int type);
}
}