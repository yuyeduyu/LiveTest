package com.ascend.wangfeng.locationbyhand.api;

import android.util.Log;

import rx.Subscriber;

/**
 * Created by fengye on 2016/10/25.
 * email 1040441325@qq.com
 * 网络请求通用处理
 */
public abstract class BaseSubcribe<T> extends Subscriber<T> {

    private String TAG="retrofit";

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        Log.i(TAG, "onError: "+e.getMessage());
    }

}
