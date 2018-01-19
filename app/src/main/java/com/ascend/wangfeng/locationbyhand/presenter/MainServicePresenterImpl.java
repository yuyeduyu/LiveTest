package com.ascend.wangfeng.locationbyhand.presenter;

import android.os.Handler;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.ApAndSta;
import com.ascend.wangfeng.locationbyhand.bean.ApMessageDo;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.StaMessageDo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.contract.MainServiceContract;
import com.ascend.wangfeng.locationbyhand.model.MainServiceModelImpl;
import com.ascend.wangfeng.locationbyhand.util.DataFormat;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
* Created by lenovo on 2017/02/08
*/

public class MainServicePresenterImpl implements MainServiceContract.Presenter{
    final String TAG=getClass().getCanonicalName();
    private static long DELAYTIME;
    private final Handler mHandler;
    private final Runnable mRunable;
    private MainServiceContract.View mView;
    private MainServiceContract.Model mModel;
    private Observable<ApAndSta> observableList;

    public MainServicePresenterImpl(MainServiceContract.View view) {
        mView = view;
        mModel=new MainServiceModelImpl(this);
        DELAYTIME= (int) SharedPreferencesUtils.getParam(MyApplication.mContext
        ,"delaytime",1*1000);
        mHandler=new Handler();
        mRunable=new Runnable(){

            @Override
            public void run() {

                Observable<ApMessageDo> observableAp = AppClient.getWiFiApi().getApMessage().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                Observable<StaMessageDo> observableSta = AppClient.getWiFiApi().getStaMessage().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                observableList =Observable.zip(observableAp, observableSta, new Func2<ApMessageDo, StaMessageDo, ApAndSta>() {
                    @Override
                    public ApAndSta call(ApMessageDo aDo, StaMessageDo aDo2) {
                        return new ApAndSta(aDo,aDo2);
                    }
                });
                observableList.subscribe(new BaseSubcribe<ApAndSta>() {
                    @Override
                    public void onNext(ApAndSta data) {
                        //数据处理：排序，添加标记
                        List<ApVo> apVos = data.getaDo().getApVos();
                        apVos = DataFormat.makeTagOfAp(apVos);
                        List<StaVo> staVos = data.getsDo().getStaVos();
                        staVos = DataFormat.makeTagOfSta(staVos);
                        //发送数据
                        mView.updateData(apVos,staVos);
                        mView.updateElectric(data.getaDo().getElectric());
                    }
                });
                mHandler.postDelayed(mRunable,DELAYTIME);
            }
        };
    }

    @Override
    public void update() {
      mHandler.post(mRunable);
    }

    @Override
    public void stop() {
   mHandler.removeCallbacks(mRunable);
    }

    @Override
    public void receiveApData(List<ApVo> data) {
        mView.updateAp(data);
    }

    @Override
    public void receiveStaData(List<StaVo> data) {
        mView.updateSta(data);
    }

    @Override
    public void receiveElectric(String electric) {
        mView.updateElectric(electric);
    }
}