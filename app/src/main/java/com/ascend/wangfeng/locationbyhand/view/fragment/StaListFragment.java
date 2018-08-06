package com.ascend.wangfeng.locationbyhand.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.adapter.OnItemClickLisener;
import com.ascend.wangfeng.locationbyhand.adapter.StaListAdapter;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.NoteVo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.data.FTPClientData;
import com.ascend.wangfeng.locationbyhand.data.FileData;
import com.ascend.wangfeng.locationbyhand.data.saveData.ApData;
import com.ascend.wangfeng.locationbyhand.data.saveData.LocationData;
import com.ascend.wangfeng.locationbyhand.data.saveData.StaConInfo;
import com.ascend.wangfeng.locationbyhand.data.saveData.StaData;
import com.ascend.wangfeng.locationbyhand.data.saveData.UpLoadData;
import com.ascend.wangfeng.locationbyhand.dialog.IShowView;
import com.ascend.wangfeng.locationbyhand.dialog.LoadingDialog;
import com.ascend.wangfeng.locationbyhand.dialog.TargetSetDialog;
import com.ascend.wangfeng.locationbyhand.event.FTPEvent;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.SearchEvent;
import com.ascend.wangfeng.locationbyhand.event.StaListEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.AppVersionEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.VolEvent;
import com.ascend.wangfeng.locationbyhand.util.DataFormat;
import com.ascend.wangfeng.locationbyhand.util.PowerImageSet;
import com.ascend.wangfeng.locationbyhand.view.activity.ChartActivity;

import org.apache.commons.net.ftp.FTPClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by fengye on 2017/2/7.
 * email 1040441325@qq.com
 */
public class StaListFragment extends BaseFragment
        implements IShowView {
    @BindView(R.id.frame_aplist_recycler)
    RecyclerView mFrameAplistRecycler;
    @BindView(R.id.update_time)
    TextView mUpdateTime;
    @BindView(R.id.list_count)
    TextView mListCount;
    @BindView(R.id.electric)
    ImageView mElectric;
    @BindView(R.id.count_title)
    TextView mCountTitle;
    @BindView(R.id.btn_upload)
    FloatingActionButton btnUpload;
    private ArrayList<StaVo> mData;
    private StaListAdapter adapter;
    private String TAG = getClass().getCanonicalName();
    private Subscription rxSub;
    private Subscription rxSubFromSearch;
    private String searchCondition;
    private Subscription rxElectric;
    private Subscription mVolRxBus;

    public LoadingDialog loadingDialog; //上传dialog

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aplist, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MyApplication.AppVersion == Config.C_MINI) {
            btnUpload.setVisibility(View.VISIBLE);
        } else {
            btnUpload.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
        initVol();
        initUpload();
        loadingDialog = new LoadingDialog(getActivity());
    }

    //mini 上传
    private void initUpload() {
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.mDevicdID != null) {
                    if (MyApplication.isConnected(getContext())) {
                        loadingDialog.show();
                        UpLoad(aplist, stalist, sclist, gpslist);
                    } else
                        show(null, "请先连接网络");
                } else {
                    show(null, "请先连接设备");
                }

            }
        });
    }

    private void initVol() {
        mVolRxBus = RxBus.getDefault().toObservable(VolEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<VolEvent>() {
                    @Override
                    public void onNext(VolEvent event) {
                        PowerImageSet.setImage(mElectric, event.getVol());
                    }
                });
    }

    private void initView() {
        mCountTitle.setText("终端数：");
        mFrameAplistRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mFrameAplistRecycler.setAdapter(adapter);
        mFrameAplistRecycler.addItemDecoration(new MyItemDecoration(getContext(), LinearLayoutManager.HORIZONTAL));
    }

    private void initData() {
        mData = new ArrayList<>();
        adapter = new StaListAdapter(mData, new OnItemClickLisener() {
            @Override
            public void onClick(View view, int position) {
                //点击事件
                if (position == -1) return;
                Intent intent = new Intent(getActivity(), ChartActivity.class);
                intent.putExtra("mac", mData.get(position).getMac());
                intent.putExtra("type", 1);
                intent.putExtra("channel", mData.get(position).getChannel());
                intent.putExtra("tag", mData.get(position).isTag());        //是否是布控目标
                startActivity(intent);

            }
        }, new OnItemClickLisener() {
            @Override
            public void onClick(View view, int position) {
                if (position == -1) return;
                //长按事件
                NoteVo noteVo = new NoteVo();
                noteVo.setMac(mData.get(position).getMac());
                noteVo.setNote(mData.get(position).getNote());
                TargetSetDialog.showDialog((AppCompatActivity) getActivity(), noteVo,
                        StaListFragment.this,false);
            }
        });

        rxSub = RxBus.getDefault().toObservable(StaListEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<StaListEvent>() {
                    @Override
                    public void onNext(StaListEvent event) {
                        List<StaVo> mList = event.getList();

                        updateData(mList);

                    }
                });
        rxSubFromSearch = RxBus.getDefault().toObservable(SearchEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<SearchEvent>() {
                    @Override
                    public void onNext(SearchEvent event) {
                        searchCondition = event.getContent();
                        Log.d(TAG, "onNext: " + searchCondition);
                    }
                });
        RxBus.getDefault().toObservable(AppVersionEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<AppVersionEvent>() {
                    @Override
                    public void onNext(AppVersionEvent event) {
                        //更新界面
                        //mini显示上传按钮
                        if (event.getAppVersion() == Config.C_MINI) {
                            btnUpload.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }


    private void updateData(List<StaVo> list) {
        Calendar c = Calendar.getInstance();
        mUpdateTime.setText(new SimpleDateFormat("HH:mm:ss").format(c.getTime()));
        //筛选数据
        Log.d(TAG, "updateData: " + list.size());
        if (searchCondition != null) {
            list = DataFormat.searchStas(list, searchCondition);
        }
        mListCount.setText(list.size() + "");
        mData.clear();
        mData.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rxSub != null)
            rxSub.unsubscribe();
        if (rxSubFromSearch != null)
            rxSubFromSearch.unsubscribe();
        if (mVolRxBus != null) mVolRxBus.unsubscribe();
    }

    @Override
    public void show(Integer type, String e) {
        Snackbar.make(mFrameAplistRecycler, e, Snackbar.LENGTH_SHORT).show();
    }


    private List<ApData> aplist = new ArrayList<>();
    private List<StaData> stalist = new ArrayList<>();
    private List<StaConInfo> sclist = new ArrayList<>();
    private List<LocationData> gpslist = new ArrayList<>();

    //获取全部数据，在点击上传时  上传全部数据
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(UpLoadData upLoadData) {
        this.aplist = upLoadData.getAplist();
        this.stalist = upLoadData.getStalist();
        this.sclist = upLoadData.getsClist();
        this.gpslist = upLoadData.getGpslist();
//        Log.e(TAG,"UP>>-->点击Sta  aplist-->"+aplist.size()+"   stalist:"+stalist.size()+"  sclist:"+sclist.size()+"   gpslist:"+gpslist.size());
    }

    //点击上传文件
    private void UpLoad(final List<ApData> aplist, final List<StaData> stalist, final List<StaConInfo> sclist, final List<LocationData> gpslist) {
        if (MyApplication.mDevicdID != null) {                               //连接成功
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long time = (System.currentTimeMillis() / 1000);
//                Log.e(TAG,"启动服务时间:"+time);
                    String filePath = "/mnt/sdcard/";
                    String fileName = MyApplication.mDevicdID + "[211.211.211.211]_" + time;

                    FTPClientData ftpClientData = new FTPClientData();

                    FTPClient ftpClient = ftpClientData.ftpConnect();
                    if (ftpClient == null) {
                        EventBus.getDefault().post(new FTPEvent(false));
                        ((Activity) getActivity()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
                                if (loadingDialog != null)
                                    loadingDialog.dismiss();
                                Toast.makeText(getActivity(), "连接FTP服务器失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                        return;

                    }else  EventBus.getDefault().post(new FTPEvent(true));
                    try {
                        ftpClient.makeDirectory(MyApplication.UpLoadFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //上传AP数据
                    FileData fileData = new FileData(getContext(), filePath, fileName, aplist, getVersion().toString());
                    ftpClientData.ftpUpload(ftpClient, filePath, fileName, "apl");

                    //上传终端数据
                    FileData staFile = new FileData(getContext(), filePath, fileName, stalist, getVersion().toString(), 1);
                    ftpClientData.ftpUpload(ftpClient, filePath, fileName, "log");

                    //上传连接数据
                    FileData ScFile = new FileData(getContext(), filePath, fileName, sclist, getVersion().toString(), "1");
                    ftpClientData.ftpUpload(ftpClient, filePath, fileName, "net");

                    //上传GPS轨迹的坐标
                    FileData GpsFile = new FileData(getContext(), filePath, fileName, gpslist, getVersion().toString(), "", 1);
                    ftpClientData.ftpUpload(ftpClient, filePath, fileName, "gps");
//                    Log.e(TAG,"UP>>-->点击数据Sta  aplist-->"+aplist.size()+"   stalist:"+stalist.size()+"  sclist:"+sclist.size()+"   gpslist:"+gpslist.size());
                    show(null, "上传成功");
                    ((Activity) getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
                            if (loadingDialog != null)
                                loadingDialog.dismiss();
                        }
                    });
                }

            }).start();
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "未知";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //处理需要保存 上传到服务器的终端数据
//    private void saveStaData(List<StaVo> mList) {
//        initStaConInfo(mList);
//        boolean addData;
//        for (int i = 0;i<mList.size();i++){
//            addData = true;
//            StaData staData = new StaData();
//            for (int j = 0;j<slist.size();j++){
//                if (slist.get(j).getStaMACStr().equals(mList.get(i).getMac())){                     //如果slist中已经有了该数据
//                    slist.get(j).setlTime(mList.get(i).getLtime());                                 //刷新数据最后采集时间
//                    slist.get(j).setSignal(mList.get(i).getSignal());                               //刷新信号轻度
//                    slist.get(j).setScanNum(mList.get(i).getNum());
//                    slist.get(j).setLatitude(myLatitude);            //纬度                             //刷新纬度
//                    slist.get(j).setLongitude(myLongitude);           //经度                             //刷新经度
//                    addData = false;
//                    break;
//                }
//            }
//            if (addData){
//                staData.setStaMACStr(mList.get(i).getMac());    //终端MAC
//                staData.setSignal(mList.get(i).getSignal());    //信号强度
//                staData.setScanNum(mList.get(i).getNum());      //扫描次数
//                staData.setfTime(mList.get(i).getLtime());      //第一次扫描时间
//                staData.setlTime(mList.get(i).getLtime());      //最后一次扫描时间
//                staData.setLatitude(myLatitude);                //纬度
//                staData.setLongitude(myLongitude);              //经度
//                slist.add(staData);
//            }
//        }
////        Log.e(TAG,"slist.size():"+slist.size());
//        for (int i = 0;i<slist.size();i++){
//            EventBus.getDefault().post(slist.get(i));
//        }
//    }
//
//    //连接信息
//    private void initStaConInfo(List<StaVo> mList) {
//        boolean addData;
//        for (int i = 0;i<mList.size();i++){
//            addData = true;
//            StaConInfo staConInfo = new StaConInfo();
//            for (int j = 0;j<sclist.size();j++){
//                if (sclist.get(j).getStaMACStr().equals(mList.get(i).getMac())){                     //如果slist中已经有了该数据
//                    sclist.get(j).setlTime(mList.get(i).getLtime());
//                    sclist.get(j).setLatitude(myLatitude);            //纬度                             //刷新纬度
//                    sclist.get(j).setLongitude(myLongitude);           //经度                             //刷新经度
//                    addData = false;
//                    break;
//                }
//            }
//            if (addData){
//                staConInfo.setStaMACStr(mList.get(i).getMac());    //终端MAC
//                staConInfo.setApName(mList.get(i).getEssid());
//                staConInfo.setfTime(mList.get(i).getLtime());      //第一次扫描时间
//                staConInfo.setlTime(mList.get(i).getLtime());      //最后一次扫描时间
//                staConInfo.setLatitude(myLatitude);                //纬度
//                staConInfo.setLongitude(myLongitude);              //经度
//                sclist.add(staConInfo);
//            }
//        }
////        Log.e(TAG,"sclist.size():"+sclist.size());
//        for (int i = 0;i<sclist.size();i++){
//            EventBus.getDefault().post(sclist.get(i));
//        }
//    }

}
