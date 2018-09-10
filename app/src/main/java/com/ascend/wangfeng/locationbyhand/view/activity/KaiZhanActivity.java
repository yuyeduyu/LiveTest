package com.ascend.wangfeng.locationbyhand.view.activity;

import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
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
import com.ascend.wangfeng.locationbyhand.data.saveData.LocationData;
import com.ascend.wangfeng.locationbyhand.event.FTPEvent;
import com.ascend.wangfeng.locationbyhand.util.ImeiUtils;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtil;
import com.ascend.wangfeng.locationbyhand.util.citychoice.ChangeAddressPopwindow;

import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ascend.wangfeng.locationbyhand.util.CardCodeUtil.cardCodeVerifySimple;
import static com.ascend.wangfeng.locationbyhand.util.CardCodeUtil.isMobileNO;

public class KaiZhanActivity extends BaseActivity {

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

    @BindView(R.id.set_submit)
    Button setSubmit;
    @BindView(R.id.tv_dev)
    TextView tvDev;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_card)
    EditText etCard;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_minjing)
    EditText etMinjing;
    @BindView(R.id.tv_paichusuo)
    TextView tvPaichusuo;
    @BindView(R.id.ll_paichusuo)
    LinearLayout llPaichusuo;

    private double latitude;
    private double longitude;

    private String cityName;
    private String areaName;
    private String paichusuoCode;
    private String paichusuoName;

    @Override
    protected int setContentView() {
        return R.layout.activity_kai_zhan;
    }

    /**
     * 检查是否开过站
     *
     * @author lish
     * created at 2018-08-10 14:48
     */
    private void checkKaiZhan() {
        List<KaiZhanBean> devs = SharedPreferencesUtil.getList(KaiZhanActivity.this
                , "kaizhan");
        if (devs != null) {
            for (KaiZhanBean dev : devs) {
                if (dev.getMac().equals(MyApplication.mDevicdMac)) {
                    etName.setText(dev.getName());
                    etCard.setText(dev.getCard());
                    etPhone.setText(dev.getPhone());
                    etMinjing.setText(dev.getMinjing());
                    tvPaichusuo.setText(dev.getCity()+"  "+dev.getArea()+"  "+dev.getPaichusuo());
                    cityName = dev.getCity();
                    areaName = dev.getArea();
                    paichusuoName = dev.getPaichusuo();
                    latitude = dev.getLatitude();
                    longitude = dev.getLongitude();
                }
            }
        }
    }

    protected void initView() {
        EventBus.getDefault().register(this);
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mToolbar.setTitle("开站信息");
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
        tvDev.setText(MyApplication.mDevicdID);

        //检查是否开站
        checkKaiZhan();
    }
    @OnClick({R.id.ll_adress, R.id.set_submit, R.id.ll_paichusuo})
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
            case R.id.ll_paichusuo:
                //选择派出所
                shownPaichusuoSlect();
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
            Toast.makeText(KaiZhanActivity.this, "获取设备MAC失败，请退出重新连接设备", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(tvImei.getText().toString().trim())) {
            Toast.makeText(KaiZhanActivity.this, "获取手机IMEI失败，请检查相关权限是否开放", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(tvDev.getText().toString().trim())) {
            Toast.makeText(KaiZhanActivity.this, "获取设备编号失败，请退出重新连接设备", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etName.getText().toString().trim())) {
            Toast.makeText(KaiZhanActivity.this, "请输入姓名", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!TextUtils.isEmpty(etCard.getText().toString().trim()) & !isCard(etCard.getText().toString().trim())) {
            Toast.makeText(KaiZhanActivity.this, "输入身份证号或者警号有误", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(etPhone.getText().toString().trim())) {
            Toast.makeText(KaiZhanActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!isMobileNO(etPhone.getText().toString().trim())) {
            Toast.makeText(KaiZhanActivity.this, "输入手机号有误", Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (TextUtils.isEmpty(etMinjing.getText().toString().trim())) {
//            Toast.makeText(KaiZhanActivity.this, "请输入所属民警", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (TextUtils.isEmpty(cityName)){
            Toast.makeText(KaiZhanActivity.this, "请选择地址", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(paichusuoName)) {
            Toast.makeText(KaiZhanActivity.this, "请选择派出所", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (latitude == 0 || longitude == 0) {
            Toast.makeText(KaiZhanActivity.this, "获取经纬度失败，请检查定位权限是否开放", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 判断是否是身份证号 或者警号（8位）
     *
     * @author lish
     * created at 2018-08-23 15:50
     */
    private boolean isCard(String trim) {
        if (trim.length() == 8)
            return true;
        else
            return cardCodeVerifySimple(trim);
    }

    //点击上传文件
    private void UpLoad() {
        loadingDialog.show();
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
                    content.append("," + tvDev.getText().toString().trim());
                    content.append("," + tvImei.getText().toString().trim());
                    content.append("," + etName.getText().toString().trim());
                    content.append("," + (TextUtils.isEmpty(etCard.getText().toString().trim())
                            ? " " : etCard.getText().toString().trim()));
                    content.append("," + etPhone.getText().toString().trim());
                    content.append("," + (TextUtils.isEmpty(etMinjing.getText().toString().trim())
                            ?" ":etMinjing.getText().toString().trim()));
                    content.append("," + (cityName.equals("")?" ":cityName));
                    content.append("," + (areaName.equals("")?" ":areaName));
                    content.append("," + paichusuoName);

                    content.append("," + longitude);
                    content.append("," + latitude);
                    FileData fileData = new FileData(KaiZhanActivity.this, filePath, fileName + ".carsite", content);
                    final boolean sub = ftpClientData.ftpUpload(ftpClient, filePath, fileName + ".carsite");

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
     *
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
        bean.setDevCode(tvDev.getText().toString().trim());
        bean.setName(etName.getText().toString().trim());
        bean.setCard(etCard.getText().toString().trim());
        bean.setPhone(etPhone.getText().toString().trim());
        bean.setMinjing(etMinjing.getText().toString().trim());
        bean.setCity(cityName);
        bean.setArea(areaName);
        bean.setPaichusuo(paichusuoName);
        bean.setLatitude(latitude);
        bean.setLongitude(longitude);
        devs.add(bean);
        SharedPreferencesUtil.putList(KaiZhanActivity.this, "kaizhan", devs);
    }


    //城市选择器
    private void shownCitySlect() {
        WindowManager manager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;  //以要素为单位
        int height = metrics.heightPixels;
        ChangeAddressPopwindow mChangeAddressPopwindow =
                new ChangeAddressPopwindow(KaiZhanActivity.this, "city.json", "gbk",height);
        mChangeAddressPopwindow.setAddress("浙江", "杭州", "西湖区");
        mChangeAddressPopwindow.showAtLocation(llAdress, Gravity.BOTTOM, 0, 0);
        mChangeAddressPopwindow
                .setAddresskListener(new ChangeAddressPopwindow.OnAddressCListener() {

                    @Override
                    public void onClick(String province, String city, String area
                            , String provinceCode, String cityCode, String areaCode) {
//                        tvAdress.setText(province +"  "+ city +"  "+ area);
//                        cityName = city;
//                        areaName = area;
//                        adress = province +"  "+ city +"  "+ area;
                    }
                });
    }

    //派出所选择器
    private void shownPaichusuoSlect() {
        WindowManager manager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;  //以要素为单位
        int height = metrics.heightPixels;
        ChangeAddressPopwindow mChangeAddressPopwindow =
                new ChangeAddressPopwindow(KaiZhanActivity.this, "paichusuo.json", "utf-8",height);
//        mChangeAddressPopwindow.setAddress("台州市", "温岭市公安局", "太平派出所");
        mChangeAddressPopwindow.showAtLocation(llAdress, Gravity.BOTTOM, 0, 250);
        mChangeAddressPopwindow
                .setAddresskListener(new ChangeAddressPopwindow.OnAddressCListener() {

                    @Override
                    public void onClick(String province, String city, String area
                            , String provinceCode, String cityCode, String areaCode) {
                        tvPaichusuo.setText(province +"  "+ city +"  "+ area);
                        paichusuoCode = areaCode;
                        paichusuoName = area;
                        cityName = province;
                        areaName = city;
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
