package com.ascend.wangfeng.locationbyhand.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anye.greendao.gen.TagLogDao;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.TagLog;
import com.ascend.wangfeng.locationbyhand.contract.LineContract;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.ble.LineEvent;
import com.ascend.wangfeng.locationbyhand.presenter.LinePresenterImpl;
import com.ascend.wangfeng.locationbyhand.util.LogUtils;
import com.ascend.wangfeng.locationbyhand.util.OuiDatabase;
import com.ascend.wangfeng.locationbyhand.util.TimeUtil;
import com.ascend.wangfeng.locationbyhand.util.chart.DayAxisValueFormatter;
import com.ascend.wangfeng.locationbyhand.util.chart.MyAxisValueFormatter;
import com.ascend.wangfeng.locationbyhand.view.activity.VirtualIdentityActivity;
import com.ascend.wangfeng.locationbyhand.view.myview.XYMarkerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

/**
 * Created by fengye on 2017/2/8.
 * email 1040441325@qq.com
 */

public class LineFragment extends BaseFragment implements LineContract.View {

    public static final String TAG = "LineFragment";
    public static final String NOMAC = "00:00:00:00:00:00";
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.name_title)
    TextView mNameTitle;
    @BindView(R.id.ApName)
    TextView mApName;
    @BindView(R.id.ltime)
    TextView mLtime;
    @BindView(R.id.Mac)
    TextView mMac;
    @BindView(R.id.signal)
    TextView mSignal;
    @BindView(R.id.channel)
    TextView mChannel;

    @BindView(R.id.line_chart)
    LineChart mLineChart;
    @BindView(R.id.message)
    TextView mMessage;
    @BindView(R.id.v_id)
    LinearLayout mVIdLayout;
    @BindView(R.id.chart1)
    BarChart BarChart;
    private String mac;
    private Integer type;
    private LinePresenterImpl mPresenter;
    private LineData mLineData;
    private float entryCount;
    private Subscription lineDataRxbus;
    private long lastTime;//上一次数据采集时间
    //保存图像数据集合，显示默认点数时使用
    private List<Entry> entries = new ArrayList<>();
    //图表x轴显示数据个数
    private int shownNum = 10;
    //图表x轴刻度间距倍数
    public static int Multiple = 1;
    private TagLogDao logDao;
    private List<TagLog> logs;
    private boolean isTag;//是否是布控目标
    ArrayList<BarEntry> barChartData = new ArrayList<BarEntry>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isTag = getArguments().getBoolean("tag");
        logDao = MyApplication.instances.getDaoSession().getTagLogDao();
        initData();
        initView();
        initBle();
        //mPresenter.update(mac, type);
    }

    private void initBle() {
        lineDataRxbus = RxBus.getDefault().toObservable(LineEvent.class)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<LineEvent>() {
                    @Override
                    public void onNext(LineEvent event) {
                        if (type == 0)
                            updateAp(event.getApVo());
                        else
                            updateSta(event.getStaVo());
                    }
                });
    }

    private void initData() {
        Intent intent = getActivity().getIntent();
        mac = intent.getStringExtra("mac");
        type = intent.getIntExtra("type", 0);
        //mPresenter = new LinePresenterImpl(this);
        initMapSeries();
    }

    private void initView() {
        initMap();
        //布控目标则显示最近一个小时采集信号
        if (isTag){
            BarChart.setVisibility(View.VISIBLE);
            long time = System.currentTimeMillis();
            logs = logDao
                    .queryBuilder()
                    .where(TagLogDao.Properties.Mac.eq(mac),
                            TagLogDao.Properties.Ltime.between(time - 3600 * 1000, time))
                    .orderAsc(TagLogDao.Properties.Ltime)
                    .list();
            LogUtils.e("tagLogs",logs.size()+"");
            initBraChart();
        }
        //oui匹配信息
        String re = OuiDatabase.ouiMatch(mac);
        mMessage.setText(re + "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lineDataRxbus != null) lineDataRxbus.unsubscribe();
        //mPresenter.stop();
    }

    /**
     * 初始化折线图点集
     */
    private void initMapSeries() {
        ArrayList<Entry> entrys = new ArrayList<Entry>();
        LineDataSet dataSet = new LineDataSet(entrys, mac);
        dataSet.setCircleColor(getResources().getColor(R.color.primary));
        dataSet.setLineWidth(2);
        //dataSet.setHighLightColor(getResources().getColor(R.color.accent));
        dataSet.setColor(getResources().getColor(R.color.primary));
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(125);
        dataSet.setFillFormatter(new IFillFormatter() {
            @Override
            public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                return -100;
            }
        });
        mLineData = new LineData();
        mLineData.addDataSet(dataSet);
        mLineChart.moveViewToX(mLineData.getXMax());
    }

    /**
     * 初始化折线图
     */
    private void initMap() {
//设置右下角描述
        Description description = new Description();
        description.setText("");
        mLineChart.setDescription(description);
        //设置坐标轴最大值
//        mLineChart.setMaxVisibleValueCount(10);
        mLineChart.setDrawGridBackground(false);
        mLineChart.setTouchEnabled(true);

        // enable scaling and dragging
        mLineChart.setDragEnabled(true);
//        mChart.setScaleEnabled(true);
        mLineChart.setScaleXEnabled(true);
        mLineChart.setScaleYEnabled(false);
        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mLineChart);
        XYMarkerView mv = new XYMarkerView(getActivity(), xAxisFormatter, 0);
        mv.setChartView(mLineChart); // For bounds control
        mLineChart.setMarker(mv); // Set the marker to the chart
        //设置网格线显示
        XAxis xAxis = mLineChart.getXAxis();
//        xAxis.setSpaceMin(5);
//        xAxis.setSpaceMax(5);
        xAxis.setDrawGridLines(false);
//        xAxis.setEnabled(true);
//        xAxis.setAxisMinValue(0);
//        xAxis.setLabelCount(5);//设置x轴显示的标签个数
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return TimeUtil.formatToHour((long) value);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        YAxis yAxis = mLineChart.getAxisLeft();
        yAxis.setDrawLabels(true);
        yAxis.setDrawGridLines(true);
        yAxis.setAxisMaximum(0);
        yAxis.setAxisMinimum(-100);
        yAxis.setLabelCount(11, false);
        //设置y轴最小间隔；
        yAxis.setGranularity(20);
        YAxis yAxis1 = mLineChart.getAxisRight();
        yAxis1.setEnabled(false);
        mLineChart.animateX(2000);

        Legend legend = mLineChart.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);
        mLineChart.setData(mLineData);
    }

    /**
     * 初始化 最近一小时mac数据 柱状图
     *
     * @author lish
     * created at 2018-07-30 14:56
     */
    private void initBraChart() {
        BarChart.setDrawBarShadow(false);
        BarChart.setDrawValueAboveBar(true);

        BarChart.getDescription().setEnabled(false);
        BarChart.setScaleYEnabled(false);
        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        BarChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        BarChart.setPinchZoom(false);

        BarChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(BarChart);

        XAxis xAxis = BarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setSpaceMax(30f);
//        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return TimeUtil.formatToHour((long) value * Multiple);
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        IAxisValueFormatter custom = new MyAxisValueFormatter();
        YAxis leftAxis = BarChart.getAxisLeft();
//        leftAxis.setTypeface(mTfLight);
//        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
//        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(100);

        BarChart.getAxisRight().setEnabled(false);

        Legend l = BarChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(getActivity(), xAxisFormatter, 1);
        mv.setChartView(BarChart); // For bounds control
        BarChart.setMarker(mv); // Set the marker to the chart
        float start = 1f;
        for (int i = (int) start; i < start + logs.size(); i++) {

            barChartData.add(new BarEntry(TimeUtil.formatToTime(logs.get(i - 1).getLtime() / Multiple), 100 + logs.get(i - 1).getDistance()));
        }
        setData(barChartData);
    }

    private void setData(ArrayList<BarEntry> logs) {


        BarDataSet set1;

        if (BarChart.getData() != null &&
                BarChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) BarChart.getData().getDataSetByIndex(0);
            set1.setValues(barChartData);
            BarChart.getData().notifyDataChanged();
            BarChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(barChartData, mac + ("(最近一小时采集信号)"));
//            set1.setDrawIcons(false);
            int[] MATERIAL_COLORS = {rgb("#e74c3c")};
            set1.setColors(MATERIAL_COLORS);
            set1.setDrawValues(false);  //不显示数值
            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
//            data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);

            BarChart.setData(data);
        }
    }

    @Override
    public void updateAp(ApVo data) {
        //若两次数据是同一时间采集,无需更新
        if (lastTime == data.getLtime()) return;
        lastTime = data.getLtime();
        mTitle.setText(data.getBssid() + (data.isTag() ? "(" + data.getNote() + ")" : ""));
        mApName.setText(data.getEssid() + "");
        mMac.setText(data.getBssid() + "");
        mChannel.setText(data.getChannel() + "");
        mSignal.setText(data.getSignal() + "dBm");
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time = format.format(data.getLtime());
        mLtime.setText(time + "");
        update(lastTime, data.getSignal());
    }


    @Override
    public void updateSta(StaVo data) {
        //若两次数据是同一时间采集,无需更新
        if (lastTime == data.getLtime()) return;
        lastTime = data.getLtime();
        mTitle.setText(data.getMac() + (data.isTag() ? "(" + data.getNote() + ")" : ""));
        if ("00:00:00:00:00:00".equals(data.getApmac())) {
            //未连接
            mApName.setText("未连接");
            mMac.setText("无");
            mChannel.setText("无");

        } else {
            if (data.getApmac().equals(NOMAC)) {
                //有连接信息，但Ap不在范围内，无ap信息
                mApName.setText("未知");
                mMac.setText(data.getApmac() + "");
                mChannel.setText("无");

            } else {
                //正常情况
                mApName.setText(data.getEssid());
                mMac.setText(data.getApmac() + "");
                mChannel.setText(data.getChannel() + "");
            }
        }
        //显示虚拟身份图标
        initVirtualIdentity(data);
        mSignal.setText(data.getSignal() + "dBm");
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time = format.format(data.getLtime());
        mLtime.setText(time);
        update(lastTime, data.getSignal());
    }

    /**
     * 显示虚拟身份
     *
     * @param data
     * @author lishanhui
     * created at 2018-06-29 15:54
     */
    private void initVirtualIdentity(final StaVo data) {
        final HashMap<Integer, String> identities = data.getIdentities();
        mVIdLayout.removeAllViews();
        if (identities != null && identities.size() > 0) {
            mVIdLayout.setVisibility(View.VISIBLE);
            mVIdLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转虚拟身份页面
                    Intent intent = new Intent(getActivity(),
                            VirtualIdentityActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("sta", data);
                    intent.putExtras(bundle);
                    getActivity().startActivity(intent);
                }
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            for (Map.Entry<Integer, String> entry : identities.entrySet()) {
                ImageView imageView = new ImageView(getActivity());
                imageView.setLayoutParams(lp);
                imageView.setPadding(0, 0, 0, 0);
                switch (entry.getKey()) {
                    case 1:
                        imageView.setImageResource(R.drawable.phone);
                        mVIdLayout.addView(imageView);
                        break;
                    case 49:
                        imageView.setImageResource(R.drawable.alipay);
                        mVIdLayout.addView(imageView);
                        break;
                    case 4:
                        imageView.setImageResource(R.drawable.qq);
                        mVIdLayout.addView(imageView);
                        break;
                    case 5:
                        imageView.setImageResource(R.drawable.wechat);
                        mVIdLayout.addView(imageView);
                        break;
                    case 6:
                        imageView.setImageResource(R.drawable.taobao);
                        mVIdLayout.addView(imageView);
                        break;
                    case 3:
                        imageView.setImageResource(R.drawable.sim_card);
                        mVIdLayout.addView(imageView);
                        break;
                    default:
                        break;
                }
            }
            if (mVIdLayout.getChildCount() < identities.size()) {
                ImageView imageView = new ImageView(getActivity());
                imageView.setLayoutParams(lp);
                imageView.setPadding(0, 0, 0, 0);
                imageView.setImageResource(R.drawable.more);
                mVIdLayout.addView(imageView);
            }
        } else {
            mVIdLayout.setVisibility(View.GONE);
        }
    }

    private void update(long lastTime, int signal) {
        Entry entry = new Entry(TimeUtil.formatToTime(lastTime), (float) signal);
        entries.add(entry);
        mLineData.addEntry(entry, 0);
        mLineChart.notifyDataSetChanged();
        mLineChart.invalidate();
        if (entries.size() > shownNum) {
            mLineChart.setVisibleXRangeMaximum(mLineChart.getData().getXMax() - entries.get(entries.size() - shownNum).getX());
            mLineChart.moveViewToX(entries.get(entries.size() - shownNum).getX());
        }
        mLineChart.setVisibleXRangeMaximum(mLineChart.getData().getXMax());
        //刷新柱状图
        barChartData.add(new BarEntry(TimeUtil.formatToTime(lastTime/ Multiple), 100 + signal));

        LogUtils.e("mult",TimeUtil.formatToHour((long) TimeUtil.formatToTime(lastTime/ Multiple) * Multiple));

        setData(barChartData);
//        BarChart.notifyDataSetChanged();
        BarChart.invalidate();
//        BarChart.setVisibleXRangeMaximum(BarChart.getData().getXMax());
    }

}
