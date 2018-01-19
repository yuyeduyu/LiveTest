package com.ascend.wangfeng.locationbyhand.model;
import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.contract.LineContract;
import com.ascend.wangfeng.locationbyhand.util.DataFormat;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
* Created by lenovo on 2017/02/08
*/

public class LineModelImpl implements LineContract.Model{
    private LineContract.Presenter mPresenter;

    public LineModelImpl(LineContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void getAp(String mac) {
        AppClient.getStaticWiFiApi().getAp(mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<ApVo>() {
                    @Override
                    public void onNext(ApVo vo) {
                       vo = DataFormat.makeTagOfAp(vo);
                        mPresenter.receiveApData(vo);
                    }
                });
    }

    @Override
    public void getSta(String mac) {
        AppClient.getStaticWiFiApi().getSta(mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<StaVo>() {
                    @Override
                    public void onNext(StaVo vo) {
                        vo = DataFormat.makeTagOfSta(vo);
                        mPresenter.receiveStaData(vo);
                    }
                });
    }
}