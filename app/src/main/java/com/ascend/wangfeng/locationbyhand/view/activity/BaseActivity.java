package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by fengye on 2017/2/7.
 * email 1040441325@qq.com
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Context context;
    private Unbinder bind;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        /** 注意：setContentView 必需在 bind 之前 */
        setContentView(setContentView());
        bind = ButterKnife.bind(this);
        initView();
    }

    /** 子类设置界面 */
    protected abstract int setContentView();

    protected abstract void initView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bind.unbind();
    }
}
