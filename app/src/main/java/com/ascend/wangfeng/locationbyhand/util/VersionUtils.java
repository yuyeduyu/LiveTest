package com.ascend.wangfeng.locationbyhand.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.ascend.wangfeng.locationbyhand.R;

/**
 * 作者：lish on 2018-08-16.
 * 描述：获取版本信息
 */

public class VersionUtils {
    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return context.getString(R.string.version_name);
        }
    }
}
