package com.ascend.wangfeng.locationbyhand.presenter;
import com.ascend.wangfeng.locationbyhand.bean.NoteVo;
import com.ascend.wangfeng.locationbyhand.contract.TargetContract;
import com.ascend.wangfeng.locationbyhand.model.TargetModelImpl;

import java.util.List;

/**
* Created by lenovo on 2017/02/09
*/

public class TargetPresenterImpl implements TargetContract.Presenter{
    private TargetContract.View mView;
    private TargetContract.Model mModel;

    public TargetPresenterImpl(TargetContract.View view) {
        mView = view;
        mModel=new TargetModelImpl(this);
    }


    @Override
    public void addMac(String mac, String note) {
        mModel.addMac(mac, note);
    }

    @Override
    public void update(String oldMac, String mac, String note) {
        mModel.update(oldMac, mac, note);
    }

    @Override
    public void delMac(String mac) {
        mModel.delMac(mac);
    }

    @Override
    public void receiveResult(List<NoteVo> list) {
        mView.update(list);
        mView.show("操作成功");
    }

    @Override
    public void receiveResult(String error) {
        mView.show(error);
    }

}