package com.ascend.wangfeng.locationbyhand.bean;

import com.anye.greendao.gen.NoteDoDao;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;
import com.ascend.wangfeng.locationbyhand.util.LogUtils;

import java.util.List;

/**
 * Created by fengye on 2017/8/12.
 * email 1040441325@qq.com
 */

public class NoteDoDeal {
    private static NoteDoDao dao = MyApplication.getInstances().getDaoSession().getNoteDoDao();
    private List<NoteDo> mDos;

    public NoteDoDeal(List<NoteDo> dos) {
        mDos = dos;
    }

    public List<NoteDo> getDos() {
        return mDos;
    }

    public void setDos(List<NoteDo> dos) {
        mDos = dos;
    }

    public void upDate(String mac, String note) {
        List<NoteDo> noteDos1 = dao.queryBuilder().where(NoteDoDao.Properties.Mac.eq(mac)).build().list();
        if (noteDos1.size() <= 0) return;
        NoteDo noteDo = noteDos1.get(0);
        noteDo.setNote(note);
        dao.update(noteDo);
        for (int i = 0; i < mDos.size(); i++) {
            if (noteDo.getId() == mDos.get(i).getId()) {
                mDos.set(i, noteDo);
            }
        }
    }

    public void upDate(String mac, boolean ring) {
        List<NoteDo> noteDos1 = dao.queryBuilder().where(NoteDoDao.Properties.Mac.eq(mac)).build().list();
        if (noteDos1.size() <= 0) return;
        NoteDo noteDo = noteDos1.get(0);
        noteDo.setRing(ring);
        dao.update(noteDo);
        for (int i = 0; i < mDos.size(); i++) {
            if (noteDo.getId() == mDos.get(i).getId()) {
                mDos.set(i, noteDo);
            }
        }

    }

    public void add(NoteDo noteDo) {
        for (int i = 0; i < mDos.size(); i++) {//如果存在则更新
            if (noteDo.getMac().equals(mDos.get(i).getMac())
                    & noteDo.getType() == mDos.get(i).getType()) {
                upDate(noteDo.getMac(), noteDo.getNote());
                return;
            }
        }
        dao.insert(noteDo);
        mDos.add(noteDo);
    }

    public void delete(int i) {
        dao.delete(mDos.get(i));
        mDos.remove(i);
    }

    public void delete(String mac) {
        for (int i = 0; i < mDos.size(); i++) {
            if (mac.equals(mDos.get(i).getMac())) {
                delete(i);
            }
        }
    }
    /**
     * 将服务器布控目标 存储本地
     *
     * @author lish
     * created at 2018-08-20 14:48
     */
    public static void saveToSqlite(List<NoteDo> data) {
        delectFromSqlite(1);
        NoteDoDeal deal = new NoteDoDeal(MyApplication.getmNoteDos());
        for (NoteDo noteDo : data) {
            deal.add(noteDo);
        }
    }

    /**
     * 删除本地缓存的服务器布控目标
     *
     * @param type 0:本地布控 1:网络布控
     *             created at 2018-08-20 14:48
     * @author lish
     */
    public static void delectFromSqlite(int type) {
        List<NoteDo> net = dao.queryBuilder().where(NoteDoDao.Properties.Type.eq(type)).list();
        for (NoteDo bean : net) {
            dao.delete(bean);
        }
        MyApplication.setmNoteDos(dao.loadAll());
    }
}
