package com.ascend.wangfeng.locationbyhand.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.AnalyseAdapter;
import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.HistoryMacBean;
import com.ascend.wangfeng.locationbyhand.util.MacUtils;
import com.ascend.wangfeng.locationbyhand.util.OuiDatabase;
import com.ascend.wangfeng.locationbyhand.view.myview.ClearEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 数据分析
 *
 * @author lishanhui
 *         created at 2018-07-03 14:02
 */
public class AnalyseActivity extends BaseActivity {
    private View line_oui;
    private View line_adress;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_oui)
    TextView tvOui;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.longitude)
    TextView longitude;
    @BindView(R.id.latitude)
    TextView latitude;
    @BindView(R.id.adress)
    TextView adress;
    @BindView(R.id.filter_edit)
    ClearEditText filterEdit;
    @BindView(R.id.search)
    TextView search;
    @BindView(R.id.ll_oui)
    LinearLayout llOui;
    @BindView(R.id.ll_adress)
    LinearLayout llAdress;
    @BindView(R.id.ll_ap)
    LinearLayout llAp;
    private String mac = "";
    private List<HistoryMacBean> datas;
    private AnalyseAdapter adapter;

    @Override
    protected int setContentView() {
        return R.layout.activity_analyse;
    }

    @Override
    protected void initView() {
        initTool();
        line_oui = findViewById(R.id.line_oui);
        line_adress = findViewById(R.id.line_adress);
        initRecyle();

        filterEdit.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 输入的内容变化的监听
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // 输入前的监听

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 输入后的监听
                llAdress.setVisibility(View.GONE);
                llAp.setVisibility(View.GONE);
                llOui.setVisibility(View.GONE);
                line_adress.setVisibility(View.GONE);
                line_oui.setVisibility(View.GONE);
            }
        });
//        tvOui.setText("HUAWEI");
//        latitude.setText("纬度:xxxxxxxxxxxx");
//        longitude.setText("经度:xxxxxxxxxxxx");
//        adress.setText("具体地址:xxxxxxxxxxxx");
//        getOui();
    }

    /**
     * 根据mac获取oui
     *
     * @author lishanhui
     * created at 2018-07-03 15:35
     */
    private void getOui() {
        AppClient.getWiFiApi().getOui(mac).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<String>() {
                    @Override
                    public void onNext(String vo) {
                        tvOui.setText(vo);
                    }
                });
    }

    private void initRecyle() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(AnalyseActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        datas = new ArrayList<>();
        for (int i = 0; i < 22; i++) {
            HistoryMacBean bean = new HistoryMacBean();
            bean.setMac("00:11:11:11:11");
            bean.setTime("2018/06/29");
            datas.add(bean);
        }
        adapter = new AnalyseAdapter(datas);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
    }

    /**
     * 初始化 toolbar
     */
    private void initTool() {
        mToolbar.setTitle("数据分析");
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


    @OnClick(R.id.search)
    public void onViewClicked() {
        if (!MacUtils.CheckMac(filterEdit.getText().toString().trim())) {
            Toast.makeText(AnalyseActivity.this, "请输入正确的mac", Toast.LENGTH_SHORT).show();
        } else {
            //请求数据
            //mac格式标准化 00-00-00-00-00-00
           String mac = MacUtils.formatMac(filterEdit.getText().toString().trim());
            Log.e("mac_oui",mac);
            setOui(mac);
        }
    }

    private void setOui(String mac) {
        String re = OuiDatabase.ouiMatch(mac.replaceAll("-",":"));
        llOui.setVisibility(View.VISIBLE);
        line_oui.setVisibility(View.VISIBLE);
        tvOui.setText(re==null?"未查询到厂商信息":re);
    }

}
