package com.ascend.wangfeng.locationbyhand.util.hashMap;

/**
 * Created by fengye on 2018/2/23.
 * email 1040441325@qq.com
 */

public class MapBean {
    private Object key;
    private String value;

    public MapBean(Object key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }
}
