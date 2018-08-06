package com.ascend.wangfeng.locationbyhand.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.dialog.LoadingDialog;
import com.ascend.wangfeng.locationbyhand.resultBack.AppVersionBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：lish on 2018-07-24.
 * 描述：
 */

public class AppVersionUitls {
    public static Integer getVersionNo(Context context) {
        Integer version = 0;
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            version = info.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;

    }
    /**
     * 检测app版本是否需要更新
     *
     * @author lish
     * created at 2018-07-24 11:57
     */
    public static void checkVersion(final Context context, String appVersionTxt, final String appName, final LoadingDialog loadingDialog) {
        AppClient.getAppVersionApi().getAppVersion(appVersionTxt)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AppVersionBack>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("getAppVersion:", e.toString());
                        Toast.makeText(context, "获取版本信息失败", Toast.LENGTH_LONG).show();
                        if (loadingDialog!=null) loadingDialog.dismiss();
                    }

                    @Override
                    public void onNext(AppVersionBack appVersion) {
                        if (loadingDialog!=null) loadingDialog.dismiss();
                        if (AppVersionUitls.getVersionNo(context) < appVersion.getData().getVersionCode()) {
                            SharedPreferencesUtils.setParam(context, "appVersion", true);
                            shownUpdataDialog(context,appVersion.getData().getDes(),appName);
                        } else
                            SharedPreferencesUtils.setParam(context, "appVersion", false);
                    }
                });
    }

    /**
     * 更新dialog
     *
     * @author lish
     * created at 2018-07-24 12:35
     */
    public static void shownUpdataDialog(final Context context, String des, final String appName) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setTitle("版本更新");
        normalDialog.setMessage(des);
        normalDialog.setCancelable(false);
        normalDialog.setPositiveButton("下载安装",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        downApk(context,appName);
                        dialog.dismiss();
                    }
                });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        dialog.dismiss();
                    }
                });
        // 显示
        normalDialog.show();
    }

    public static void downApk(final Context context,String appName) {
        AppClient.getAppVersionApi().updateApp(appName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(context, "更新失败", Toast.LENGTH_LONG).show();
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
                                context.startActivity(intent);

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
}
