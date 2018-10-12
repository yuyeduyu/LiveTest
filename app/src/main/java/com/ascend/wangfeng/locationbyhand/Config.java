package com.ascend.wangfeng.locationbyhand;

import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.AlarmMacListDo;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.ble.MessageEvent;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by fengye on 2017/2/8.
 * email 1040441325@qq.com
 */

public class Config {
    public static String TAG = "Config";
    public static int C = 0;//无线雷达-c
    public static int C_MINI = 1;//无线雷达-mini
    public static int C_PLUS = 2;//无线雷达-cplus

    public static String timeTypeByYear = "yyyy-MM-dd";

    public static int SAVEDATATIME = 30;//日志保存时间

    public static String UpLoadFtpUrl = "47.92.210.239";                   //上传的地址
    public static int UpLoadFtpPort = 21;                                   //端口号
    public static String UpLoadFtpUser = "test123";                         //上传的地址
    public static String UpLoadFtpPass = "test123";                         //上传的地址
    public static String UpLoadFilePath = "tzwificar";                        //文件名

    public static String TargetUrl = "http://192.168.168.56:9086/";     //网络布控目标服务器地址

    public static String URL_YIDONG = "111.3.157.168"; //台州移动网段地址
    private static AlarmMacListDo alarmMacListDo;

    public static AlarmMacListDo getAlarmMacListDo() {
        if (alarmMacListDo == null) {
            updateConfig();
        }
        return alarmMacListDo;
    }

    public static String getTargetUrl() {
        if (MyApplication.isDev)
            //测试版本
            return TargetUrl;
        else
            return "http://"+MyApplication.UpLoadFtpUrl+":"+MyApplication.UpLoadFtpPort;
    }

    public static void updateConfig() {
        AppClient.getWiFiApi().getAlarmMacList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<AlarmMacListDo>() {
                    @Override
                    public void onNext(AlarmMacListDo aDo) {
                        alarmMacListDo = aDo;
                    }
                });
    }

    // 获取设置密码的ap-mac
    public static String getApPasswordMac() {
        return (String) SharedPreferencesUtils.getParam(MyApplication.mContext, "pa_ap_mac", "");
    }

    /**
     * 清空密码
     */
    public static void clearApPassword() {

        SharedPreferencesUtils.setParam(MyApplication.mContext, "pa_ap_mac", "");
        SharedPreferencesUtils.setParam(MyApplication.mContext, "pa_ap_name", "");
        SharedPreferencesUtils.setParam(MyApplication.mContext, "pa_ap_password", "");
        ;
    }

    /**
     * 设置AP密码
     */
    public static void setApPassword(String mac, String name, String password) {
        SharedPreferencesUtils.setParam(MyApplication.mContext, "pa_ap_mac", mac);
        SharedPreferencesUtils.setParam(MyApplication.mContext, "pa_ap_name", name);
        SharedPreferencesUtils.setParam(MyApplication.mContext, "pa_ap_password", password);
        if (password != null && mac != null) {
            MessageEvent event = new MessageEvent(MessageEvent.SEND_DATA);
            event.setData("ESSID:" + name);
            RxBus.getDefault().post(event);
        }
    }

    /**
     * 设备每次重启,会丢失密码
     * 向设备设置密码
     */
    public static void reLoadApPassword() {
        String mac = (String) SharedPreferencesUtils.getParam(MyApplication.mContext, "pa_ap_mac", "");
        String name = (String) SharedPreferencesUtils.getParam(MyApplication.mContext, "pa_ap_name", "");
        String password = (String) SharedPreferencesUtils.getParam(MyApplication.mContext, "pa_ap_password", "");
        if (!password.equals("") && !mac.equals("")) {
            MessageEvent event = new MessageEvent(MessageEvent.SEND_DATA);
            event.setData("ESSID:" + name);
            RxBus.getDefault().post(event);
        }
    }
}
