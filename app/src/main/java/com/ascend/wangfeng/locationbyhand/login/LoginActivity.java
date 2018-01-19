package com.ascend.wangfeng.locationbyhand.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;
import com.ascend.wangfeng.locationbyhand.view.activity.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.login_imei)
    TextView mLoginImei;
    @BindView(R.id.login_edit)
    EditText mLoginEdit;
    @BindView(R.id.linearLayout)
    LinearLayout mLinearLayout;
    @BindView(R.id.activity_login)
    RelativeLayout mActivityLogin;
    private String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        imei = mTelephonyManager.getDeviceId();
    }

    private void initView() {
        mLoginImei.setText(imei);
    }

    @OnClick(R.id.login_submit)
    public void onClick() {
        if (mLoginEdit.getText().toString().isEmpty()) {
            Snackbar.make(mLoginImei, "未输入密码", Snackbar.LENGTH_SHORT).show();
        } else {
            TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String imei = mTelephonyManager.getDeviceId();
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
}
