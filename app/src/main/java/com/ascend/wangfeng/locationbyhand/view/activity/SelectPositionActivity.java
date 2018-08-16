package com.ascend.wangfeng.locationbyhand.view.activity;

import android.content.Intent;
import android.os.Bundle;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.Ghz;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.Log;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.ble.LineEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.MainServiceEvent;
import com.ascend.wangfeng.locationbyhand.util.ChannelConvert;
import com.ascend.wangfeng.locationbyhand.util.LogUtils;

import java.text.SimpleDateFormat;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SelectPositionActivity extends BaseActivity {
    private Subscription lineDataRxbus;
    private long lastTime;//上一次数据采集时间
    @Override
    protected int setContentView() {
        return R.layout.activity_select_position;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        String mac = intent.getStringExtra("mac");
        //todo 未设置 type值
        int type = intent.getIntExtra("type", 0);
        int channel = intent.getIntExtra("channel", 0);
        MainServiceEvent event = new MainServiceEvent(MainServiceEvent.LOCK);
        if (MyApplication.mGhz == Ghz.G24) {
            event.setChannel(channel);
        } else {
            event.setChannel(ChannelConvert.convertChannel(channel));
        }
        event.setMac(mac);
        event.setType(type);
        RxBus.getDefault().post(event);

        lineDataRxbus = RxBus.getDefault().toObservable(LineEvent.class)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<LineEvent>() {
                    @Override
                    public void onNext(LineEvent event) {
                        if (event.getType() == 0)
                            updateAp(event.getApVo());
                        else
                            updateSta(event.getStaVo());
                    }
                });
    }
    public void updateAp(ApVo data) {
        //若两次数据是同一时间采集,无需更新
        if (lastTime == data.getLtime()) return;
        lastTime = data.getLtime();

        update(lastTime, data.getSignal());
    }

    public void updateSta(StaVo data) {
        //若两次数据是同一时间采集,无需更新
        if (lastTime == data.getLtime()) return;
        lastTime = data.getLtime();

        update(lastTime, data.getSignal());
    }

    private void update(long lastTime, int signal) {
        LogUtils.e("signal",signal+"");
    }

}
