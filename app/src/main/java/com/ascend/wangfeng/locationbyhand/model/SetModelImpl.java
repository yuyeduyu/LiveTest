package com.ascend.wangfeng.locationbyhand.model;
import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.contract.SetContract;
import com.ascend.wangfeng.locationbyhand.util.DataFormat;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
* Created by lenovo on 2017/02/07
*/

public class SetModelImpl implements SetContract.Model{
    private String TAG=getClass().getCanonicalName();
    private SetContract.Presenter mPresenter;

    public SetModelImpl(SetContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setRing(boolean ring) {

        mPresenter.receiveResult("成功");
    }

    @Override
    public void setChannel(int channel) {
        String channelStr= DataFormat.channel_intToString(channel);
        AppClient.getWiFiApi().setChannel(channelStr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        Config.updateConfig();
                        mPresenter.receiveResult("成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mPresenter.receiveResult(e.getMessage());
                    }
                });

    }

    @Override
    public void setChannelLock(int channelLock) {
        AppClient.getWiFiApi().setPatternOfChannelLock(channelLock)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        mPresenter.receiveResult("成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mPresenter.receiveResult(e.getMessage());
                    }
                });

    }

    @Override
    public void setUrl(String url) {
        SharedPreferencesUtils.setParam(MyApplication.mContext,"url_equipment",url);
    }

    @Override
    public void setTime(long time) {
        AppClient.getWiFiApi().setTime(time)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<String>() {
                    @Override
                    public void onNext(String aBoolean) {

                    }
                });

    }
}