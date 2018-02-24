package com.ascend.wangfeng.locationbyhand.util.hashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fengye on 2018/2/23.
 * email 1040441325@qq.com
 */

public class MapUtil {
    public static ArrayList<MapBean> toList(HashMap<Integer, String> map){
        ArrayList<MapBean> mapBeans = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : map.entrySet()){
            MapBean mapBean = new MapBean(entry.getKey(),entry.getValue());
            mapBeans.add(mapBean);
        }
        return mapBeans;
    }
}
