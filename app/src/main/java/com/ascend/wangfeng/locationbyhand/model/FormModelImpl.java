package com.ascend.wangfeng.locationbyhand.model;
import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.ApAssociatedDo;
import com.ascend.wangfeng.locationbyhand.bean.StaAssociatedDo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.contract.FormContract;
import com.ascend.wangfeng.locationbyhand.util.DataFormat;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
* Created by lenovo on 2017/04/10
*/

public class FormModelImpl implements FormContract.Model{
    private FormContract.Presenter mPresenter;

    public FormModelImpl(FormContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void getData(final String mac, final int type) {
        if (type == 0){//ap
        AppClient.getStaticWiFiApi().getApAssociated(mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<ApAssociatedDo>() {
                    @Override
                    public void onNext(ApAssociatedDo aDo) {
                            List<StaVo> staVos = DataFormat.makeTagOfSta(aDo.getStaVos());
                            mPresenter.receiveUpdate(DataFormat.makeTagOfAp(aDo.getAp()),
                                    staVos );
                            mPresenter.reciiveStaNum(aDo.getTotal());
                    }
                });
        } else {//sta
            AppClient.getStaticWiFiApi().getStaAssociated(mac)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new BaseSubcribe<StaAssociatedDo>() {
                        @Override
                        public void onNext(StaAssociatedDo aDo) {
                            ArrayList<StaVo> staVos = DataFormat.StaAssociatedFormat(aDo);
                            mPresenter.receiveUpdate(aDo.getRowsAp(),staVos);
                            mPresenter.reciiveStaNum(aDo.getTotal());
                        }
                    });
        }

    }
}