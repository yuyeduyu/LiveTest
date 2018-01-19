package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anye.greendao.gen.ConnectRelationDao;
import com.anye.greendao.gen.LogDao;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.LogAdapter;
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.ConnectRelation;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.count)
    TextView mCount;
    @BindView(R.id.clear)
    Button mClear;
    @BindView(R.id.relation)
    Button mRelation;
    @BindView(R.id.ltime)
    TextView mLtime;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    @BindView(R.id.activity_log)
    LinearLayout mActivityLog;
    private String mac;
    private ArrayList<Log> mList;
    private LogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
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
        adapter =new LogAdapter(mList);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.addItemDecoration(new MyItemDecoration(this,LinearLayoutManager.HORIZONTAL));
        mRecycler.setAdapter(adapter);
        mCount.setText(mList.size()+"");
    }

    private void initData() {
        mac = getIntent().getStringExtra("mac");
        LogDao dao = MyApplication.getInstances().getDaoSession().getLogDao();
        mList = (ArrayList<Log>) dao.queryBuilder().where(LogDao.Properties.Mac.eq(mac)).list();
        ConnectRelationDao relationDao = MyApplication.getInstances().getDaoSession().getConnectRelationDao();
        List<ConnectRelation> mRelations = relationDao.queryBuilder().where(ConnectRelationDao.Properties.Ap.eq(mac)).list();
        if (mRelations.size() > 0) mRelation.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.clear, R.id.relation})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clear:
                LogDao dao = MyApplication.getInstances().getDaoSession().getLogDao();
                for (Log log:mList){
                    dao.delete(log);
                }
                mList.clear();
                adapter.notifyDataSetChanged();
                mCount.setText(mList.size() + "");
                break;
            case R.id.relation:
                Intent intent = new Intent(LogActivity.this,LogRelationActivity.class);
                intent.putExtra("mac",mac);
                startActivity(intent);
                break;
        }
    }
}
