package com.ascend.wangfeng.locationbyhand;

import com.ascend.wangfeng.locationbyhand.api.AppClient;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.AlarmMacListDo;

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

}
