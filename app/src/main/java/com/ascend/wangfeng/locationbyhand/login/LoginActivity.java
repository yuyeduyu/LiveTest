package com.ascend.wangfeng.locationbyhand.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.BuildConfig;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.util.ImeiUtils;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;
import com.ascend.wangfeng.locationbyhand.view.activity.BaseActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.PermissionListener;
import com.ascend.wxldcmenu.MenuMainActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.login_imei)
    TextView mLoginImei;
    @BindView(R.id.login_edit)
    EditText mLoginEdit;
    @BindView(R.id.linearLayout)
    LinearLayout mLinearLayout;
    @BindView(R.id.activity_login)
    RelativeLayout mActivityLogin;
    @BindView(R.id.name)
    TextView name;
    private String imei;
    private static PermissionListener mListener;

    @Override
    protected int setContentView() {
        return R.layout.activity_login;
    }

    private void initData() {
        imei = ImeiUtils.getImei();
        mLoginImei.setText(imei);
    }

    protected void initView() {
        name.setText(BuildConfig.AppName);
        getPermissions();
        if (MyApplication.isDev) {
            //测试版本，跳过登录
//            if (AppVersionConfig.appTitle.equals("便携式移动采集")) {
            //便携式车载采集系统
            startActivity(new Intent(LoginActivity.this, MenuMainActivity.class));
//            } else
//                startActivity(new Intent(LoginActivity.this, NewMainActivity.class));
            finish();
        } else {
            checkIsLogin();
        }
//        initData();
    }

    private void checkIsLogin() {
        String password = (String) SharedPreferencesUtils
                .getParam(getBaseContext(), "passwordOfApp", "null");
        if (!password.equals("null")) {
//            if (AppVersionConfig.appTitle.equals("便携式移动采集")) {
            //便携式车载采集系统
            startActivity(new Intent(LoginActivity.this, MenuMainActivity.class));
//            } else
//                startActivity(new Intent(LoginActivity.this, NewMainActivity.class));
            finish();
        }
    }

    @OnClick(R.id.login_submit)
    public void onClick() {
        if (mLoginEdit.getText().toString().isEmpty()) {
            Snackbar.make(mLoginImei, "未输入密码", Snackbar.LENGTH_SHORT).show();
        } else {
            /*TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String imei = mTelephonyManager.getDeviceId();*/
            String password = new Tea().createKey(imei);
            Log.i("tag", "onClick: " + password);
            if (mLoginEdit.getText().toString().equals(password)) {
                SharedPreferencesUtils.setParam(getBaseContext(), "passwordOfApp", password);
                //验证成功，跳转指定页面；
//                if (AppVersionConfig.appTitle.equals("便携式移动采集")) {
                //便携式车载采集系统
                startActivity(new Intent(LoginActivity.this, MenuMainActivity.class));
//                } else
//                    startActivity(new Intent(LoginActivity.this, NewMainActivity.class));
                finish();

            } else {
                Snackbar.make(mLoginImei, "密码错误", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.READ_PHONE_STATE"};

    /**
     * 获取动态权限
     *
     * @author lish
     * created at 2018-07-18 13:58
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {//判断当前系统是不是Android6.0
            requestRuntimePermissions(PERMISSIONS_STORAGE, new PermissionListener() {
                @Override
                public void granted() {
                    //权限申请通过
                    initData();
                }

                @Override
                public void denied(List<String> deniedList) {
                    //权限申请未通过
                    for (String denied : deniedList) {
                        Toast.makeText(LoginActivity.this, "未获取手机状态权限,生成帐号失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else initData();
    }
}
