package com.ascend.wangfeng.locationbyhand.keeplive;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ascend.wangfeng.locationbyhand.util.LogUtils;

/**
 * 作者：lish on 2018-08-03.
 * 描述：
 */

public class LiveService extends Service {

    public  static void toLiveService(Context pContext){
        Intent intent=new Intent(pContext,LiveService.class);
        pContext.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //屏幕关闭的时候启动一个1像素的Activity，开屏的时候关闭Activity
        final ScreenManager screenManager = ScreenManager.getInstance(LiveService.this);
        ScreenBroadcastListener listener = new ScreenBroadcastListener(this);
        listener.registerListener(new ScreenBroadcastListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                screenManager.finishActivity();
                Log.e("screenManager","Receive开屏");
            }
            @Override
            public void onScreenOff() {
                screenManager.startActivity();
                Log.e("screenManager","Receive关闭");
            }
        });
        return START_REDELIVER_INTENT;
    }
}