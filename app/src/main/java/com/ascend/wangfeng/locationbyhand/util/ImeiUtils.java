package com.ascend.wangfeng.locationbyhand.util;

import android.annotation.SuppressLint;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.login.LoginActivity;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * 作者：lish on 2018-08-09.
 * 描述：
 */

public class ImeiUtils {
    @SuppressLint("MissingPermission")
    public static String getImei(){
        String imei ="";
        TelephonyManager mTelephonyManager = (TelephonyManager) MyApplication.mContext.getSystemService(TELEPHONY_SERVICE);
        if (!TextUtils.isEmpty(mTelephonyManager.getDeviceId())) {
            imei = mTelephonyManager.getDeviceId();
        } else {
            //android.provider.Settings;
            imei = Settings.Secure.getString(MyApplication.mContext.getApplicationContext().getContentResolver()
                    , Settings.Secure.ANDROID_ID);
        }

        if (TextUtils.isEmpty(imei)) {
            imei = mTelephonyManager.getSimSerialNumber().substring(0, 15);
        }
        return imei;
    }
}
