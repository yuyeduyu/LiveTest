package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.anye.greendao.gen.EventBeanDao;
import com.anye.greendao.gen.LogDao;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.AnalyeAdapter;
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.bean.EventBean;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AnalyeActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.title)
    RecyclerView mTitle;
    @BindView(R.id.content)
    RecyclerView mContent;
    @BindView(R.id.btn)
    Button mBtn;
    private LogDao mStaDao;
    private EventBeanDao mEventDao;
    private ArrayList<EventBean> mEvents;
    private ArrayList<String> mMacs;
    boolean mFirst = true;//是否是第一次的数据,第一次取全部,之后取交集
    private AlertDialog dialog;
    private ArrayList<String> mTitles;
    private AnalyeAdapter titleAdapter;
    private AnalyeAdapter contentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analye);
        ButterKnife.bind(this);
        initTool();
        initData();
        initDialog();
        initView();
    }

    private void initView() {
        titleAdapter = new AnalyeAdapter(mTitles);
        contentAdapter = new AnalyeAdapter(mMacs);
        mTitle.setLayoutManager(new LinearLayoutManager(this));
        mContent.setLayoutManager(new LinearLayoutManager(this));
        mTitle.setAdapter(titleAdapter);
        mContent.setAdapter(contentAdapter);
        mTitle.addItemDecoration(new MyItemDecoration(this, LinearLayoutManager.HORIZONTAL));
        mContent.addItemDecoration(new MyItemDecoration(this, LinearLayoutManager.HORIZONTAL));
    }

    private void initTool() {
        mToolbar.setTitle("分析");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initDialog() {
        final String[] items = new String[mEvents.size()];
        boolean initChoiceSets[] = new boolean[mEvents.size()];
        final ArrayList<Boolean> choices = new ArrayList<>();
        for (int i = 0; i < mEvents.size(); i++) {
            items[i] = mEvents.get(i).getTitle();
            initChoiceSets[i] = false;
            choices.add(i, false);
        }

        dialog = new AlertDialog.Builder(AnalyeActivity.this)
                .setTitle("添加事件")
                .setMultiChoiceItems(items, initChoiceSets, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i, boolean b) {
                      choices.set(i,b);
                        android.util.Log.i("aaaaaa", "onClick: "+choices.size());
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {

                        clean();
                        for (int j = 0; j < choices.size(); j++) {
                            if (choices.get(j)) {
                                mTitles.add(mEvents.get(j).getTitle());
                                getIntersection(j);
                            }
                        }
                        //刷新数据
                        titleAdapter.notifyDataSetChanged();
                        contentAdapter.notifyDataSetChanged();

                    }
                }).create();
    }

    private void clean() {
        //清空,重新添加数据
        mFirst = true;
        mTitles.clear();
        mMacs.clear();
    }

    private void initData() {
        mEventDao = MyApplication.getInstances().getDaoSession().getEventBeanDao();
        mStaDao = MyApplication.getInstances().getDaoSession().getLogDao();
        mEvents = (ArrayList<EventBean>) mEventDao.loadAll();
        mTitles = new ArrayList<>();
        mMacs = new ArrayList<>();
        //初始化数据;
    }

    private void getIntersection(int position) {
        EventBean event = mEvents.get(position);
        List<Log> logs = mStaDao.queryBuilder().where(LogDao.Properties.Ltime.between(event.getStart(), event.getEnd())).list();
        ArrayList<String> macs = new ArrayList<>();
        for (Log log : logs) {
            if (!macs.contains(log.getMac())) {
                macs.add(log.getMac());
            }
        }
        if (mFirst) {
            mMacs.addAll(macs);
            mFirst = false;
        } else {
            mMacs.retainAll(macs);
        }

    }

    @OnClick(R.id.btn)
    public void onViewClicked() {
        dialog.show();
    }
}
