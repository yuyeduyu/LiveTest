package com.ascend.wangfeng.locationbyhand.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

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
}
