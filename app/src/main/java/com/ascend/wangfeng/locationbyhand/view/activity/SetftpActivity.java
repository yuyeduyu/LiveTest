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
import com.ascend.wangfeng.locationbyhand.bean.KaiZhanBean;
import com.ascend.wangfeng.locationbyhand.data.FTPClientData;
import com.ascend.wangfeng.locationbyhand.event.FTPEvent;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtil;

import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

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
    private int from;// 1.为开站界面跳转
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setftp);
        ButterKnife.bind(this);
        from = getIntent().getIntExtra("from",-1);
        preferences = SetftpActivity.this.getSharedPreferences("ftpData",
                Context.MODE_PRIVATE);
        initView();
        initData();
        if (from==1){
            setSubmit.setText("下一步");
            //设置过IP 则直接跳到 开站界面
            if (!preferences.getString("url", "").equals("")){
                startActivity(new Intent(SetftpActivity.this,KaiZhanActivity.class));
                finish();
            }
        }
    }


    private void initView() {
        mToolbar.setTitle("设置服务器");
        mToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                back();
                finish();
            }
        });
    }

    private void back() {
        //清除开站信息
        delectKaizhanInfo();
        if (from==1){
            //开站
            MyApplication.ftpData(); //更新服务器地址
            startActivity(new Intent(SetftpActivity.this,KaiZhanActivity.class));
        }else {
            //设置服务器地址
            Intent intent = new Intent();
            SetftpActivity.this.setResult(1, intent);
            EventBus.getDefault().post(new FTPEvent(true));
            MyApplication.ftpConnect = true;
        }
        finish();
    }
    /**
     * 清除开站数据
     * @author lish
     * created at 2018-08-22 15:44
     */
    private void delectKaizhanInfo() {
        List<KaiZhanBean> devs = SharedPreferencesUtil.getList(SetftpActivity.this
                , "kaizhan");
        if (devs == null)
            devs = new ArrayList<>();
        devs.clear();
        SharedPreferencesUtil.putList(SetftpActivity.this, "kaizhan", devs);
    }


    private void initData() {
            setUrl.setText(preferences.getString("url", MyApplication.UpLoadFtpUrl));
            setPort.setText(preferences.getInt("port", MyApplication.UpLoadFtpPort)+"");
            setUser.setText(preferences.getString("user", MyApplication.UpLoadFtpUser));
            setPassword.setText(preferences.getString("password", MyApplication.UpLoadFtpPass));
            setPath.setText(preferences.getString("path", MyApplication.UpLoadFilePath));
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
                TextUtils.isEmpty(setPassword.getText())
//                || TextUtils.isEmpty(setPath.getText())
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
                    back();
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
