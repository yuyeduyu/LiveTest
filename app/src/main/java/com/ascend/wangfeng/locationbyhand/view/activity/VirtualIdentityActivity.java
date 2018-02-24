package com.ascend.wangfeng.locationbyhand.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.adapter.TitleAdapter;
import com.ascend.wangfeng.locationbyhand.adapter.VirtualIdentityAdapter;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.util.TimeUtil;
import com.ascend.wangfeng.locationbyhand.util.hashMap.MapBean;
import com.ascend.wangfeng.locationbyhand.util.hashMap.MapUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VirtualIdentityActivity extends AppCompatActivity {

    @BindView(R.id.list_title)
    RecyclerView mListTitle;
    @BindView(R.id.list)
    RecyclerView mList;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    private TitleAdapter titleAdapter;
    private VirtualIdentityAdapter adapter;
    private ArrayList<MapBean> mIndentities;
    private ArrayList<Object> keyValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_identity);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        StaVo staVo = (StaVo) bundle.getSerializable("sta");
        mIndentities = MapUtil.toList(staVo.getIdentities());
        adapter = new VirtualIdentityAdapter();
        titleAdapter = new TitleAdapter();
        keyValues = new ArrayList<>();
        keyValues.add(new MapBean("目标MAC", staVo.getMac()));
        keyValues.add(new MapBean("连接AP", staVo.getApmac()));
        keyValues.add(new MapBean("AP-MAC", staVo.getEssid()));
        keyValues.add(new MapBean("采集时间", TimeUtil.getTime(staVo.getLtime())));
    }
    private void initView() {
        mToolbar.setTitle("虚拟身份");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mList.setLayoutManager(new LinearLayoutManager(this));
        mList.setAdapter(adapter);
        mList.addItemDecoration(new MyItemDecoration(this, LinearLayoutManager.HORIZONTAL,4,R.color.gray));
        mListTitle.setLayoutManager(new LinearLayoutManager(this));
        mListTitle.setAdapter(titleAdapter);
        mListTitle.addItemDecoration(new MyItemDecoration(this, LinearLayoutManager.HORIZONTAL,4,R.color.black));
    }
}
