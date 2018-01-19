package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class AboutActivity extends AppCompatActivity {

    public static final int APP_ID = 2;
    @BindView(R.id.toolbar)
    Toolbar mAppBar;
    @BindView(R.id.about_img)
    ImageView mAboutImg;
    @BindView(R.id.about_version)
    TextView mAboutVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initialView();

    }

    private void initialView() {
        mAppBar.setTitle("关于我们");
        setSupportActionBar(mAppBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        mAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mAboutVersion.setText(this.getString(R.string.version) + getVersion().toString());
        mAboutVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkVersion();
            }
        });
    }

    private void checkVersion() {
        AppClient.getAppVersionApi().getLatestVersion(APP_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (getVersionNo() < integer)
                            Snackbar.make(mAppBar, "存在最新版本", Snackbar.LENGTH_INDEFINITE).
                                    setAction("更新", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //更新软件
                                            updateApk();
                                        }
                                    }).show();
                        else {
                            Snackbar.make(mAppBar, "当前版本已经是最新版本", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void updateApk() {
        AppClient.getAppVersionApi().getApkUrl(APP_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<String>() {
                    @Override
                    public void onNext(String s) {
                        Snackbar.make(mAppBar,"正在更新",Snackbar.LENGTH_INDEFINITE).show();
                        downApk(s);
                    }
                });



    }
    private void downApk(String s) {
        AppClient.getAppVersionApi().UpdateApk(s)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Snackbar.make(mAppBar,"更新失败",Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(ResponseBody body) {
                        Log.i("a", "onNext: ");
                        try {
                            // todo change the file location/name according to your needs
                            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath()
                                    + "/AscendLog/LocationShow/");
                            File futureStudioIconFile = new File(dir, "location.apk");

                            InputStream inputStream = null;
                            OutputStream outputStream = null;

                            try {
                                byte[] fileReader = new byte[4096];

                                long fileSize = body.contentLength();
                                long fileSizeDownloaded = 0;

                                inputStream = body.byteStream();
                                outputStream = new FileOutputStream(futureStudioIconFile);

                                while (true) {
                                    int read = inputStream.read(fileReader);

                                    if (read == -1) {
                                        break;
                                    }

                                    outputStream.write(fileReader, 0, read);

                                    fileSizeDownloaded += read;

                                }

                                outputStream.flush();
                                Intent intent = new Intent();
                                //执行动作
                                intent.setAction(Intent.ACTION_VIEW);
                                //执行的数据类型
                                intent.setDataAndType(Uri.fromFile(futureStudioIconFile), "application/vnd.android.package-archive");
                                startActivity(intent);

                                return;
                            } catch (IOException e) {
                                return;
                            } finally {
                                if (inputStream != null) {
                                    inputStream.close();
                                }

                                if (outputStream != null) {
                                    outputStream.close();
                                }
                            }
                        } catch (IOException e) {
                            return;
                        }
                    }
                });
    }

    private Integer getVersionNo() {
        Integer version = 0;
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            version = info.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;

    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.version_name);
        }
    }
}