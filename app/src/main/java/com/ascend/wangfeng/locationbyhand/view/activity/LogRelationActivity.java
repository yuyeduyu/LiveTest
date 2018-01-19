package com.ascend.wangfeng.locationbyhand.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.anye.greendao.gen.ConnectRelationDao;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.LogRelationAdapter;
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.ConnectRelation;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LogRelationActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.listView)
    RecyclerView mListView;
    @BindView(R.id.activity_log_relation)
    LinearLayout mActivityLogRelation;
    private String mac;
    private List<ConnectRelation> mRelations;
    private LogRelationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_relation);
        ButterKnife.bind(this);
        initData();
        initView();

    }

    private void initView() {
        mToolbar.setTitle(mac);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        adapter = new LogRelationAdapter((ArrayList<ConnectRelation>) mRelations);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.addItemDecoration(new MyItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        mListView.setAdapter(adapter);
    }

    private void initData() {
        mac = getIntent().getStringExtra("mac");
        ConnectRelationDao relationDao = MyApplication.getInstances().getDaoSession().getConnectRelationDao();
        mRelations = relationDao.queryBuilder().where(ConnectRelationDao.Properties.Ap.eq(mac)).list();
    }
}
