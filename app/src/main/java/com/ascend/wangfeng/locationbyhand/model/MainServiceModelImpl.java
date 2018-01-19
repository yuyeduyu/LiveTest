package com.ascend.wangfeng.locationbyhand.model;

import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.ApMessageDo;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.StaMessageDo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.contract.MainServiceContract;
import com.ascend.wangfeng.locationbyhand.util.DataFormat;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lenovo on 2017/02/08
 */

public class MainServiceModelImpl extends BaseModel implements MainServiceContract.Model {
    private MainServiceContract.Presenter mPresenter;

    public MainServiceModelImpl(MainServiceContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void getApList() {
        AppClient.getWiFiApi().getApMessage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<ApMessageDo>() {
                    @Override
                    public void onNext(ApMessageDo aDo) {
                        //数据处理：排序，添加标记
                        List<ApVo> apVos = aDo.getApVos();
                        apVos = DataFormat.makeTagOfAp(apVos);
                        //发送数据
                        mPresenter.receiveApData(apVos);
                        //发送电量
                        mPresenter.receiveElectric(aDo.getElectric());
                    }
                });
    }

    @Override
    public void getStaList() {
        AppClient.getWiFiApi().getStaMessage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<StaMessageDo>() {
                    @Override
                    public void onNext(StaMessageDo aDo) {
                        //数据处理，排序，添加标记
                        List<StaVo> staVos = aDo.getStaVos();
                        staVos = DataFormat.makeTagOfSta(staVos);
                        //发送数据
                        mPresenter.receiveStaData(staVos);
                    }
                });
    }
}