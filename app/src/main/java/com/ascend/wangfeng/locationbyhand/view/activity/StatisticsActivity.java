package com.ascend.wangfeng.locationbyhand.view.activity;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anye.greendao.gen.DaoSession;
import com.anye.greendao.gen.LogDao;
import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.StatisticAdapter;
import com.ascend.wangfeng.locationbyhand.adapter.StatisticBySelectAdapter;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.Log;
import com.ascend.wangfeng.locationbyhand.util.CustomDatePickerUtils.CustomDatePicker;
import com.ascend.wangfeng.locationbyhand.util.CustomDatePickerUtils.GetDataUtils;
import com.ascend.wangfeng.locationbyhand.util.LogUtils;
import com.ascend.wangfeng.locationbyhand.util.TimeUtil;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

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
    @BindView(R.id.bar_chart)
    BarChart barChart;
    @BindView(R.id.tv_time)
    TextView tvTime;
    private List<Log> datas;//当天采集mac
    private List<Map<String, String>> selectDatas;//查询时间段采集mac
    private List<Integer> aps;//最近7天采集ap数
    private List<Integer> stas;//最近7天采集终端数
    private StatisticAdapter todayadapter;
    private StatisticBySelectAdapter selectAdapter;
    private LineData mLineData;

    private List<String> xDatas;
    static long oneDay = 24 * 60 * 60 * 1000;

    private String startTime = "";
    private String endTime = "";
    private CustomDatePicker startTimePicker, endTimePicker;

    private DaoSession daoSession;

    private boolean isFirst = true;//为true则设置柱状图数据

    @Override
    protected int setContentView() {
        return R.layout.activity_statistics;
    }

    @Override
    protected void onResume() {
        super.onResume();
        daoSession = MyApplication.instances.getDaoSession();
        //初始化时间选择控件
        initDatePicker();
        //显示今天采集不重复的mac信息
//        initRecyleForTodayMac();
        //显示查询时间段 ap和终端数量
        initRecyleBySelect();
        //折线图显示最近7天统计
//        initLineChart();
        //柱状图显示最近7天统计
        initBarChart();
        searchMacData(startTime, endTime);
    }

    @Override
    protected void initView() {
        initTool();
    }

    /**
     * 根据查询时间段 显示每天采集的终端和ap数量
     *
     * @author lish
     * created at 2018-08-14 10:18
     */
    private void initRecyleBySelect() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(StatisticsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        selectDatas = new ArrayList<>();
        selectAdapter = new StatisticBySelectAdapter(selectDatas, StatisticsActivity.this);
        recycler.setLayoutManager(layoutManager);
        recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recycler.setAdapter(selectAdapter);
    }

    /**
     * 柱状图显示最近7天统计
     *
     * @author lish
     * created at 2018-08-13 10:17
     */
    private void initBarChart() {
        //获取x轴坐标数据
        getXDatas();
        //获取Y轴  柱状图数据 从查询数据中计算，节省查询数据库时间
//        List<Integer> aps = getLast7Data(daoSession);
        initBraChart();

    }

    /**
     * 初始化柱状图
     *
     * @author lish
     * created at 2018-08-13 11:36
     */
    private void initBraChart() {

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setScaleEnabled(false);
        barChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        barChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setCenterAxisLabels(true);//设置标签居中
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value >= 0 & value <= xDatas.size()-1)
                    return xDatas.get((int) value);
                else return "";
//                return value+"";
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(5, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
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

        barChart.getAxisRight().setEnabled(false);

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

//        setData(aps);
    }

    /**
     * 设置柱状图数据
     *
     * @author lish
     * created at 2018-08-13 11:35
     */
    private void setData(List<Integer> aps, List<Integer> stas) {

        float start = 1f;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();

        for (int i = (int) start; i < start + aps.size(); i++) {
            yVals1.add(new BarEntry(i, aps.get(i - 1)));
            yVals2.add(new BarEntry(i, stas.get(i - 1)));
        }

        BarDataSet set1, set2;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) barChart.getData().getDataSetByIndex(1);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "AP数");
            set2 = new BarDataSet(yVals2, "终端数");

//            set1.setColors(ColorTemplate.MATERIAL_COLORS);
//            set2.setColors(ColorTemplate.MATERIAL_COLORS);
            set1.setColor(rgb("#2ecc71"));
            set2.setColor(rgb("#e74c3c"));

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            dataSets.add(set2);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);

            barChart.setData(data);
        }
        float groupSpace = 0.2f;
        float barSpace = 0f;
        float barWidth = 0.4f;
        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinimum(0);
        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
//        float groupSpace = 0.4f;
//        float barSpace = 0f; // x3 DataSet
//        float barWidth = 0.2f; // x3 DataSet
//        // (0.2 + 0) * 3 + 0.4 = 1.00 -> interval per "group"
        barChart.getXAxis().setAxisMaximum(barChart.getBarData().getGroupWidth(groupSpace, barSpace) * 7 + 0);
        barChart.groupBars(0, groupSpace, barSpace);
    }

    /**
     * 折线图显示最近7天统计
     *
     * @author lish
     * created at 2018-08-13 9:43
     */
    private void initLineChart() {
        List<Integer> counts = getLast7Data(daoSession);
        getXDatas();
        //初始化统计图表数据
        initMapSeries(counts);
        initMap();
    }

    /**
     * x轴坐标数据
     *
     * @author lish
     * created at 2018-08-13 10:27
     */
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

    /**
     * 显示今天采集不重复的mac信息
     *
     * @author lish
     * created at 2018-08-13 11:49
     */
    private void initRecyleForTodayMac() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(StatisticsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        datas = getData(daoSession);

        todayadapter = new StatisticAdapter(datas, StatisticsActivity.this);
        recycler.setLayoutManager(layoutManager);
        recycler.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recycler.setAdapter(todayadapter);
    }

    //获取当天采集的mac 并根据mac过滤数据库重复的数据 查询语句
    private static final String SQL_DISTINCT_ENAME = "SELECT " + LogDao.Properties.Mac.columnName
            + "," + LogDao.Properties.Ltime.columnName + "," + LogDao.Properties.Type.columnName
            + " FROM " + LogDao.TABLENAME
            + " WHERE " + LogDao.Properties.Ltime.columnName + " >= " + TimeUtil.getTimesmorning()
            + " AND " + LogDao.Properties.AppVersion.columnName + " = " + MyApplication.getAppVersion()
//            + " AND " + LogDao.Properties.Type.columnName + " = "+ 0
            + " GROUP BY " + LogDao.Properties.Mac.columnName;

    /**
     * 查询最近N天每天采集的mac数
     *
     * @param i             =0,表示 距今天 前days天
     * @param startLongTime 查询开始时间戳
     * @param type          -1:ap和终端   0:ap, 1:sta
     * @author lish
     * created at 2018-08-13 16:21
     */
    private static String getSql(int i, long startLongTime, long endLongTime, int diff, int type) {
        String SQL_COUNT_LAST_7 = "SELECT " + LogDao.Properties.Mac.columnName
                + "," + LogDao.Properties.Ltime.columnName + "," + LogDao.Properties.Type.columnName
                + " FROM " + LogDao.TABLENAME
                + " WHERE " + LogDao.Properties.Ltime.columnName
                + " BETWEEN " + (startLongTime + (i * oneDay))
                + " AND " + (endLongTime - ((diff - i) * oneDay))
                + (type == -1 ? "" : " AND " + LogDao.Properties.Type.columnName + " = " + type)
                + " AND " + LogDao.Properties.AppVersion.columnName + " = " + MyApplication.getAppVersion()
                + " GROUP BY " + LogDao.Properties.Mac.columnName;
        return SQL_COUNT_LAST_7;
    }

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
        //7天前0点时间戳
        long startLongTime = TimeUtil.getLastmorning(7 - 1);
        long endLongTime = TimeUtil.getTimesmorning();
        for (int i = 0; i < 7; i++) {
            c = session.getDatabase().rawQuery(getSql(i, startLongTime, endLongTime, 5, -1), null);
            result.add(c.getCount());
        }
        return result;
    }

    /**
     * 初始化折线图点集
     *
     * @param counts 最近7天采集mac数量集合
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

    /**
     * 初始化时间选择控件
     *
     * @author lish
     * created at 2018-08-13 13:42
     */
    private String startData;
    private String lastData;

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        startTime = GetDataUtils.getDateStrByDay(now, -6);
        endTime = now.split(" ")[0];
        tvTime.setText("时间段:" + startTime + " 至 " + endTime);
        startData = GetDataUtils.getDateStrByMint(now, -30);
        lastData = GetDataUtils.getDateStrByMint(now, 0);
        startTimePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                startTime = time.split(" ")[0];
                tvTime.setText("时间段:" + startTime + " 至 ");
                showEndTimePicker();
            }
        }, "2018-01-01 00:00", lastData); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        startTimePicker.showSpecificTime(false); // 不显示时和分
        startTimePicker.setIsLoop(false); // 不允许循环滚动
        startTimePicker.setTitle("请选择开始时间");
    }

    /**
     * 结束时间选择器
     *
     * @author lish
     * created at 2018-08-13 15:53
     */
    private void showEndTimePicker() {
        endTimePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                endTime = time.split(" ")[0];
                tvTime.setText("时间段:" + startTime + " 至 " + endTime);
                searchMacData(startTime, endTime);
            }
        }, startTime + " 00:00", lastData); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        endTimePicker.showSpecificTime(false); // 显示时和分
        endTimePicker.setIsLoop(false); // 允许循环滚动
        endTimePicker.setTitle("请选择结束时间");
        endTimePicker.show(startTime);
    }

    /**
     * 根据时间 查询mac采集数
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param startTime
     * @param endTime
     * @author lish
     * created at 2018-08-13 15:54
     */
    private void searchMacData(final String startTime, final String endTime) {
        mBaseActivity.showDialog(true);
        //开始 结束时间差，同一天 则 diffDays = 0;
        final int diffDays = GetDataUtils.differentDaysByMillisecond(startTime, endTime, Config.timeTypeByYear);
        if (diffDays == -1) {
            Toast.makeText(this, "计算时间差错误,请重新选择时间", Toast.LENGTH_SHORT).show();
            return;
        } else if (diffDays > 30) {
            Toast.makeText(this, "查询时间差不能大于31天，请重新选择时间", Toast.LENGTH_SHORT).show();
            return;
        }
        selectDatas.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getByTimeData(daoSession, startTime, endTime, diffDays + 1);
            }
        }).start();
    }

    /**
     * 根据开始结束时间 查询过滤重复mac数
     *
     * @author lish
     * created at 2018-07-17 11:01
     */
    public void getByTimeData(DaoSession session, String startTime, String endTime, int diffDays) {
        aps = new ArrayList<>();
        stas = new ArrayList<>();
        Cursor c;
        //查询条件 开始 结束的0点时间戳
        long startLongTime = GetDataUtils.getLongTimeByDay(startTime, Config.timeTypeByYear);
        long endLongTime = GetDataUtils.getLongTimeByDay(endTime, Config.timeTypeByYear);
        for (int i = 0; i < diffDays; i++) {
            Map<String, String> map = new HashMap<>();
            c = session.getDatabase().rawQuery(getSql(i, startLongTime, endLongTime, diffDays - 2, 0), null);
            map.put("ap", String.valueOf(c.getCount()));

            c = session.getDatabase().rawQuery(getSql(i, startLongTime, endLongTime, diffDays - 2, 1), null);
            map.put("sta", String.valueOf(c.getCount()));
            map.put("time", String.valueOf(startLongTime + (i * oneDay)));

            aps.add(Integer.valueOf(map.get("ap")));
            stas.add(Integer.valueOf(map.get("sta")));
            selectDatas.add(map);
        }
        //子线程中查询数据，则跳转到UI线程刷新界面
        mBaseActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                selectAdapter.notifyDataSetChanged();
                //第一次运行时，设置图形数据，点击查询时不需要设置图形数据
                if (isFirst) {
                    setData(aps,stas);
                    isFirst = false;
                    barChart.invalidate();
                }
                mBaseActivity.showDialog(false);
            }
        });
    }

    @OnClick(R.id.tv_time)
    public void onViewClicked() {
        startTimePicker.show(startTime);
    }
}
