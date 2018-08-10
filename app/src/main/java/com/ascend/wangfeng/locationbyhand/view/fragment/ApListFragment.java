package com.ascend.wangfeng.locationbyhand.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
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
import com.ascend.wangfeng.locationbyhand.adapter.ApListAdapter;
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.adapter.OnItemClickLisener;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.NoteVo;
import com.ascend.wangfeng.locationbyhand.contract.ApListContract;
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
import com.ascend.wangfeng.locationbyhand.event.ApListEvent;
import com.ascend.wangfeng.locationbyhand.event.FTPEvent;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.SearchEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.AppVersionEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.MessageEvent;
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
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by fengye on 2017/2/7.
 * email 1040441325@qq.com
 */
public class ApListFragment extends BaseFragment implements
        ApListContract.View, IShowView {

    public static final long VOL_RATE = 5 * 60 * 1000;
    @BindView(R.id.frame_aplist_recycler)
    RecyclerView mFrameAplistRecycler;
    @BindView(R.id.update_time)
    TextView mUpdateTime;
    @BindView(R.id.list_count)
    TextView mListCount;
    @BindView(R.id.electric)
    ImageView mElectric;
    @BindView(R.id.btn_upload)
    FloatingActionButton btnUpload;
    private ApListAdapter adapter;
    final String TAG = getClass().getCanonicalName();
    private ArrayList<ApVo> mData;
    private Subscription rxSub;
    private String searchCondition;
    private Subscription rxSubFromSearch;
    private Subscription rxElectric;
    private Subscription mVolRxBus;
    private long rate = 5000;
    final static Handler handler = new Handler();
    Runnable runnable = null;

    public LoadingDialog loadingDialog; //上传dialog
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aplist, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

/*    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(LocationData location){
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();
    }*/

    @Override
    public void onResume() {
        super.onResume();
        if (MyApplication.AppVersion==Config.C_MINI){
            btnUpload.setVisibility(View.VISIBLE);
        }else {
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
        loadingDialog = new LoadingDialog( getActivity() );
    }

    //mini 上传
    private void initUpload() {
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.e(TAG,"UP>>-->点击电量  aplist-->"+aplist.size()+"   stalist:"+stalist.size()+"  sclist:"+sclist.size()+"   gpslist:"+gpslist.size());
                if (MyApplication.mDevicdID != null) {
                    if (MyApplication.isConnected(getContext())){
                        loadingDialog.show();
                        UpLoad(aplist, stalist, sclist, gpslist);
                    }
                    else
                        show(null, "请先连接网络");
                } else {
                    show(null, "请先连接设备");
                }

            }
        });
    }

    //更新电量
    private void initVol() {
        mVolRxBus = RxBus.getDefault().toObservable(VolEvent.class)         //接受数据
                //在io线程进行订阅，可以执行一些耗时操作
                .subscribeOn(Schedulers.io())
                //在主线程中进行观察，可做UI更新操作
                .observeOn(AndroidSchedulers.mainThread())
                //观察的对象   BaseSubcribe<VolEvent>继承Subscriber
                .subscribe(new BaseSubcribe<VolEvent>() {
                    @Override
                    public void onNext(VolEvent event) {
                        PowerImageSet.setImage(mElectric,event.getVol());
                        rate=VOL_RATE;
                    }
                });
        //定时获取电量
        runnable = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "run: GETVOL");
                MessageEvent event = new MessageEvent(MessageEvent.SEND_DATA);
                event.setData("GETVOL");
                RxBus.getDefault().post(event);
                handler.postDelayed(this, rate);
            }
        };

        handler.post(runnable);
    }

    private void initView() {
        mFrameAplistRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mFrameAplistRecycler.setAdapter(adapter);
        mFrameAplistRecycler.addItemDecoration(new MyItemDecoration(getContext(), LinearLayoutManager.HORIZONTAL));
    }

    private void initData() {
        mData = new ArrayList<>();
        adapter = new ApListAdapter(mData, new OnItemClickLisener() {
            @Override
            public void onClick(View view, int position) {
                if (position == -1) return;
                //点击事件
                Intent intent = new Intent(getActivity(), ChartActivity.class);
                intent.putExtra("mac", mData.get(position).getBssid());              //MAC
                intent.putExtra("type", 0);                                   //类型
                intent.putExtra("channel", mData.get(position).getChannel());        //信道
                intent.putExtra("tag", mData.get(position).isTag());        //是否是布控目标
                startActivity(intent);
            }
        }, new OnItemClickLisener() {
            @Override
            public void onClick(View view, int position) {
                if (position == -1) return;
                //长按事件
                NoteVo noteVo = new NoteVo();
                noteVo.setMac(mData.get(position).getBssid());
                noteVo.setNote(mData.get(position).getNote());
                noteVo.setName(mData.get(position).getEssid());
                TargetSetDialog.showDialog((AppCompatActivity) getActivity(), noteVo,
                        ApListFragment.this,true);
            }
        });

        rxSub = RxBus.getDefault().toObservable(ApListEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<ApListEvent>() {
                    @Override
                    public void onNext(ApListEvent event) {
                        List<ApVo> list = event.getList();

                        updateData(list);                                       //更新list数据

                    }
                });

        rxSubFromSearch = RxBus.getDefault().toObservable(SearchEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<SearchEvent>() {
                    @Override
                    public void onNext(SearchEvent event) {
                        searchCondition = event.getContent();
                    }
                });
        RxBus.getDefault().toObservable(AppVersionEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<AppVersionEvent>() {
                    @Override
                    public void onNext(AppVersionEvent event) {
                        //更新界面 MIni显示上传按钮
                        if (event.getAppVersion() == Config.C_MINI) {
                            btnUpload.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }


    private void updateData(List<ApVo> list) {
        Calendar c = Calendar.getInstance();
        mUpdateTime.setText(new SimpleDateFormat("HH:mm:ss").format(c.getTime()));

        //筛选数据
        if (searchCondition != null) {
            list = DataFormat.searchAps(list, searchCondition);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (runnable != null)
            handler.removeCallbacks(runnable);
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
                    if (ftpClient ==null){
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
                    show(null, "上传成功");
                    ((Activity) getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 在这里执行你要想的操作 比如直接在这里更新ui或者调用回调在 在回调中更新ui
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


    @OnClick(R.id.btn_upload)
    public void onViewClicked() {
    }
}
