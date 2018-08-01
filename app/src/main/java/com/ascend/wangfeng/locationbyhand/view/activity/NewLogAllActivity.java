package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.anye.greendao.gen.LogDao;
import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.AllLogAdapter;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.Log;
import com.ascend.wangfeng.locationbyhand.util.LogUtils;
import com.ascend.wangfeng.locationbyhand.util.MacUtils;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler2;

//监测日志
public class NewLogAllActivity extends AppCompatActivity {
    @BindView(R.id.store_house_ptr_frame)
    PtrClassicFrameLayout storeHousePtrFrame;
    @BindView(R.id.filter_edit)
    EditText filterEdit;
    @BindView(R.id.search)
    ImageView search;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private String TAG = getClass().getCanonicalName();

    ArrayList<Log> mLogs = new ArrayList<>();
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.sb_choose)
    SwitchButton mSbChoose;
    LogDao logDao;
    private int pager = 1;//数据库查询页数
    private int pagerSize = 100;//数据库每次查询数量
    private int type = 1;//1:sta  0:ap
    private AllLogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_log_all_new);
        ButterKnife.bind(this);
        logDao = MyApplication.getInstances().getDaoSession().getLogDao();
        initView();
        initRecyview();
        initRefresh();
    }

    private void initRecyview() {
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AllLogAdapter(this, mLogs);
        recyclerview.setAdapter(adapter);
        recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
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
                if (TextUtils.isEmpty(filterEdit.getText().toString())){
                    //上拉加载所有数据
                    List<Log> Logs = (ArrayList<Log>) logDao.queryBuilder()
                            .where(LogDao.Properties.Type.eq(type)
                                    , LogDao.Properties.AppVersion.eq(MyApplication.getAppVersion()))
                            .orderDesc(LogDao.Properties.Ltime)
                            .offset(pager * pagerSize)
                            .limit(pagerSize)
                            .list();          //1:sta  0:ap
                    mLogs.addAll(Logs);
                    if (Logs.size()<1){
                        Toast.makeText(NewLogAllActivity.this, "日志全部加载完成", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                    pager++;
                }else {
                    //上拉加载 指定mac采集数据
                }

                storeHousePtrFrame.refreshComplete();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //下拉刷新
                if (TextUtils.isEmpty(filterEdit.getText().toString())) {
                    //查询所有数据
                    getAllLog();
                } else {
                    //查询指定mac的采集数据
                    getLogByMac();
                }
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
        storeHousePtrFrame.setDurationToCloseHeader(500);
        // default is false
        storeHousePtrFrame.setPullToRefresh(false);
        // default is true
        storeHousePtrFrame.setKeepHeaderWhenRefresh(true);
        //进入界面刷新加载数据
        storeHousePtrFrame.autoRefresh();
    }

    /**
     * 查询指定mac的采集数据
     * @author lish
     * created at 2018-08-01 16:12
     */
    private void getLogByMac() {
        pager = 1;
        mLogs.clear();
        if (!MacUtils.CheckMac(filterEdit.getText().toString().trim())) {
            Toast.makeText(NewLogAllActivity.this, "请输入正确的mac", Toast.LENGTH_SHORT).show();
        } else {
            //请求数据
            //mac格式标准化 00-00-00-00-00-00
            String mac = MacUtils.formatMac(filterEdit.getText().toString().trim());
            android.util.Log.e("mac_oui", mac);
            String addressMac = mac.replaceAll("-", ":");
            addressMac=addressMac.toUpperCase();
            LogUtils.e("addressMac",addressMac);
            List<Log> Logs = (ArrayList<Log>) logDao.queryBuilder()
                    .where(LogDao.Properties.AppVersion.eq(MyApplication.getAppVersion())
                            , LogDao.Properties.Mac.eq(addressMac))
                    .orderDesc(LogDao.Properties.Ltime)
                    .list();          //1:sta  0:ap
            mLogs.addAll(Logs);
            if (Logs.size()>0){
                type = Logs.get(0).getType();
                mSbChoose.setChecked(type==1?false:true);
            }else {
                Toast.makeText(NewLogAllActivity.this, "没有该MAC的日志", Toast.LENGTH_SHORT).show();
            }
        }
        adapter.notifyDataSetChanged();
        storeHousePtrFrame.refreshComplete();
    }

    /**
     * 未指定mac，则分页查询所有数据
     *
     * @author lish
     * created at 2018-08-01 16:10
     */
    private void getAllLog() {
        pager = 1;
        mLogs.clear();
        List<Log> Logs = (ArrayList<Log>) logDao.queryBuilder()
                .where(LogDao.Properties.Type.eq(type)
                        , LogDao.Properties.AppVersion.eq(MyApplication.getAppVersion()))
                .orderDesc(LogDao.Properties.Ltime)
                .offset(pager * pagerSize)
                .limit(pagerSize)
                .list();          //1:sta  0:ap
        mLogs.addAll(Logs);
        pager++;
        adapter.notifyDataSetChanged();
        storeHousePtrFrame.refreshComplete();
        if (Logs.size()<1){
            Toast.makeText(NewLogAllActivity.this, "没有日志记录", Toast.LENGTH_SHORT).show();
        }
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
        type = i;
        storeHousePtrFrame.autoRefresh();
    }

    @OnClick(R.id.search)
    public void onViewClicked() {
        storeHousePtrFrame.autoRefresh();
    }
}
