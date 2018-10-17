package com.ascend.wxldcmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.keeplive.LiveService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 便携式汽车采集app首页
 *
 * @author lish
 *         created at 2018-08-06 11:22
 */
public class MenuMainActivity extends AppCompatActivity {
    @BindView(R.id.ll_tongji)
    LinearLayout llTongji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_main);
        ButterKnife.bind(this);
        LiveService.toLiveService(MenuMainActivity.this);
    }

    @OnClick(R.id.ll_tongji)
    public void onViewClicked() {
        startActivity(new Intent(MenuMainActivity.this,MainActivity.class));
    }
}
