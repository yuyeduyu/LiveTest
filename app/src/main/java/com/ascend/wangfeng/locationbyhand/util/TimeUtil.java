package com.ascend.wangfeng.locationbyhand.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fengye on 2017/8/21.
 * email 1040441325@qq.com
 */

public class TimeUtil {
    public static String getTime(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        Date date = new Date(time);
        return format.format(date);
    }
}
