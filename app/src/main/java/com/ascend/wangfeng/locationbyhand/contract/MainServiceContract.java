package com.ascend.wangfeng.locationbyhand.contract;

import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;

import java.util.List;

/**
 * Created by fengye on 2017/2/8.
 * email 1040441325@qq.com
 *
 * 后台服务
 */

public class MainServiceContract {
    
public interface View{
    void updateAp(List<ApVo> data);
    void updateSta(List<StaVo> data);
    void updateElectric(String electric);
    void updateData(List<ApVo> aps, List<StaVo> stas);
}

public interface Presenter{
    void update();
    void stop();
    void receiveApData(List<ApVo> data);
    void receiveStaData(List<StaVo> data);
    void receiveElectric(String electric);
}

public interface Model{
    void getApList();
    void getStaList();

}


}