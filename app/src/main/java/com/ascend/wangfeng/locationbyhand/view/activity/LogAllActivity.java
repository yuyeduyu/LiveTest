package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;

import com.anye.greendao.gen.LogDao;
import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.Log;
import com.ascend.wangfeng.locationbyhand.util.TimeUtil;
import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.Column;
import com.bin.david.form.data.TableData;
import com.bin.david.form.data.format.IFormat;
import com.bin.david.form.data.format.draw.IDrawFormat;
import com.bin.david.form.data.format.draw.TextImageDrawFormat;
import com.bin.david.form.utils.DensityUtils;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler2;

//监测日志
public class LogAllActivity extends AppCompatActivity {
    @BindView(R.id.store_house_ptr_frame)
    PtrClassicFrameLayout storeHousePtrFrame;
    private String TAG = getClass().getCanonicalName();

    @BindView(R.id.st_log)
    SmartTable mStLog;
    ArrayList<Log> mLogs;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.sb_choose)
    SwitchButton mSbChoose;
    LogDao logDao;
    private int pager = 1;//数据库查询页数
    private int pagerSize = 10;//数据库每次查询数量
    private int type = 1;//1:sta  0:ap
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_all);
        ButterKnife.bind(this);
        initData();
        initTable();
        initView();
//        initRefresh();
    }

    private void initRefresh() {
        storeHousePtrFrame.setLastUpdateTimeRelateObject(this);
        storeHousePtrFrame.setPtrHandler(new PtrHandler2() {
            @Override
            public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {
                return PtrDefaultHandler2.checkContentCanBePulledUp(frame, content, footer);
            }

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                //上拉加载
                mLogs = (ArrayList<Log>) logDao.queryBuilder()
                        .where(LogDao.Properties.Type.eq(type), LogDao.Properties.AppVersion.eq(Config.C_MINI))
                        .orderDesc(LogDao.Properties.Ltime)
                        .limit(pager*pagerSize)
                        .list();          //1:sta  0:ap
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //下拉刷新
                pager =1;
                mLogs = (ArrayList<Log>) logDao.queryBuilder()
                        .where(LogDao.Properties.Type.eq(type), LogDao.Properties.AppVersion.eq(Config.C_MINI))
                        .orderDesc(LogDao.Properties.Ltime)
                        .limit(pager*pagerSize)
                        .list();          //1:sta  0:ap
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

        });
        // the following are default settings
        storeHousePtrFrame.setResistance(1.7f);
        storeHousePtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        storeHousePtrFrame.setDurationToClose(200);
        storeHousePtrFrame.setDurationToCloseHeader(1000);
        // default is false
        storeHousePtrFrame.setPullToRefresh(false);
        // default is true
        storeHousePtrFrame.setKeepHeaderWhenRefresh(true);
    }

    //获取数据库中的相关数据
    private void initData() {
        logDao = MyApplication.getInstances().getDaoSession().getLogDao();
        mLogs = (ArrayList<Log>) logDao.queryBuilder()
                .where(LogDao.Properties.Type.eq(1), LogDao.Properties.AppVersion.eq(Config.C_MINI))
                .orderDesc(LogDao.Properties.Ltime)
                .list();
    }

    private void initView() {
        mToolbar.setTitle("监测日志");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mSbChoose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean b) {            //switchButton选择按钮
                if (b) {
                    refreshData(0);                         //刷新ap
                } else {
                    refreshData(1);                         //刷新sta
                }
            }
        });
    }

    private void refreshData(int i) {
        ArrayList<Log> logs = (ArrayList<Log>) logDao.queryBuilder()
                .where(LogDao.Properties.Type.eq(i), LogDao.Properties.AppVersion.eq(Config.C_MINI))
                .list();
        mLogs.clear();
//        logs.remove(logs.size()-1);
        mLogs.addAll(logs);

        try {
            mStLog.notifyDataChanged();
            android.util.Log.e(TAG, "正确信息:-->");
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "refreshData: " + i + "错误信息:-->" + e.toString());
        }

    }


    private void initTable() {
        Column<String> column1 = new Column<String>("MAC", "mac");
        final IFormat<Long> format2 = new IFormat<Long>() {
            @Override
            public String format(Long aLong) {
                return TimeUtil.getTime(aLong);
            }
        };
        Column<Long> column2 = new Column<>("采集时间", "ltime", format2);
        final IFormat<Integer> format3 = new IFormat<Integer>() {
            @Override
            public String format(Integer integer) {
                if (integer > -50) {
                    return "2米内";
                } else if (integer > -60) {
                    return "5米内";
                } else {
                    return "全范围";
                }
            }
        };
        int size = DensityUtils.dp2px(this, 15);
        final IDrawFormat<Integer> drawFormat3 = new TextImageDrawFormat<Integer>(size, size, 0) {
            @Override
            protected Context getContext() {
                return LogAllActivity.this;
            }

            @Override
            protected int getResourceID(Integer integer, String s, int i) {
                if (integer > -50) {
                    return R.drawable.ic_dis_fir;
                } else if (integer > -60) {
                    return R.drawable.ic_dis_sec;
                } else {
                    return R.drawable.ic_dis_thi;
                }
            }
        };
        Column<Integer> column3 = new Column<>("距离", "distance", format3, drawFormat3);
        TableData<Log> tableData = new TableData<Log>("", mLogs, column1, column2, column3);
        mStLog.setTableData(tableData);
        mStLog.setZoom(true);
        // 关闭左侧,和头部单元格
        mStLog.getConfig().setShowXSequence(false).setShowYSequence(false);
    }
}