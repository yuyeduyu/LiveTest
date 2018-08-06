package com.ascend.wangfeng.locationbyhand;

/**
 * 作者：lish on 2018-08-06.
 * 描述：便携式车载采集  与无线雷达版本区别配置
 */

public class AppVersionConfig {
    public static int WXLDC = 1;//无线雷达C
    public static int WXLDMENU = 2;//无线雷达-便携式车载采集
//        public static int VERSION = WXLDMENU;
    public static int VERSION = WXLDC;

    public static String WXLDCVERSIONTXT = "wxldCVersion.txt";
    public static String WXLDCAPPNAME = "wxldC.apk";
    public static String WXLDMENUVERSIONTXT = "wxldMenuVersion.txt";
    public static String WXLDMENUAPPNAME = "wxldMenu.apk";
}
