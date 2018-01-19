package com.ascend.wangfeng.locationbyhand.bean;

import com.anye.greendao.gen.NoteDoDao;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;

import java.util.List;

/**
 * Created by fengye on 2017/8/12.
 * email 1040441325@qq.com
 */

public class NoteDoDeal {
    private NoteDoDao dao = MyApplication.getInstances().getDaoSession().getNoteDoDao();
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
    public void upDate(String mac,String note){
        List<NoteDo> noteDos1 = dao.queryBuilder().where(NoteDoDao.Properties.Mac.eq(mac)).build().list();
        if (noteDos1.size()<=0)return;
        NoteDo noteDo =noteDos1.get(0);
        noteDo.setNote(note);
        dao.update(noteDo);
        for (int i = 0; i < mDos.size(); i++) {
            if (noteDo.getId()==mDos.get(i).getId()){
                mDos.set(i,noteDo);
            }
        }
    }
    public void upDate(String mac,boolean ring){
        List<NoteDo> noteDos1 = dao.queryBuilder().where(NoteDoDao.Properties.Mac.eq(mac)).build().list();
        if (noteDos1.size()<=0)return;
        NoteDo noteDo =noteDos1.get(0);
        noteDo.setRing(ring);
        dao.update(noteDo);
        for (int i = 0; i < mDos.size(); i++) {
            if (noteDo.getId()==mDos.get(i).getId()){
                mDos.set(i,noteDo);
            }
        }

    }
    public void add(NoteDo noteDo){
        for (int i = 0; i < mDos.size(); i++) {//如果存在则更新
            if (noteDo.getMac().equals(mDos.get(i).getMac())){
                upDate(noteDo.getMac(),noteDo.getNote());
                return;
            }
        }
        dao.insert(noteDo);
        mDos.add(noteDo);
    }
    public void delete(int i){
        dao.delete(mDos.get(i));
        mDos.remove(i);
    }
    public void delete(String mac){
        for (int i = 0; i < mDos.size(); i++) {
            if (mac.equals(mDos.get(i).getMac())){
                delete(i);
            }
        }
    }
}
