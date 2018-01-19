package com.ascend.wangfeng.locationbyhand.presenter;

import android.os.Handler;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.contract.LineContract;
import com.ascend.wangfeng.locationbyhand.model.LineModelImpl;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;

/**
 * Created by lenovo on 2017/02/08
 */

public class LinePresenterImpl implements LineContract.Presenter {
    private Handler mHandler;
    private LineContract.Model mModel;
    private LineContract.View mView;
    private Runnable mRunable;
    private static long DELAYTIME;

    public LinePresenterImpl(LineContract.View view) {
        mView = view;
        mModel = new LineModelImpl(this);
        DELAYTIME = (int) SharedPreferencesUtils.getParam(MyApplication.mContext
                , "delaytime", 1 * 1000);
        mHandler = new Handler();


    }

    @Override
    public void update(final String mac, final Integer type) {
        if (mRunable == null) {
            if (type == 0) {
                mRunable = new Runnable() {
                    @Override
                    public void run() {
                        mModel.getAp(mac);
                        mHandler.postDelayed(mRunable, DELAYTIME);
                    }
                };
            } else {
                mRunable = new Runnable() {

                    @Override
                    public void run() {
                        mModel.getSta(mac);
                        mHandler.postDelayed(mRunable, DELAYTIME);
                    }
                };
            }
        }

        mHandler.post(mRunable);
    }


    @Override
    public void stop() {
        mHandler.removeCallbacks(mRunable);
    }

    @Override
    public void receiveApData(ApVo data) {
        mView.updateAp(data);
    }

    @Override
    public void receiveStaData(StaVo data) {
        mView.updateSta(data);
    }
}