package com.ascend.wangfeng.locationbyhand.util;

import java.util.HashMap;

/**
 * Created by fengye on 2018/2/23.
 * email 1040441325@qq.com
 */

public class VirtualIdentityUtil {
    private static HashMap<Integer,String> types = new HashMap<>();
    static {
        types.put(1,"手机");
        types.put(2,"IMEI");
        types.put(3,"IMSI");
        types.put(4,"QQ");
        types.put(5,"微信");
        types.put(6,"淘宝");
        types.put(7,"微博");
        types.put(8,"百度");
        types.put(15,"大众点评");
        types.put(16,"京东");
        types.put(17,"优酷");
        types.put(18,"米聊");
        types.put(19,"携程");
        types.put(20,"陌陌");
        types.put(21,"唱吧");
        types.put(22,"滴滴打车");
        types.put(23,"飞信");
        types.put(24,"快的打车");
        types.put(25,"美团");
        types.put(26,"糯米");
        types.put(27,"土豆");
        types.put(49,"支付宝");
        types.put(50,"好友QQ");
    }
    public static String getType(Integer id){
        return types.get(id);
    }
}
