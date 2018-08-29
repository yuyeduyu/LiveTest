package com.ascend.wangfeng.locationbyhand.util;

import android.text.TextUtils;
import android.util.Log;

import com.ascend.wangfeng.locationbyhand.MyApplication;

/**
 * 作者：lish on 2018-07-17.
 * 描述：
 */

public class LogUtils {
    public static String tagPrefix = "";
    public static boolean showV = true;
    public static boolean showD = true;
    public static boolean showI = true;
    public static boolean showW = true;
    public static boolean showE = MyApplication.isDev;
    public static boolean showWTF = true;

    /**
     * 得到tag（所在类.方法（L:行））
     * @return
     */
    private static String generateTag() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        String callerClazzName = stackTraceElement.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        String tag = "%s.%s(L:%d)";
        tag = String.format(tag, new Object[]{callerClazzName, stackTraceElement.getMethodName(), Integer.valueOf(stackTraceElement.getLineNumber())});
        //给tag设置前缀
        tag = TextUtils.isEmpty(tagPrefix) ? tag : tagPrefix + ":" + tag;
        return tag;
    }

    public static void v(String tag ,String msg) {
        if (showV) {
//            String tag = generateTag();
            Log.v(tag, msg);
        }
    }

    public static void v(String tag ,String msg, Throwable tr) {
        if (showV) {
//            String tag = generateTag();
            Log.v(tag, msg, tr);
        }
    }

    public static void d(String tag ,String msg) {
        if (showD) {
//            String tag = generateTag();
            Log.d(tag, msg);
        }
    }

    public static void d(String tag ,String msg, Throwable tr) {
        if (showD) {
//            String tag = generateTag();
            Log.d(tag, msg, tr);
        }
    }

    public static void i(String tag ,String msg) {
        if (showI) {
//            String tag = generateTag();
            Log.i(tag, msg);
        }
    }

    public static void i(String tag ,String msg, Throwable tr) {
        if (showI) {
//            String tag = generateTag();
            Log.i(tag, msg, tr);
        }
    }

    public static void w(String tag ,String msg) {
        if (showW) {
//            String tag = generateTag();
            Log.w(tag, msg);
        }
    }

    public static void w(String tag ,String msg, Throwable tr) {
        if (showW) {
//            String tag = generateTag();
            Log.w(tag, msg, tr);
        }
    }

    public static void e(String tag ,String msg) {
        if (showE) {
//            String tag = generateTag();
            Log.e(tag, msg);
        }
    }

    public static void e(String tag ,String msg, Throwable tr) {
        if (showE) {
//            String tag = generateTag();
            Log.e(tag, msg, tr);
        }
    }

    public static void wtf(String tag ,String msg) {
        if (showWTF) {
//            String tag = generateTag();
            Log.wtf(tag, msg);
        }
    }

    public static void wtf(String tag ,String msg, Throwable tr) {
        if (showWTF) {
//            String tag = generateTag();
            Log.wtf(tag, msg, tr);
        }
    }

}
