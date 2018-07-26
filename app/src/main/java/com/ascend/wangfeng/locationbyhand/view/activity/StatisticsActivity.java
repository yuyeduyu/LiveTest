package com.ascend.wangfeng.locationbyhand.view.activity;

import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.anye.greendao.gen.DaoSession;
import com.anye.greendao.gen.LogDao;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.StatisticAdapter;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.Log;
import com.ascend.wangfeng.locationbyhand.util.LogUtils;
import com.ascend.wangfeng.locationbyhand.util.TimeUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

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
    @BindView(R.id.line_chart)
    LineChart mLineChart;
    private List<Log> datas;
    private StatisticAdapter adapter;
    private LineData mLineData;

    private List<String> xDatas;
    static long oneDay = 24 * 60 * 60 * 1000;

    @Override
    protected int setContentView() {
        return R.layout.activity_statistics;
    }

    @Override
    protected void initView() {
        initTool();
        initRecyle();
        List<Integer> counts = getLast7Data(MyApplication.instances.getDaoSession());
        getXDatas();
        //初始化统计图表数据
        initMapSeries(counts);
        initMap();
    }

    private void getXDatas() {
        xDatas = new ArrayList<>();
        long time = TimeUtil.getTimesmorning();
        LogUtils.e("tiem", time + "");
        for (int i = 0; i < 7; i++) {
            xDatas.add(TimeUtil.getTime(time - ((6 - i) * oneDay), "MM/dd "));
        }
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

    //获取当天采集的mac 并根据mac过滤数据库重复的数据 查询语句
    private static final String SQL_DISTINCT_ENAME = "SELECT " + LogDao.Properties.Mac.columnName
            + "," + LogDao.Properties.Ltime.columnName + "," + LogDao.Properties.Type.columnName
            + " FROM " + LogDao.TABLENAME
            + " WHERE " + LogDao.Properties.Ltime.columnName + " >= " + TimeUtil.getTimesmorning()
            + " AND " + LogDao.Properties.AppVersion.columnName + " = " + MyApplication.getAppVersion()
            + " GROUP BY " + LogDao.Properties.Mac.columnName;

    /**
     * 获取当天采集的mac 并根据mac过滤数据库重复的数据
     *
     * @author lish
     * created at 2018-07-17 11:01
     */
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
                    result.add(log);
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return result;
    }

    /**
     * 获取最近7天采集的mac 并根据mac过滤数据库重复的数据
     *
     * @author lish
     * created at 2018-07-17 11:01
     */
    public List<Integer> getLast7Data(DaoSession session) {
        ArrayList<Integer> result = new ArrayList<>();
        Cursor c;
        for (int i = 0; i < 6; i++) {
            c = session.getDatabase().rawQuery(getSql(i), null);
            LogUtils.e("xDatas", c.getCount() + "");
            result.add(c.getCount());
        }
        result.add(datas.size());
        return result;
    }

    private static String getSql(int i) {
        //查询最近7天每天采集的mac数
        String SQL_COUNT_LAST_7 = "SELECT " + LogDao.Properties.Mac.columnName
                + "," + LogDao.Properties.Ltime.columnName + "," + LogDao.Properties.Type.columnName
                + " FROM " + LogDao.TABLENAME
                + " WHERE " + LogDao.Properties.Ltime.columnName + " BETWEEN " + (TimeUtil.getLast7morning() + (i * oneDay)) + " AND " + (TimeUtil.getTimesmorning() - ((5 - i) * oneDay))
                + " AND " + LogDao.Properties.AppVersion.columnName + " = " + MyApplication.getAppVersion()
                + " GROUP BY " + LogDao.Properties.Mac.columnName;
        return SQL_COUNT_LAST_7;
    }

    /**
     * 初始化折线图点集
     *
     * @param counts
     */
    private void initMapSeries(List<Integer> counts) {
        ArrayList<Entry> entrys = new ArrayList<Entry>();
        for (int i = 0; i < counts.size(); i++) {
            entrys.add(new Entry(i, counts.get(i)));
        }
        LineDataSet dataSet = new LineDataSet(entrys, "最近7天采集mac数");
        dataSet.setCircleColor(getResources().getColor(R.color.primary));
        dataSet.setLineWidth(2);
        //dataSet.setHighLightColor(getResources().getColor(R.color.accent));
        dataSet.setColor(getResources().getColor(R.color.primary));
        dataSet.setDrawFilled(true);
        dataSet.setHighlightEnabled(true);
        dataSet.setValueTextColor(ContextCompat.getColor(StatisticsActivity.this, R.color.c13)); //数值显示的颜色
        dataSet.setValueTextSize(8f);     //数值显示的大小
        dataSet.setFillAlpha(125);
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return -100;
            }
        });
        mLineData = new LineData();
        mLineData.addDataSet(dataSet);

    }

    /**
     * 初始化折线图
     */
    private void initMap() {
        mLineChart.getDescription().setEnabled(false);
        mLineChart.setTouchEnabled(false);
        mLineChart.setDragEnabled(false);
        mLineChart.setScaleEnabled(false);
        //设置网格线显示
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(false);
        xAxis.setAxisMinValue(0);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xDatas.get((int) value);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        YAxis yAxis = mLineChart.getAxisLeft();
        yAxis.setDrawLabels(true);
        yAxis.setDrawGridLines(true);
        yAxis.setAxisMinimum(0);
        //设置y轴最小间隔；
        YAxis yAxis1 = mLineChart.getAxisRight();
        yAxis1.setEnabled(false);
        yAxis.setLabelCount(5);
        yAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value > 10000)
                    return (int) value / 10000 + "万";
                return (int) value + "";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        mLineChart.animateX(1500);

        Legend legend = mLineChart.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);
        mLineChart.setData(mLineData);
    }

}
