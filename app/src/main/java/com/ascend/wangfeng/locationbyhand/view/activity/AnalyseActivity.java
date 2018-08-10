package com.ascend.wangfeng.locationbyhand.view.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.AnalyseAdapter;
import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.HistoryMacBean;
import com.ascend.wangfeng.locationbyhand.dialog.LoadingDialog;
import com.ascend.wangfeng.locationbyhand.resultBack.AdressResult;
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
    EditText filterEdit;
    @BindView(R.id.search)
    ImageView search;
    @BindView(R.id.ll_oui)
    LinearLayout llOui;
    @BindView(R.id.ll_adress)
    LinearLayout llAdress;
    @BindView(R.id.ll_ap)
    LinearLayout llAp;
    private String mac = "";
    private List<HistoryMacBean> datas;
    private AnalyseAdapter adapter;
    public LoadingDialog loadingDialog; //上传dialog
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
        loadingDialog = new LoadingDialog(this);
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

    }

    /**
     * 根据mac获取经纬度
     *
     * @author lishanhui
     * created at 2018-07-03 15:35
     * @param mac
     */
    private void getAdresss(long mac) {
        if (!loadingDialog.isShowing()){
            loadingDialog.show();
        }
        AppClient.getAppVersionApi().getAdress(mac).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<AdressResult>() {
                    @Override
                    public void onNext(AdressResult vo) {
                        if (vo.getCode() == 200 & vo.isFlag()) {
                            if (vo.getData().getLongitude() != null) {
                                llAdress.setVisibility(View.VISIBLE);
                                latitude.setText("纬度:" + vo.getData().getLatitude());
                                longitude.setText("经度:" + vo.getData().getLongitude());
                                adress.setText(vo.getData().getAddr());
                            } else {
                                llAdress.setVisibility(View.VISIBLE);
                                longitude.setText("未查询到该经纬度");
                            }
                        } else {
                            llAdress.setVisibility(View.VISIBLE);
                            Toast.makeText(AnalyseActivity.this, "获取经纬度失败", Toast.LENGTH_SHORT).show();
                        }
                        if (loadingDialog!=null)
                            loadingDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        llAdress.setVisibility(View.VISIBLE);
                        Toast.makeText(AnalyseActivity.this, "获取经纬度失败", Toast.LENGTH_SHORT).show();
                        if (loadingDialog!=null)
                            loadingDialog.dismiss();
                    }
                });
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
        clearText();
        if (!MacUtils.CheckMac(filterEdit.getText().toString().trim())) {
            Toast.makeText(AnalyseActivity.this, "请输入正确的mac", Toast.LENGTH_SHORT).show();
        } else {
            //请求数据
            //mac格式标准化 00-00-00-00-00-00
            String mac = MacUtils.formatMac(filterEdit.getText().toString().trim());
            Log.e("mac_oui", mac);
            setOui(mac);
            //getOui();
            String addressMac = mac.replaceAll("-", "");
            getAdresss(Long.parseLong(addressMac,16));
        }
    }
    /**
     * 点击搜索 清空原有数据
     * @author lish
     * created at 2018-07-25 10:24
     */
    private void clearText() {
        tvOui.setText("");
        longitude.setText("");
        latitude.setText("");
        adress.setText("");
    }

    private void setOui(String mac) {
        Log.e("setOuimac",mac.replaceAll("-", ":"));
        String re = OuiDatabase.ouiMatch(mac.replaceAll("-", ":"));
        llOui.setVisibility(View.VISIBLE);
        line_oui.setVisibility(View.VISIBLE);
        tvOui.setText(re == null ? "未查询到厂商信息" : re);
    }

}
