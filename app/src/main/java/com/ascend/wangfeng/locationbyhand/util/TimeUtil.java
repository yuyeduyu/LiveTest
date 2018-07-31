package com.ascend.wangfeng.locationbyhand.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by fengye on 2017/8/21.
 * email 1040441325@qq.com
 */

public class TimeUtil {
    public static String getTime(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(time);
        return format.format(date);
    }
    public static String getTime(long time,String style){
        SimpleDateFormat format = new SimpleDateFormat(style);
        Date date = new Date(time);
        return format.format(date);
    }

    //获得本周一0点时间
    @SuppressLint("WrongConstant")
    public static int getTimesWeekmorning(){
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0,0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return (int) (cal.getTimeInMillis());
    }
    //获得本周日24点时间
    @SuppressLint("WrongConstant")
    public static int getTimesWeeknight(){
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH)
                , 0, 0,0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return (int) ((cal.getTime().getTime()+ (7 * 24 * 60 * 60 * 1000)));
    }
    //获得本月第一天0点时间
    @SuppressLint("WrongConstant")
    public static int getTimesMonthmorning(){
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH)
                , 0, 0,0);
        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return (int) (cal.getTimeInMillis());
    }
    //获得本月最后一天24点时间
    @SuppressLint("WrongConstant")
    public static int getTimesMonthnight(){
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH)
                , 0, 0,0);
        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return (int) (cal.getTimeInMillis());}

    //获得当天0点时间
    public static long getTimesmorning(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    //获得当天24点时间
    public static int getTimesnight(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return (int) (cal.getTimeInMillis());
    }

    //获得7天前0点时间戳
    public static long getLast7morning(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis()-(7 * 24 * 60 * 60 * 1000);
    }
    /**
     * 将数据固定为2位 不足补0
     * @author lishanhui
     * created at 2018-04-26 16:19
     */
    public static String formatTo2(long s) {
        String newString = String.format("%02d",s);
        return newString;
    }
    public static String formatToHour(long seconds) {
        String timeStr = "00:00:" + formatTo2(seconds);
        if (seconds > 60) {
            long second = seconds % 60;
            long min = seconds / 60;
            timeStr = "00:" + formatTo2(min) + ":" + formatTo2(second);
            if (min > 60) {
                min = (seconds / 60) % 60;
                long hour = (seconds / 60) / 60;
                timeStr = formatTo2(hour) + ":" + formatTo2(min) + ":" + formatTo2(second);
                if (hour > 24) {
                    hour = ((seconds / 60) / 60) % 24;
                    long day = (((seconds / 60) / 60) / 24);
                    timeStr = formatTo2(hour) + ":" + formatTo2(min) + ":" + formatTo2(second);
                }
            }
        }
        return timeStr;
    }
    /**
     * 根据时间戳获取当天时间刻度
     *
     * @author lishanhui
     * created at 2018-04-24 17:43
     */
    public static float formatToTime(long timestamp) {
        Date date = new Date(timestamp);
//        Log.e("datetime","时："+date.getHours()+"\n"+"分："+date.getMinutes()+"\n秒："+date.getSeconds());
        return date.getHours() * 60 * 60 + date.getMinutes() * 60 + date.getSeconds();
    }
}
