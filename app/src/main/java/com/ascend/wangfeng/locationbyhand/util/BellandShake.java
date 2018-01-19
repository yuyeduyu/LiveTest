package com.ascend.wangfeng.locationbyhand.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import com.ascend.wangfeng.locationbyhand.R;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by fengye on 2016/9/26.
 * email 1040441325@qq.com
 * 铃声和震动提醒
 */
public class BellandShake{
    public static final String TAG = BellandShake.class.getName();
    private static boolean isRunning=false;

    /**
     *
     * @param time 播放时间(毫秒)
     * @param mode 播放模式
     */
    public static void open(final int time, int mode, final Context context){
        Log.i(TAG, "open: ");
        if (!isRunning) {
            isRunning=true;
                 /*   openBell(context, time);
                    openShake(context, time);*/
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    openBell(context,time);
                    openShake(context,time);
                }
            }.start();

        }

    }

    private static void openShake(Context context, int time) {
        Log.i(TAG, "openShake: ");
        final Vibrator vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
       /* 第一个参数，指代一个震动的频率数组。每两个为一组，每组的第一个为等待时间，第二个为震动时间。
        比如 [2000,500,100,400],会先等待2000毫秒，震动500，再等待100，震动400
         第二个参数，repest指代从 第几个索引（第一个数组参数） 的位置开始循环震动。
        会一直保持循环，我们需要用 vibrator.cancel()主动终止*/
        vibrator.vibrate(new long[]{300,500},0);
        TimerTask task= new TimerTask() {
            @Override
            public void run() {
                vibrator.cancel();
                isRunning=false;
            }
        };
        new Timer().schedule(task,time);

    }

    private static void openBell(Context context, int time) {

        final MediaPlayer player=new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                player.seekTo(0);//播放完毕后，重新指向流文件开头
                isRunning=false;
            }
        });
        final AssetFileDescriptor file = context.getResources().openRawResourceFd(
                R.raw.play);
        try {
            player.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(), file.getLength());
            file.close();
            // player.setVolume(BEEP_VOLUME, BEEP_VOLUME);
            player.prepare();
        } catch (IOException ioe) {

        }
        player.start();
        TimerTask task= new TimerTask() {
            @Override
            public void run() {
                player.stop();
                player.release();
                isRunning=false;
            }
        };
        new Timer().schedule(task,time);

    }


}
