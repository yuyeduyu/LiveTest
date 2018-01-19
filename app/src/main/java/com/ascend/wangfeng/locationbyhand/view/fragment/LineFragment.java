package com.ascend.wangfeng.locationbyhand.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.contract.LineContract;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.ble.LineEvent;
import com.ascend.wangfeng.locationbyhand.presenter.LinePresenterImpl;
import com.ascend.wangfeng.locationbyhand.util.OuiDatabase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    private String mac;
    private Integer type;
    private LinePresenterImpl mPresenter;
    private LineData mLineData;
    private float entryCount;
    private Subscription lineDataRxbus;
    private long lastTime;//上一次数据采集时间

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
        initData();
        initView();
        initBle();
        //mPresenter.update(mac, type);
    }

    private void initBle() {
        lineDataRxbus=RxBus.getDefault().toObservable(LineEvent.class)
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
        //oui匹配信息
        String re = OuiDatabase.ouiMatch(mac);
        mMessage.setText(re + "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lineDataRxbus!=null)lineDataRxbus.unsubscribe();
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
        mLineChart.setMaxVisibleValueCount(10);

        //设置网格线显示
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setSpaceMin(5);
        xAxis.setSpaceMax(5);
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(false);
        xAxis.setAxisMinValue(0);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis yAxis = mLineChart.getAxisLeft();
        yAxis.setDrawLabels(true);
        yAxis.setDrawGridLines(true);
        yAxis.setAxisMaximum(0);
        yAxis.setAxisMinimum(-100);
        yAxis.setLabelCount(11, false);
        //设置y轴最小间隔；
        yAxis.setGranularity(10);
        YAxis yAxis1 = mLineChart.getAxisRight();
        yAxis1.setEnabled(false);
        mLineChart.animateX(2000);

        Legend legend = mLineChart.getLegend();
        legend.setEnabled(true);
        legend.setWordWrapEnabled(true);
        mLineChart.setData(mLineData);
    }

    @Override
    public void updateAp(ApVo data) {
        //若两次数据是同一时间采集,无需更新
        if (lastTime==data.getLtime())return;
        lastTime =data.getLtime();
        mTitle.setText(data.getBssid() + (data.isTag() ? "(" + data.getNote() + ")" : ""));
        mApName.setText(data.getEssid() + "");
        mMac.setText(data.getBssid() + "");
        mChannel.setText(data.getChannel() + "");
        mSignal.setText(data.getSignal() + "dBm");
        SimpleDateFormat format =  new SimpleDateFormat("HH:mm:ss");
        String time = format.format(data.getLtime());
        mLtime.setText(time + "");
        update(data.getSignal());
    }


    @Override
    public void updateSta(StaVo data) {
        //若两次数据是同一时间采集,无需更新
        if (lastTime==data.getLtime())return;
        lastTime =data.getLtime();
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


        mSignal.setText(data.getSignal() + "dBm");
        SimpleDateFormat format =  new SimpleDateFormat("HH:mm:ss");
        String time = format.format(data.getLtime());
        mLtime.setText(time);
        update(data.getSignal());
    }

    private void update(int signal) {

        mLineData.addEntry(new Entry(entryCount, (float) signal), 0);
        mLineChart.notifyDataSetChanged();
        mLineChart.setVisibleXRangeMaximum(20);
        mLineChart.moveViewToX(mLineData.getEntryCount() - 1);
        mLineChart.invalidate();
        entryCount++;
    }
}
