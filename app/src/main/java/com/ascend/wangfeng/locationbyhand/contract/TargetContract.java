package com.ascend.wangfeng.locationbyhand.contract;

import com.ascend.wangfeng.locationbyhand.bean.NoteVo;

import java.util.List;

/**
 * Created by fengye on 2017/2/9.
 * email 1040441325@qq.com
 * 标记目标界面
 */

public class TargetContract {
public interface View{
    void update(List<NoteVo> noteVos);
    void show(String message);
}

public interface Presenter{
    void addMac(String mac,String note);
    void update(String oldMac,String mac,String note);
    void delMac(String mac);

    /**
     *
     * @param list 执行成功返回结果
     */
    void receiveResult(List<NoteVo> list);

    /**
     *
     * @param error 失败返回结果；
     */
    void receiveResult(String error);
}

public interface Model{
    void addMac(String mac,String note);
    void update(String oldMac,String mac,String note);
    void delMac(String mac);
}


}