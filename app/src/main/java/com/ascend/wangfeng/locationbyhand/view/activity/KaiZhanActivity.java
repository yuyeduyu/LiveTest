package com.ascend.wangfeng.locationbyhand.view.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.bean.KaiZhanBean;
import com.ascend.wangfeng.locationbyhand.data.FTPClientData;
import com.ascend.wangfeng.locationbyhand.data.FileData;
import com.ascend.wangfeng.locationbyhand.data.saveData.ApData;
import com.ascend.wangfeng.locationbyhand.data.saveData.LocationData;
import com.ascend.wangfeng.locationbyhand.data.saveData.StaConInfo;
import com.ascend.wangfeng.locationbyhand.data.saveData.StaData;
import com.ascend.wangfeng.locationbyhand.dialog.LoadingDialog;
import com.ascend.wangfeng.locationbyhand.event.FTPEvent;
import com.ascend.wangfeng.locationbyhand.util.ImeiUtils;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtil;
import com.ascend.wangfeng.locationbyhand.util.citychoice.ChangeAddressPopwindow;
import com.ascend.wxldcmenu.MenuMainActivity;

import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KaiZhanActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_devmac)
    TextView tvDevmac;
    @BindView(R.id.tv_imei)
    TextView tvImei;
    @BindView(R.id.tv_adress)
    TextView tvAdress;
    @BindView(R.id.ll_adress)
    LinearLayout llAdress;
    @BindView(R.id.et_adress)
    EditText etAdress;
    @BindView(R.id.set_submit)
    Button setSubmit;

    private double latitude;
    private double longitude;

    private String cityCode;//市code
    private String areaCode; //区code
    public LoadingDialog loadingDialog; //上传dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kai_zhan);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        loadingDialog = new LoadingDialog(this);

        //检查是否开站
        checkKaiZhan();
    }

    /**
     * 检查是否开过站
     * @author lish
     * created at 2018-08-10 14:48
     */
    private void checkKaiZhan() {
        List<KaiZhanBean> devs = SharedPreferencesUtil.getList(KaiZhanActivity.this
                ,"kaizhan");
        if(devs!=null){
            for (KaiZhanBean dev:devs){
                if (dev.getMac().equals(MyApplication.mDevicdMac)){
                    tvAdress.setText(dev.getArea());
                    etAdress.setText(dev.getAdress());
                    cityCode = dev.getCityCode();
                    areaCode = dev.getAreaCode();
                    latitude = dev.getLatitude();
                    longitude = dev.getLongitude();
                }
            }
        }
    }

    private void initView() {
        mToolbar.setTitle("开站信息采集");
        mToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvDevmac.setText(MyApplication.mDevicdMac);
        tvImei.setText(ImeiUtils.getImei());
    }

    @OnClick({R.id.ll_adress, R.id.set_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_adress:
                //城市选择器
                shownCitySlect();
                break;
            case R.id.set_submit:
                if (checkSubmit())
                    UpLoad();
                break;
        }
    }

    /**
     * 提交数据验证
     *
     * @author lish
     * created at 2018-08-09 16:13
     */
    private boolean checkSubmit() {
        if (TextUtils.isEmpty(tvDevmac.getText().toString().trim())) {
            Toast.makeText(KaiZhanActivity.this, "获取设备编号失败，请退出重新连接设备", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(tvImei.getText().toString().trim())) {
            Toast.makeText(KaiZhanActivity.this, "获取手机IMEI失败，请检查相关权限是否开放", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(cityCode) || TextUtils.isEmpty(areaCode)) {
            Toast.makeText(KaiZhanActivity.this, "请选择地址", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etAdress.getText().toString().trim())) {
            Toast.makeText(KaiZhanActivity.this, "请填写详细地址", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (latitude == 0 || longitude == 0) {
            Toast.makeText(KaiZhanActivity.this, "获取经纬度失败，请检查定位权限是否开放", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //点击上传文件
    private void UpLoad() {
        if (MyApplication.mDevicdID != null) {                               //连接成功
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long time = (System.currentTimeMillis() / 1000);
//                Log.e(TAG,"启动服务时间:"+time);
                    String filePath = "/mnt/sdcard/";
                    String fileName = MyApplication.mDevicdID + "[211.211.211.211]_" + time;

                    FTPClientData ftpClientData = new FTPClientData();

                    FTPClient ftpClient = ftpClientData.ftpConnect();
                    if (ftpClient == null) {
                        EventBus.getDefault().post(new FTPEvent(false));
                        KaiZhanActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
                                if (loadingDialog != null)
                                    loadingDialog.dismiss();
                                Toast.makeText(KaiZhanActivity.this, "连接FTP服务器失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;
                    } else EventBus.getDefault().post(new FTPEvent(true));
                    try {
                        ftpClient.makeDirectory(MyApplication.UpLoadFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //上传开站数据
                    StringBuffer content = new StringBuffer();
                    content.append(tvDevmac.getText().toString().trim().replaceAll(":", ""));
                    content.append("," + tvAdress.getText().toString().trim() + etAdress.getText().toString().trim());
                    content.append("," + cityCode);
                    content.append("," + areaCode);
                    content.append("," + tvImei.getText().toString().trim());
                    content.append("," + longitude);
                    content.append("," + latitude);
                    FileData fileData = new FileData(KaiZhanActivity.this, filePath, fileName, content);
                    final boolean sub = ftpClientData.ftpUpload(ftpClient, filePath, fileName, "carsite");

                    (KaiZhanActivity.this).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
                            loadingDialog.dismiss();
                            if (sub) {
                                Toast.makeText(KaiZhanActivity.this, "开站信息上传成功", Toast.LENGTH_SHORT).show();
                                saveKaiZhanInfo();

                                finish();
                            } else
                                Toast.makeText(KaiZhanActivity.this, "开站信息上传失败，请重新提交", Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }).start();
        }
    }

    /**
     * 开站数据上传成功后 保存开站数据
     * @author lish
     * created at 2018-08-10 14:39
     */
    private void saveKaiZhanInfo() {
        List<KaiZhanBean> devs = SharedPreferencesUtil.getList(KaiZhanActivity.this
                , "kaizhan");
        if (devs == null)
            devs = new ArrayList<>();
        KaiZhanBean bean = new KaiZhanBean();
        bean.setMac(tvDevmac.getText().toString().trim());
        bean.setImei(tvImei.getText().toString().trim());
        bean.setArea(tvAdress.getText().toString().trim());
        bean.setAdress(etAdress.getText().toString().trim());
        bean.setCityCode(cityCode);
        bean.setAreaCode(areaCode);
        bean.setLatitude(latitude);
        bean.setLongitude(longitude);
        devs.add(bean);
        SharedPreferencesUtil.putList(KaiZhanActivity.this, "kaizhan", devs);
    }


    //城市选择器
    private void shownCitySlect() {
        ChangeAddressPopwindow mChangeAddressPopwindow = new ChangeAddressPopwindow(KaiZhanActivity.this);
//        mChangeAddressPopwindow.setAddress("浙江", "杭州", "西湖区");
        mChangeAddressPopwindow.showAtLocation(llAdress, Gravity.BOTTOM, 0, 0);
        mChangeAddressPopwindow
                .setAddresskListener(new ChangeAddressPopwindow.OnAddressCListener() {

                    @Override
                    public void onClick(String province, String city, String area, String provinceCode) {
                        tvAdress.setText(province + city + area);
                        cityCode = "666666";
                        areaCode = "666666";
                    }
                });
    }

    //获取经纬度信息
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEventLocation(LocationData locationData) {
        this.latitude = locationData.getLatitude();
        this.longitude = locationData.getLongitude();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
