package com.ascend.wangfeng.locationbyhand.view.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.anye.greendao.gen.DaoSession;
import com.anye.greendao.gen.LogDao;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.AnalyseAdapter;
import com.ascend.wangfeng.locationbyhand.adapter.StatisticAdapter;
import com.ascend.wangfeng.locationbyhand.bean.HistoryMacBean;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 统计
 *
 * @author lish
 *         created at 2018-07-16 14:36
 */
public class StatisticsActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    private List<Log> datas;
    private StatisticAdapter adapter;
    private LogDao logDao;
    private List<Log> data;

    @Override
    protected int setContentView() {
        return R.layout.activity_statistics;
    }

    @Override
    protected void initView() {
        logDao = MyApplication.instances.getDaoSession().getLogDao();
        initTool();
        initRecyle();
    }

    /**
     * 初始化 toolbar
     */
    private void initTool() {
        mToolbar.setTitle("统计");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
    }

    private void goBack() {
        finish();
    }

    private void initRecyle() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(StatisticsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        datas = getData(MyApplication.instances.getDaoSession());

        adapter = new StatisticAdapter(datas, StatisticsActivity.this);
        recycler.setLayoutManager(layoutManager);
        recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recycler.setAdapter(adapter);
    }

    private static final String SQL_DISTINCT_ENAME = "SELECT " + LogDao.Properties.Mac.columnName
            +","+LogDao.Properties.Ltime.columnName+","+LogDao.Properties.Type.columnName
            + " FROM " + LogDao.TABLENAME + " GROUP BY "+ LogDao.Properties.Mac.columnName;

    public static List<Log> getData(DaoSession session) {
        ArrayList<Log> result = new ArrayList<>();
        Cursor c = session.getDatabase().rawQuery(SQL_DISTINCT_ENAME, null);
        try {
            if (c.moveToFirst()) {
                do {
                    Log log = new Log();
                    log.setMac(c.getString(0));
                    log.setLtime(Long.parseLong(c.getString(1)));
                    log.setType(Integer.parseInt(c.getString(2)));
                    android.util.Log.e("setType",c.getString(2));
                    result.add(log);
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }
}
