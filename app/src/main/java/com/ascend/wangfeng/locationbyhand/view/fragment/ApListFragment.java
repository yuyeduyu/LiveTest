package com.ascend.wangfeng.locationbyhand.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.ApListAdapter;
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.adapter.OnItemClickLisener;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.NoteVo;
import com.ascend.wangfeng.locationbyhand.contract.ApListContract;
import com.ascend.wangfeng.locationbyhand.dialog.IShowView;
import com.ascend.wangfeng.locationbyhand.dialog.TargetSetDialog;
import com.ascend.wangfeng.locationbyhand.event.ApListEvent;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.SearchEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.MessageEvent;
import com.ascend.wangfeng.locationbyhand.event.ble.VolEvent;
import com.ascend.wangfeng.locationbyhand.util.DataFormat;
import com.ascend.wangfeng.locationbyhand.util.PowerImageSet;
import com.ascend.wangfeng.locationbyhand.view.activity.ChartActivity;

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
public class ApListFragment extends BaseFragment implements
        ApListContract.View, IShowView {

    public static final long VOL_RATE=5*60*1000;
    @BindView(R.id.frame_aplist_recycler)
    RecyclerView mFrameAplistRecycler;
    @BindView(R.id.update_time)
    TextView mUpdateTime;
    @BindView(R.id.list_count)
    TextView mListCount;
    @BindView(R.id.electric)
    ImageView mElectric;
    private ApListAdapter adapter;
    final String TAG = getClass().getCanonicalName();
    private ArrayList<ApVo> mData;
    private Subscription rxSub;
    private String searchCondition;
    private Subscription rxSubFromSearch;
    private Subscription rxElectric;
    private Subscription mVolRxBus;
    private long rate =5000;
    final static Handler handler =new Handler();
    Runnable runnable=null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aplist, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
        initVol();
    }
//更新电量
    private void initVol() {
        mVolRxBus = RxBus.getDefault().toObservable(VolEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
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
                intent.putExtra("mac", mData.get(position).getBssid());
                intent.putExtra("type", 0);
                intent.putExtra("channel",mData.get(position).getChannel());
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
                TargetSetDialog.showDialog((AppCompatActivity) getActivity(), noteVo,
                        ApListFragment.this);
            }
        });

        rxSub = RxBus.getDefault().toObservable(ApListEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<ApListEvent>() {
                    @Override
                    public void onNext(ApListEvent event) {
                        List<ApVo> list = event.getList();
                        updateData(list);
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
        Log.i(TAG, "onNext: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(rxSub!=null)
        rxSub.unsubscribe();
        if (rxSubFromSearch!=null)
        rxSubFromSearch.unsubscribe();
        if (mVolRxBus!=null)mVolRxBus.unsubscribe();

    }

    @Override
    public void show(Integer type, String e) {
        Snackbar.make(mFrameAplistRecycler, e, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (runnable!=null)
        handler.removeCallbacks(runnable);
    }
}
