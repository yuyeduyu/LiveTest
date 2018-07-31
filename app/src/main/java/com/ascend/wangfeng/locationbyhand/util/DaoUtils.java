package com.ascend.wangfeng.locationbyhand.util;

import com.anye.greendao.gen.TagLogDao;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.TagLog;

import java.util.List;

/**
 * 作者：lish on 2018-07-31.
 * 描述：
 */

public class DaoUtils {
    private static TagLogDao tagLogDao = MyApplication.getInstances().getDaoSession().getTagLogDao();

    public static void delectTaglog(String mac) {
        List<TagLog> tagLogs = tagLogDao.queryBuilder().where(TagLogDao.Properties.Mac.eq(mac)).list();
        tagLogDao.deleteInTx(tagLogs);
    }
}
