package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.data.FTPClientData;

import org.apache.commons.net.ftp.FTPClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetftpActivity extends AppCompatActivity {

    private String TAG = getClass().getCanonicalName();
    private final int SUCCESS = 1;
    private final int FAIL = 2;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.set_url)
    EditText setUrl;
    @BindView(R.id.set_port)
    EditText setPort;
    @BindView(R.id.set_user)
    EditText setUser;
    @BindView(R.id.password)
    EditText setPassword;
    @BindView(R.id.set_path)
    EditText setPath;
    @BindView(R.id.set_submit)
    Button setSubmit;

    private boolean isClick = false;
    private String url;
    private int port;
    private String user;
    private String password;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setftp);
        ButterKnife.bind(this);
        initView();
        initData();
    }


    private void initView() {
        mToolbar.setTitle("设置FTP参数");
        mToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //数据是使用Intent返回
                Intent intent = new Intent();
                //把返回数据存入Intent
                intent.putExtra("result", "My name is linjiqin");
                SetftpActivity.this.setResult(1, intent);
                finish();
            }
        });
    }


    private void initData() {
        SharedPreferences preferences = SetftpActivity.this.getSharedPreferences("ftpData",
                Context.MODE_PRIVATE);
        if (preferences.getString("url", "") != "" ||
                preferences.getString("user", "") != "" ||
                preferences.getString("password", "") != "" ||
                preferences.getString("path", "") != "") {
            setUrl.setText(preferences.getString("url", ""));
            setPort.setText(preferences.getInt("port", -1) + "");
            setUser.setText(preferences.getString("user", ""));
            setPassword.setText(preferences.getString("password", ""));
            setPath.setText(preferences.getString("path", ""));
        } else {
            setUrl.setText(preferences.getString("url", ""));
            setPort.setText("");
            setUser.setText(preferences.getString("user", ""));
            setPassword.setText(preferences.getString("password", ""));
            setPath.setText(preferences.getString("path", ""));
        }
    }

    @OnClick(R.id.set_submit)
    public void onClick() {
        if (!isClick) {
            isClick = true;
            save();
        } else {
            Toast.makeText(SetftpActivity.this, "正在配置ftp,请稍后", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean save() {//保存信息
        if (TextUtils.isEmpty(setUrl.getText()) ||
                TextUtils.isEmpty(setPort.getText()) ||
                TextUtils.isEmpty(setUser.getText()) ||
                TextUtils.isEmpty(setPassword.getText()) ||
                TextUtils.isEmpty(setPath.getText())
                ) {
            Toast.makeText(SetftpActivity.this, "请完善信息", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            url = setUrl.getText().toString();
            port = Integer.parseInt(setPort.getText().toString());
            user = setUser.getText().toString();
            password = setPassword.getText().toString();
            path = setPath.getText().toString();

            upLoad();                           //提交一个文档检测是否可以提交ftp成功，以此判断配置信息是否有误
            return true;
        }
    }

    private void upLoad() {
        //检测保存信息是否正确
        new Thread(new Runnable() {
            @Override
            public void run() {
                FTPClientData ftpClientData = new FTPClientData(url, port, user, password, path);
                FTPClient ftpClient = ftpClientData.ftpConnect();
                Looper.prepare();
                if (ftpClient != null) {
                    //上传成功---》保存配置信息
                    SharedPreferences preferences = SetftpActivity.this.getSharedPreferences("ftpData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    MyApplication.ftpData();
                    editor.putString("url", url);
                    editor.putInt("port", port);
                    editor.putString("user", user);
                    editor.putString("password", password);
                    editor.putString("path", path);
                    editor.commit();
                    Toast.makeText(SetftpActivity.this, "ftp服务器配置成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(1,intent);
                    finish();
                } else {
                    //上传失败，信息有问题
                    Toast.makeText(SetftpActivity.this, "ftp服务器配置信息有误，请重新配置", Toast.LENGTH_SHORT).show();
                    isClick = false;
                }
                Looper.loop();
            }
        }).start();
    }
}
