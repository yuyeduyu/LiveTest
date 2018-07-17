package com.ascend.wangfeng.locationbyhand.view.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.anye.greendao.gen.ConnectRelationDao;
import com.anye.greendao.gen.LogDao;
import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.Ghz;
import com.ascend.wangfeng.locationbyhand.bean.NoteVo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.ConnectRelation;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;
import com.ascend.wangfeng.locationbyhand.contract.MainServiceContract;
import com.ascend.wangfeng.locationbyhand.event.ApListEvent;
import com.ascend.wangfeng.locationbyhand.event.ElectricEvent;
import com.ascend.wangfeng.locationbyhand.event.RingEvent;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.StaListEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.LineEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.MacData;
import com.ascend.wangfeng.locationbyhand.event.ble.MainServiceEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.MessageEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.RelationEvent;
import com.ascend.wangfeng.locationbyhand.presenter.MainServicePresenterImpl;
import com.ascend.wangfeng.locationbyhand.util.BellandShake;
import com.ascend.wangfeng.locationbyhand.util.DataFormat;
import com.ascend.wangfeng.locationbyhand.util.MatchMac;
import com.ascend.wangfeng.locationbyhand.util.SharedPreferencesUtils;
import com.ascend.wangfeng.locationbyhand.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by fengye on 2017/2/8.
 * email 1040441325@qq.com
 */

public class MainService extends Service implements MainServiceContract.View, SharedPreferences.OnSharedPreferenceChangeListener {
    public static final int SAVE_RATE = 30;    // 轮询30次,保存一次 即一分钟保存一次
    public static final int CACHE_TIME = 2 * 60 * 1000;
    private static final String NOMAC = "00:00:00:00:00:00";
    public static final int RING_TIME = 10 * 1000;
    public static final int CHANNEL_RATE = 5;
    public static final int CHANNEL_RATE_5G = 1;
    public static final int DATA_RATE = 2000;
    public int SINGAL = 20;//信号强度差值阈值
    public static final int DEFAULT_SINGAL = -111;// 默认信号强度
    private final String TAG = getClass().getCanonicalName();
    private MainServicePresenterImpl mPresenter;
    private int saveCount;//计数器
    private List<ApVo> mApVos;
    private List<StaVo> mStaVos;
    private int mType;
    private String mMac;
    private int mChannel = 1;//信道
    private int mChannel5 = 1;//5G信道
    private boolean mLock;//是否锁定信道
    private Subscription mDataFromBle;
    private Subscription mCommand;
    private Handler mHandler;
    private Runnable mRunable;
    private int mSingal;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // mPresenter = new MainServicePresenterImpl(this);
        // mPresenter.update();
        initData();
        init();//蓝牙
        Notification notification = new Notification.Builder(this.getApplicationContext())
                .setContentTitle("无线雷达mini")
                .setContentText("数据传输服务")
                .setSmallIcon(R.mipmap.upload)
                .setWhen(System.currentTimeMillis())
                .build();
        startForeground(2, notification);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        SharedPreferencesUtils.mSp.registerOnSharedPreferenceChangeListener(this);
    }

    public void lockChannel(int channel, int type, String mac) {
        mType = type;
        mMac = mac;
        MessageEvent event = new MessageEvent(MessageEvent.SEND_DATA);
        if (channel != 0) {
            event.setData("CHANNEL:" + channel);
            mChannel = channel;
            mChannel5 = channel;
            mLock = true;
        } else {
            event.setData("END");
        }
        RxBus.getDefault().post(event);
    }

    public void unLockChannel() {
        mLock = false;
    }


    private void initData() {
        mApVos = new ArrayList<>();
        mStaVos = new ArrayList<>();
        mSingal = getSingalFromSp();
    }

    private void init() {
        //定时向BLE获取数据
        mHandler = new Handler();
        mRunable = new Runnable() {
            @Override
            public void run() {
                if (MyApplication.getIsDataRun()) {
                    MessageEvent event = new MessageEvent(MessageEvent.SEND_DATA);
                    if (mLock) {
                        // 发送 信道,防止锁定失败
                        if (MyApplication.mGhz == Ghz.G24) {
                            event.setData("CHANNEL:" + mChannel);
                        } else {
                            event.setData("CHANNEL:" + mChannel5);
                        }
                    } else {
                        if (MyApplication.mGhz == Ghz.G24) {
                            event.setData("CHANNEL:" + mChannel);
                            mChannel += CHANNEL_RATE;
                            if (mChannel > 11) mChannel = 1;
                        } else {
                            event.setData("CHANNEL:" + mChannel5);
                            mChannel5 += CHANNEL_RATE_5G;
                            if (mChannel5 > 13) mChannel5 = 1;
                        }
                    }
                    RxBus.getDefault().post(event);
                }
                mHandler.postDelayed(this, DATA_RATE);
            }
        };
        mHandler.post(mRunable);
        mDataFromBle = RxBus.getDefault().toObservable(MacData.class)
                .observeOn(Schedulers.computation())
                .subscribe(new BaseSubcribe<MacData>() {
                    @Override
                    public void onNext(MacData data) {
                        Log.i(TAG, "onNext: macdata");
                        // mini
                        if (MyApplication.AppVersion==Config.C_MINI){
                            data = filterData(data);
                        }
                        maintainData(data);
                        if (saveCount >= SAVE_RATE) {
                            saveToSqlite(mApVos,mStaVos);           //保存数据
                            saveCount = 0;
                        } else {
                            saveCount++;
                        }
                        try {
//                            maintainData(data);
//                            saveToSqlite(mApVos, mStaVos);
                            checkRing();
                            //发送数据
                            updateData(mApVos, mStaVos);
                            toLineData();
                            toListData();
                        } catch (Exception e) {
                            Log.e(TAG, "onNext: " + e.getMessage());
                        }


/*=======
                        }
                        checkRing();
                        //发送数据
                        updateData(mApVos, mStaVos);
                        toLineData();
                        toListData();
>>>>>>> wxldMini*/
                    }
                });
        mCommand = RxBus.getDefault().toObservable(MainServiceEvent.class)
                .subscribe(new BaseSubcribe<MainServiceEvent>() {
                    @Override
                    public void onNext(MainServiceEvent event) {
                        switch (event.getCommand()) {
                            case MainServiceEvent.LOCK:
                                lockChannel(event.getChannel(), event.getType(), event.getMac());
                                break;
                            case MainServiceEvent.UNLOCK:
                                unLockChannel();
                                break;
                            case MainServiceEvent.GETNUMBER:
                                break;
                            case MainServiceEvent.CLEAE_DATA:
                                mApVos.clear();
                                mStaVos.clear();
                                updateData(mApVos, mStaVos);
                                break;
                        }
                    }
                });
    }

    // 过滤数据
    private MacData filterData(MacData data) {
        List<ApVo> apVos = data.getApVos();
        List<StaVo> staVos = data.getStaVos();
        for (int i = apVos.size() - 1; i >= 0; i--) {
            ApVo ap = apVos.get(i);
            if (ap.getSignal() < mSingal) {
                apVos.remove(i);
            }
        }
        for (int i = staVos.size() - 1; i >= 0; i--) {
            StaVo sta = staVos.get(i);
            if (sta.getSignal() < mSingal) {
                staVos.remove(i);
            }
        }
        MacData result = new MacData(apVos, staVos);
        return result;
    }

    /**
     * 保存数据,存储所有 info,布控的 连接关系
     */
    private void saveToSqlite(List<ApVo> apVos, List<StaVo> staVos) {
        for (ApVo ap : apVos) {
            Log.i(TAG, "saveToSqlite: " + ap.getBssid());
            saveInfo(ap);
            for (NoteDo tag : MyApplication.getmNoteDos()) {
                if (MatchMac.isSame(ap.getBssid(), tag.getMac())) {
                    Log.i(TAG, "saveToSqlite: " + ap.getBssid());
                    saveRelation(ap, staVos);
                    break;
                }
            }
        }
        for (StaVo sta : staVos) {
            saveInfo(sta);
        }

    }

    /**
     * 铃声提示
     */
    private void checkRing() {
        if ((Boolean) SharedPreferencesUtils.getParam(getBaseContext(), "ring", false)) {
            if (DataFormat.hasTagFromAp(mApVos) || DataFormat.hasTagFromSta(mStaVos)) {
                BellandShake.open(RING_TIME, 0, getBaseContext());
                Log.i(TAG, "checkRing: ");
            }

        }
    }

    private void toListData() {
        Log.i(TAG, "toListData: start");
        ApVo apVo = new ApVo();
        StaVo target = new StaVo();
        ArrayList<StaVo> staVos = new ArrayList<>();
        if (mMac == null) return;
        if (mType == 0) {
            for (int i = 0; i < mApVos.size(); i++) {
                if (mMac.equals(mApVos.get(i).getBssid())) {
                    apVo = mApVos.get(i);
                    break;
                }
            }
            for (int i = 0; i < mStaVos.size(); i++) {
                if (mMac.equals(mStaVos.get(i).getApmac())) {
                    staVos.add(mStaVos.get(i));
                }
            }
        } else {
            for (int i = 0; i < mStaVos.size(); i++) {
                if (mMac.equals(mStaVos.get(i).getMac())) {
                    target = mStaVos.get(i);
                    if (!target.getApmac().equals(NOMAC)) {
                        for (int j = 0; j < mApVos.size(); j++) {
                            if (target.getApmac().equals(mApVos.get(j).getBssid())) {
                                apVo = mApVos.get(j);
                                for (int k = 0; k < mStaVos.size(); k++) {
                                    if (apVo.getBssid().equals(mStaVos.get(k).getApmac())) {
                                        staVos.add(mStaVos.get(k));
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        if (mType == 1 && staVos.size() == 0) staVos.add(target);
        RxBus.getDefault().post(new RelationEvent(apVo, staVos));
        Log.i(TAG, "toListData: finish" + apVo.toString());
    }

    private void toLineData() {
        if (mMac == null) return;
        if (mType == 0) {
            for (int i = 0; i < mApVos.size(); i++) {
                if (mMac.equals(mApVos.get(i).getBssid())) {
                    RxBus.getDefault().post(new LineEvent(mApVos.get(i)));
                    Log.i(TAG, "toLineData: " + mApVos.get(i).toString());
                }
            }
        } else {
            for (int i = 0; i < mStaVos.size(); i++) {
                if (mMac.equals(mStaVos.get(i).getMac())) {
                    RxBus.getDefault().post(new LineEvent(mStaVos.get(i)));
                    Log.i(TAG, "toLineData: success" + mStaVos.get(i));
                }
            }
        }
        Log.i(TAG, "toLineData: finish");
    }

    /**
     * @param data 从ble接受的采集数据
     *             进行处理;维护一个最新的数据列表
     */
    private void maintainData(MacData data) {
        Log.i(TAG, "maintainData: " + mApVos.size());
        long time = System.currentTimeMillis();
        List<ApVo> apVos = data.getApVos();
        //新的数据替换旧数据
        for (int i = apVos.size() - 1; i >= 0; i--) {
            for (int j = 0; j < mApVos.size(); j++) {
                if (apVos.get(i).getBssid().equals(mApVos.get(j).getBssid())) {
                    Log.i(TAG, "maintainData: ap" + apVos.get(i).getBssid());
                    // 信道处理
                    if (MyApplication.AppVersion==Config.C_MINI){
                        SINGAL = -60;
                        if (apVos.get(i).getSignal() < SINGAL) {//信号强度小于60,信道不改变
                            // 改变 新数据的channel,然后用新数据覆盖旧数据
                            apVos.get(i).setChannel(mApVos.get(j).getChannel());
                        }
                    }else {
                        if (!NumberUtil.muchLarger(apVos.get(i).getSignal(), mApVos.get(j).getSignal(), SINGAL)) {//信号强度差值不大于20
                            // 使用旧数据的信道
                            apVos.get(i).setChannel(mApVos.get(j).getChannel());
                        }
                    }

                    mApVos.set(j, apVos.get(i));
                    apVos.remove(i);
                    break;
                }
            }
        }
        mApVos.addAll(apVos);//添加新的数据
        Log.i(TAG, "maintainData: " + mApVos.size());
        //过期数据删除
        for (int i = mApVos.size() - 1; i >= 0; i--) {
            if (time - mApVos.get(i).getLtime() > CACHE_TIME) {
                mApVos.remove(i);
            }
        }
        //数据处理：排序，添加标记
        mApVos = (ArrayList<ApVo>) DataFormat.makeTagOfAp(mApVos);
/*--------------------------*/
        List<StaVo> staVos = data.getStaVos();
        //新的数据替换旧数据
        for (int i = staVos.size() - 1; i >= 0; i--) {
            for (int j = 0; j < mStaVos.size(); j++) {
                if (staVos.get(i).getMac().equals(mStaVos.get(j).getMac())) {
                    // 信号强度处理
                    if (staVos.get(i).getSignal() == DEFAULT_SINGAL) {
                        // 若为默认值,则使用上次采集的信号强度
                        staVos.get(i).setSignal(mStaVos.get(j).getSignal());
                    }
                    // 连接关系处理
                    if (staVos.get(i).getApmac().equals(NOMAC)) {
                        staVos.get(i).setApmac(mStaVos.get(j).getApmac());
                    }
                    // 虚拟身份保存
                    if (mStaVos.get(j).getIdentities() != null && mStaVos.get(j).getIdentities().size() > 0) {
                        Log.e("虚拟身份保存", "------->");
                        staVos.get(i).addIdentities(mStaVos.get(j).getIdentities());
                    }

                    mStaVos.set(j, staVos.get(i));
                    staVos.remove(i);
                    break;
                }
            }
        }

        mStaVos.addAll(staVos);
        //过期数据删除
        for (int i = mStaVos.size() - 1; i >= 0; i--) {
            if (time - mStaVos.get(i).getLtime() > CACHE_TIME) {
                mStaVos.remove(i);
            }
        }

        // 添加sta连接的Ap名
        for (int i = 0; i < mStaVos.size(); i++) {
            for (int j = 0; j < mApVos.size(); j++) {
                if (mStaVos.get(i).getApmac().equals(mApVos.get(j).getBssid())) {
                    mStaVos.get(i).setEssid(mApVos.get(j).getEssid());
                    mStaVos.get(i).setChannel(mApVos.get(j).getChannel());
                    break;
                }
            }
        }
        mStaVos = (ArrayList<StaVo>) DataFormat.makeTagOfSta(mStaVos);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // mPresenter.stop();
        if (mDataFromBle != null) mDataFromBle.unsubscribe();
        if (mCommand != null) mCommand.unsubscribe();
        if (mHandler != null) mHandler.removeCallbacks(mRunable);
        SharedPreferencesUtils.mSp.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();

    }

    @Override
    public void updateAp(List<ApVo> data) {
        //发送数据
        RxBus.getDefault().post(new ApListEvent(data));
        Log.d(TAG, "updateAp: " + data.get(0).getBssid());
        Log.d(TAG, "updateAp: " + data.get(0).toString());

    }

    @Override
    public void updateSta(List<StaVo> data) {
        //发送数据
        RxBus.getDefault().post(new StaListEvent(data));
        Log.d(TAG, "updateSta: " + data.get(0).toString());
    }

    @Override
    public void updateElectric(String electric) {
        RxBus.getDefault().post(new ElectricEvent(electric));
    }

    @Override
    public void updateData(List<ApVo> aps, List<StaVo> stas) {
        //发送数据
        RxBus.getDefault().post(new ApListEvent(aps));
        RxBus.getDefault().post(new StaListEvent(stas));

        boolean ring = false;
        if (Config.getAlarmMacListDo() == null || Config.getAlarmMacListDo().getNoteVos() == null)
            return;
        List<NoteVo> noteVos = Config.getAlarmMacListDo().getNoteVos();
        for (ApVo ap : aps) {
            for (NoteVo noteVo : noteVos) {
                if (MatchMac.isSame(ap.getBssid(), noteVo.getMac())) {
                    ring = true;
                }
            }
        }
        for (StaVo staVo : stas) {
            for (NoteVo noteVo : noteVos) {
                if (MatchMac.isSame(staVo.getMac(), noteVo.getMac())) {
                    ring = true;
                }
            }
        }
        if (ring && (Boolean) SharedPreferencesUtils.getParam(getBaseContext(), "ring", false)) {
            BellandShake.open(10 * 1000, 0, getBaseContext());
            SharedPreferencesUtils.setParam(getBaseContext(), "ring", false);
            RxBus.getDefault().post(new RingEvent(false));
        }

    }

    private void saveInfo(StaVo vo) {
        LogDao dao = MyApplication.getInstances().getDaoSession().getLogDao();
        com.ascend.wangfeng.locationbyhand.bean.dbBean.Log log = new com.ascend.wangfeng.locationbyhand.bean.dbBean.Log();
        log.setMac(vo.getMac());
        log.setDistance(vo.getSignal());
        log.setLtime(vo.getLtime());
        log.setType(1);
        log.setAppVersion(MyApplication.AppVersion);
        dao.insert(log);
    }

    private void saveInfo(ApVo ap) {
        Log.i(TAG, "saveInfo: " + ap.getBssid());
        LogDao dao = MyApplication.getInstances().getDaoSession().getLogDao();
        com.ascend.wangfeng.locationbyhand.bean.dbBean.Log log = new com.ascend.wangfeng.locationbyhand.bean.dbBean.Log();
        log.setMac(ap.getBssid());
        log.setDistance(ap.getSignal());
        log.setLtime(ap.getLtime());
        log.setType(0);
        log.setAppVersion(MyApplication.AppVersion);
        dao.insert(log);
    }

    private void saveRelation(ApVo ap, List<StaVo> stas) {
        ConnectRelationDao dao = MyApplication.getInstances().getDaoSession().getConnectRelationDao();
        for (StaVo sta : stas) {
            if (ap.getBssid().equals(sta.getApmac())) {
                ConnectRelation relationFromSql = dao.queryBuilder()
                        .where(ConnectRelationDao.Properties.Ap.eq(sta.getApmac()))
                        .where(ConnectRelationDao.Properties.Mac.eq(sta.getMac())).unique();
                if (relationFromSql == null) {
                    ConnectRelation relation = new ConnectRelation();
                    relation.setAp(sta.getApmac());
                    relation.setMac(sta.getMac());
                    relation.setTimeStart(sta.getLtime());
                    relation.setCount(1);
                    dao.insert(relation);
                } else {
                    if (relationFromSql.getTimeStart() == sta.getLtime())
                        continue;
                    ConnectRelation relation = relationFromSql;
                    Integer count = relation.getCount();
                    relation.setCount(count + 1);
                    relation.setTimeEnd(sta.getLtime());
                    dao.update(relation);
                }
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String s) {
        if ("collectRadius".equals(s)) {
            mSingal = getSingalFromSp();
        }
    }

    private int getSingalFromSp() {
        // 清空数据,防止切换监测范围后,仍显示超出距离的数据,清空前存储数据
        saveToSqlite(mApVos, mStaVos);
        mApVos.clear();
        mStaVos.clear();
        updateData(mApVos, mStaVos);
        int i = (int) SharedPreferencesUtils.getParam(getApplicationContext(), "collectRadius", 2);
        switch (i) {
            case 0:
                return -50;
            case 1:
                return -60;
            default:
                return -200;
        }

    }
}
