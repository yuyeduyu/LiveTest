package com.ascend.wangfeng.locationbyhand.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;
import com.ascend.wangfeng.locationbyhand.view.activity.BaseActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.MainActivity;
import com.ascend.wangfeng.locationbyhand.view.activity.PermissionListener;

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
    private String imei;
    private static PermissionListener mListener;

    @Override
    protected int setContentView() {
        return R.layout.activity_login;
    }

    @SuppressLint("MissingPermission")
    private void initData() {
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (!TextUtils.isEmpty(mTelephonyManager.getDeviceId())) {
            imei = mTelephonyManager.getDeviceId();
        } else {
            //android.provider.Settings;
            imei = Settings.Secure.getString(LoginActivity.this.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        if (TextUtils.isEmpty(imei)) {
            imei = mTelephonyManager.getSimSerialNumber().substring(0, 15);
        }
        mLoginImei.setText(imei);
    }

    protected void initView() {
        getPermissions();
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
                SharedPreferencesUtils.setParam(getBaseContext(),"passwordOfApp",password);
                //验证成功，跳转指定页面；
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();

            } else {
                Snackbar.make(mLoginImei, "密码错误", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
    private static String[] PERMISSIONS_STORAGE = {
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
        }
    }
    /**
     * 申请权限
     */
    public static void requestRuntimePermissions(
            String[] permissions, PermissionListener listener) {
        mListener = listener;
        List<String> permissionList = new ArrayList<>();
        // 遍历每一个申请的权限，把没有通过的权限放在集合中
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            } else {
                mListener.granted();
            }
        }
        // 申请权限
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    permissionList.toArray(new String[permissionList.size()]), 1);
        }
    }

    /**
     * 申请后的处理
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            List<String> deniedList = new ArrayList<>();
            // 遍历所有申请的权限，把被拒绝的权限放入集合
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    mListener.granted();
                } else {
                    deniedList.add(permissions[i]);
                }
            }
            if (!deniedList.isEmpty()) {
                mListener.denied(deniedList);
            }
        }
    }
}
