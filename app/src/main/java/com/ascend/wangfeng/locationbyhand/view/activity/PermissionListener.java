package com.ascend.wangfeng.locationbyhand.view.activity;

import java.util.List;

/**
 * 作者：lish on 2018-07-18.
 * 描述：
 */

public interface PermissionListener {
    void granted();
    void denied(List<String> deniedList);
}