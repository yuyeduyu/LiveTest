package com.ascend.wangfeng.locationbyhand.contract;

import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;

/**
 * Created by fengye on 2017/2/8.
 * email 1040441325@qq.com
 * 折线图表
 */

public class LineContract {
    
public interface View{
    void updateAp(ApVo data);
    void updateSta(StaVo data);
}

public interface Presenter{
    void update(String mac,Integer type);
    void stop();
    void receiveApData(ApVo data);
    void receiveStaData(StaVo data);
}

public interface Model{
    void getAp(String mac);
    void getSta(String mac);
}
}