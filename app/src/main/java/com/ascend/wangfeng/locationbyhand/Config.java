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
    public static String TAG="Config";
    private static AlarmMacListDo alarmMacListDo;

    public static AlarmMacListDo getAlarmMacListDo() {
        if (alarmMacListDo==null){
            updateConfig();
        }
        return alarmMacListDo;
    }
    public static void updateConfig(){
        AppClient.getWiFiApi().getAlarmMacList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<AlarmMacListDo>() {
                    @Override
                    public void onNext(AlarmMacListDo aDo) {
                        alarmMacListDo=aDo;
                    }
                });
    }
    // 获取设置密码的ap-mac
    public static String getApPasswordMac(){
        return (String) SharedPreferencesUtils.getParam(MyApplication.mContext,"pa_ap_mac","");
    }
    /**
     * 清空密码
     */
    public static void clearApPassword(){
        SharedPreferencesUtils.setParam(MyApplication.mContext,"pa_ap_mac","");
        SharedPreferencesUtils.setParam(MyApplication.mContext,"pa_ap_name","");
        SharedPreferencesUtils.setParam(MyApplication.mContext,"pa_ap_password","");;
    }
    /**
     * 设置AP密码
     */
    public static void setApPassword(String mac,String name ,String password){
        SharedPreferencesUtils.setParam(MyApplication.mContext,"pa_ap_mac",mac);
        SharedPreferencesUtils.setParam(MyApplication.mContext,"pa_ap_name",name);
        SharedPreferencesUtils.setParam(MyApplication.mContext,"pa_ap_password",password);
        if (password!=null&&mac!=null){
            MessageEvent event = new MessageEvent(MessageEvent.SEND_DATA);
            event.setData("ESSID:" +name);
            RxBus.getDefault().post(event);
        }
    }
    /**
     * 设备每次重启,会丢失密码
     * 向设备设置密码
     */
    public static void reLoadApPassword(){
        String mac = (String)SharedPreferencesUtils.getParam(MyApplication.mContext,"pa_ap_mac",null);
        String name = (String)SharedPreferencesUtils.getParam(MyApplication.mContext,"pa_ap_name",null);
        String password = (String)SharedPreferencesUtils.getParam(MyApplication.mContext,"pa_ap_password",null);
        if (password!=null&&mac!=null){
            MessageEvent event = new MessageEvent(MessageEvent.SEND_DATA);
            event.setData("ESSID:" +name);
            RxBus.getDefault().post(event);
        }
    }
}
