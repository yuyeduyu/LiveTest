package com.ascend.wangfeng.locationbyhand.util;

import android.util.Log;

import com.anye.greendao.gen.LogDao;
import com.anye.greendao.gen.TagLogDao;
import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.NoteDoDeal;
import com.ascend.wangfeng.locationbyhand.bean.NoteVo;
import com.ascend.wangfeng.locationbyhand.bean.StaAssociatedDo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.TagLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fengye on 2017/2/8.
 * email 1040441325@qq.com
 */

public class DataFormat {
    private static String TAG = "DataFormat";

    /**
     * 信道转换 int-->String
     *
     * @param i
     * @return
     */
    public static String channel_intToString(int i) {
        String channel;
        if (i == 0) channel = "a";
        else if (i == 1) channel = "1";
        else if (i == 2) channel = "6";
        else channel = "11";
        Log.i(TAG, "channel_intToString: " + channel);
        return channel;
    }

    /**
     * 信道转换 String-->Int
     *
     * @param channel
     * @return
     */
    public static int channel_stringToInt(String channel) {
        int i;
        if (channel.equals("a")) i = 0;
        else if (channel.equals("1")) i = 1;
        else if (channel.equals("6")) i = 2;
        else i = 3;
        Log.i(TAG, "channel_stringToInt: " + i);
        return i;
    }
    /**
     * 获取 ap列表转化
     * @param message
     * @return
     */


    /**
     * 判断当前有无目标
     *
     * @param aps
     * @param
     * @return
     */
    public static boolean hasTagFromAp(List<ApVo> aps) {
        boolean is= false;
        List<NoteDo> noteVos = MyApplication.getmNoteDos();
        for (ApVo ap : aps) {
            for (NoteDo noteVo : noteVos) {
                if (noteVo.getRing()&&ap.getBssid().equals(noteVo.getMac())) {
                    is =true;
                    NoteDoDeal deal=new NoteDoDeal(noteVos);
                    noteVo.setRing(false);
                    deal.upDate(noteVo.getMac(),false);
                }
            }
        }
        return is;
    }

    public static boolean hasTagFromSta(List<StaVo> stas) {
        boolean is =false;
        List<NoteDo> noteVos = MyApplication.getmNoteDos();
        for (StaVo sta : stas) {
            for (NoteDo noteVo : noteVos) {
                if (noteVo.getRing()&&sta.getMac().equals(noteVo.getMac())) {
                    is = true;
                    NoteDoDeal deal=new NoteDoDeal(noteVos);
                    noteVo.setRing(false);
                    deal.upDate(noteVo.getMac(),false);
                }
            }
        }
        return is;
    }

    public static ArrayList<ApVo> searchAps(List<ApVo> aps, String regEx) {
        ArrayList<ApVo> selectedaps = new ArrayList<>();
        for (int i = 0; i < aps.size(); i++) {
            ApVo ap = aps.get(i);
            if (RegularExprssion.isChoose(ap.getBssid(), regEx) ||
                    RegularExprssion.isChoose(ap.getEssid(), regEx)) {
                Log.d(TAG, "selectedMac: ");
                selectedaps.add(ap);
            }

        }
        return selectedaps;
    }

    public static ArrayList<StaVo> searchStas(List<StaVo> aps, String regEx) {
        ArrayList<StaVo> selectedaps = new ArrayList<>();
        for (int i = 0; i < aps.size(); i++) {
            StaVo ap = aps.get(i);
            Log.i(TAG, "searchStas: " + ap.getMac());
            if (RegularExprssion.isChoose(ap.getMac(), regEx)) {
                Log.d(TAG, "selectedMac: ");
                selectedaps.add(ap);
            }
        }
        return selectedaps;
    }

    /**
     * @param vos ap列表
     * @return 布控目标 添加标注并置顶
     */
    public static List<ApVo> makeTagOfAp(List<ApVo> vos) {
        List<NoteDo> noteDos = MyApplication.getmNoteDos();
        List<ApVo> apVos = new ArrayList<>();
        List<ApVo> tags = new ArrayList<>();
        List<ApVo> normals = new ArrayList<>();
        for (ApVo vo :
                vos) {
            for (NoteDo noteDo :
                    noteDos) {
                if (vo.getBssid().equals(noteDo.getMac())) {
                    vo.setTag(true);
                    vo.setNote(noteDo.getNote());
                    tags.add( vo);
                    saveInfo(vo);
                    break;
                }
            }
            if (!vo.isTag()) {
                normals.add(vo);
            }
        }
        Collections.sort(tags);
        Collections.sort(normals);
        apVos.addAll(tags);
        apVos.addAll(normals);
        return apVos;
    }

    public static ApVo makeTagOfAp(ApVo apVo) {
        List<NoteVo> noteVos = Config.getAlarmMacListDo().getNoteVos();
        for (NoteVo noteVo :
                noteVos) {
            if (apVo.getBssid().equals(noteVo.getMac())) {
                apVo.setTag(true);
                apVo.setNote(noteVo.getNote());
                return apVo;
            }
        }
        return apVo;
    }

    /**
     * @param vos ap列表
     * @return 布控目标 添加标注并排序置顶
     */
    public static List<StaVo> makeTagOfSta(List<StaVo> vos) {
        List<NoteDo> noteVos = MyApplication.getmNoteDos();
        List<StaVo> staTagVos = new ArrayList<>();
        List<StaVo> staNormalVos = new ArrayList<>();
        for (StaVo vo : vos) {
            for (NoteDo noteVo : noteVos) {
                if (vo.getMac().equals(noteVo.getMac())) {
                    vo.setTag(true);
                    vo.setNote(noteVo.getNote());
                    staTagVos.add(vo);
                    saveInfo(vo);
                    break;
                }
            }
            if (!vo.isTag()) {
                staNormalVos.add(vo);
            }
        }
        Collections.sort(staTagVos);
        Collections.sort(staNormalVos);
        List<StaVo> staVos = new ArrayList<>();
        staVos.addAll(staTagVos);
        staVos.addAll(staNormalVos);
        return staVos;
    }

    public static StaVo makeTagOfSta(StaVo vo) {
        List<NoteVo> noteVos = Config.getAlarmMacListDo().getNoteVos();

        for (NoteVo noteVo :
                noteVos) {
            if (vo.getMac().equals(noteVo.getMac())) {
                vo.setTag(true);
                vo.setNote(noteVo.getNote());
                return vo;
            }
        }
        return vo;
    }
    private static TagLogDao dao = MyApplication.getInstances().getDaoSession().getTagLogDao();
    private static void saveInfo(StaVo vo) {

        TagLog log = new TagLog();
        log.setMac(vo.getMac());
        log.setDistance(vo.getSignal());
        log.setLtime(vo.getLtime());
        log.setType(1);
        log.setAppVersion(MyApplication.AppVersion);
        dao.insert(log);
    }

    private static void saveInfo(ApVo ap) {
        TagLog log = new TagLog();
        log.setMac(ap.getBssid());
        log.setDistance(ap.getSignal());
        log.setLtime(ap.getLtime());
        log.setType(0);
        log.setAppVersion(MyApplication.AppVersion);
        dao.insert(log);
    }
    public static ArrayList<StaVo> StaAssociatedFormat(String mac,List<StaVo> stas) {
        ArrayList<StaVo> staVos = new ArrayList<>();
        staVos = (ArrayList<StaVo>) makeTagOfSta(stas);
        ArrayList<StaVo> tags = new ArrayList<>();//被标记的sta
        ArrayList<StaVo> normals = new ArrayList<>();//未被标记的sta
        for (StaVo sta :
                staVos) {
            if (sta.isTag())tags.add(sta);
            else normals.add(sta);
        }
        Collections.sort(tags);
        Collections.sort(normals);
        staVos.clear();
        staVos.addAll(tags);
        staVos.addAll(normals);
        for (int i = 0; i < staVos.size(); i++) {
            if (mac.equals(staVos.get(i).getMac())){
                StaVo staVo = staVos.get(i);
                staVos.remove(i);
                staVos.add(0,staVo);
                break;
            }
        }
        return staVos;
    }
    public static ArrayList<StaVo> StaAssociatedFormat(StaAssociatedDo aDo) {
        ArrayList<StaVo> staVos = new ArrayList<>();

        staVos = (ArrayList<StaVo>) makeTagOfSta(aDo.getStaVos());

        ArrayList<StaVo> tags = new ArrayList<>();//被标记的sta
        ArrayList<StaVo> normals = new ArrayList<>();//未被标记的sta
        for (StaVo sta :
                staVos) {
            if (sta.isTag())tags.add(sta);
            else normals.add(sta);
        }
        Collections.sort(tags);
        Collections.sort(normals);
        staVos.clear();
        staVos.addAll(tags);
        staVos.addAll(normals);
        StaVo target = makeTagOfSta(aDo.getStaVo());
        staVos.add(0,target);
        return staVos;
    }
}

