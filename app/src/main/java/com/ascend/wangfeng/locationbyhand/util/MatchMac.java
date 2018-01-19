package com.ascend.wangfeng.locationbyhand.util;

/**
 * Created by fengye on 2017/4/27.
 * email 1040441325@qq.com
 * match mac
 */

public class MatchMac {
    /**
     * ,mac号最后一位会变动,因此判断相同时忽略最后一位
     * @param mac
     * @param standard
     * @return isSame
     *
     */
    public static boolean isSame(String mac,String standard){
      String macSub =  mac.substring(0,15);
      String standardSub =  standard.substring(0,15);
        if (macSub.equals(standardSub)){
        int macLast = Integer.parseInt(mac.substring(15, 17), 16);
        int standardLast = Integer.parseInt(standard.substring(15,17),16);
        if (Math.abs(macLast-standardLast)<=4) return true;
        }
        return  false;
    }
}
