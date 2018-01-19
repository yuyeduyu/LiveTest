package com.ascend.wangfeng.locationbyhand.model;

import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.AlarmMacListDo;
import com.ascend.wangfeng.locationbyhand.contract.TargetContract;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
* Created by lenovo on 2017/02/09
*/

public class TargetModelImpl implements TargetContract.Model{
    public static final String TAG = "TargetModelImpl";
    private TargetContract.Presenter mPresenter;

    public TargetModelImpl(TargetContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public TargetModelImpl() {
    }


    @Override
    public void addMac(String mac, String note) {

        AppClient.getWiFiApi().addMac(mac, note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<AlarmMacListDo>() {
                    @Override
                    public void onNext(AlarmMacListDo aDo) {
                        mPresenter.receiveResult(aDo.getNoteVos());
                        Config.updateConfig();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mPresenter.receiveResult(e.getMessage());
                    }
                });
    }

    @Override
    public void update(String oldMac, String mac, String note) {
        AppClient.getWiFiApi().updateMac(oldMac,mac,note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<AlarmMacListDo>() {
                    @Override
                    public void onNext(AlarmMacListDo aDo) {
                        mPresenter.receiveResult(aDo.getNoteVos());
                        Config.updateConfig();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mPresenter.receiveResult(e.getMessage());
                    }
                });
    }

    @Override
    public void delMac(String mac) {
        AppClient.getWiFiApi().delMac(mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<AlarmMacListDo>() {
                    @Override
                    public void onNext(AlarmMacListDo aDo) {
                        mPresenter.receiveResult(aDo.getNoteVos());
                        Config.updateConfig();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        mPresenter.receiveResult(e.getMessage());
                    }
                });
    }
}