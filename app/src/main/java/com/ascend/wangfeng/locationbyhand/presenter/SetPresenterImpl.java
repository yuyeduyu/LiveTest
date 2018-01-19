package com.ascend.wangfeng.locationbyhand.presenter;
import com.ascend.wangfeng.locationbyhand.contract.SetContract;
import com.ascend.wangfeng.locationbyhand.model.SetModelImpl;

/**
* Created by lenovo on 2017/02/07
*/

public class SetPresenterImpl implements SetContract.Presenter{
    private SetContract.View mView;
    private SetContract.Model mModel;

    public SetPresenterImpl(SetContract.View view) {
        mView = view;
        mModel=new SetModelImpl(this);
    }

    @Override
    public void setRing(boolean ring) {
        mModel.setRing(ring);

    }
    @Override
    public void setChannel(int channel) {
        mModel.setChannel(channel);
    }

    @Override
    public void setChannelLock(int channelLock) {
        mModel.setChannelLock(channelLock);
    }

    @Override
    public void setUrl(String url) {
        mModel.setUrl(url);
    }

    @Override
    public void setTime(String time) {

    }

    @Override
    public void receiveResult(String message) {
        mView.showMessage(message);
    }
}