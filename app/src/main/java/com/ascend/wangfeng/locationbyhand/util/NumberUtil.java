package com.ascend.wangfeng.locationbyhand.util;

/**
 * Created by fengye on 2018/2/23.
 * email 1040441325@qq.com
 */

public class NumberUtil {
    /**
     * 判断 n1 是否比 n2 大 range
     * @param n1 数字1
     * @param n2 数字2
     * @param range 允许误差范围
     * @return
     */
    public static boolean muchLarger(int n1, int n2, int range){
        if (n1 - n2 > range) return true;
        else return false;
    }
}
