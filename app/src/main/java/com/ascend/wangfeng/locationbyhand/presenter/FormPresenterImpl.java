package com.ascend.wangfeng.locationbyhand.presenter;

import android.os.Handler;

import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.contract.FormContract;
import com.ascend.wangfeng.locationbyhand.model.FormModelImpl;
import com.ascend.wangfeng.locationbyhand.view.fragment.FormAsTargetFragment;

import java.util.List;

/**
* Created by lenovo on 2017/04/10
*/

public class FormPresenterImpl implements FormContract.Presenter{
    private FormAsTargetFragment mFragment;
    private FormContract.Model mModel;
    private Handler mHandler;
    private Runnable mRunable;

    public FormPresenterImpl(FormAsTargetFragment fragment) {
        mFragment = fragment;
        mModel = new FormModelImpl(this);

    }

    @Override
    public void update(final String mac, final int type) {
        mHandler = new Handler();
        mRunable = new Runnable(){
            @Override
            public void run() {
                mModel.getData(mac,type);
                mHandler.postDelayed(this,1000);
            }
        };
        mHandler.post(mRunable);
    }

    @Override
    public void receiveUpdate(ApVo apVo, List<StaVo> staVos) {
            mFragment.update(apVo,staVos);
    }

    @Override
    public void reciiveStaNum(int count) {
        mFragment.updateStaSum(count);
    }

    @Override
    public void stopUpdate() {
        mHandler.removeCallbacks(mRunable);
    }

}