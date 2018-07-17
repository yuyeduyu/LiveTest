package com.ascend.wangfeng.locationbyhand.view.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ascend.wangfeng.locationbyhand.view.service.UploadService;

/**
 * Created by Administrator on 2018/5/9.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, UploadService.class));
    }
}
