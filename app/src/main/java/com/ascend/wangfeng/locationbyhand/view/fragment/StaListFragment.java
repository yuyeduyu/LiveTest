package com.ascend.wangfeng.locationbyhand.view.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.adapter.OnItemClickLisener;
import com.ascend.wangfeng.locationbyhand.adapter.StaListAdapter;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.NoteVo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.dialog.IShowView;
import com.ascend.wangfeng.locationbyhand.dialog.TargetSetDialog;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.SearchEvent;
import com.ascend.wangfeng.locationbyhand.event.StaListEvent;
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
public class StaListFragment extends BaseFragment implements IShowView {
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
    private ArrayList<StaVo> mData;
    private StaListAdapter adapter;
    private String TAG = getClass().getCanonicalName();
    private Subscription rxSub;
    private Subscription rxSubFromSearch;
    private String searchCondition;
    private Subscription rxElectric;
    private Subscription mVolRxBus;

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

    private void initVol() {
        mVolRxBus = RxBus.getDefault().toObservable(VolEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<VolEvent>() {
                    @Override
                    public void onNext(VolEvent event) {
                        PowerImageSet.setImage(mElectric,event.getVol());
                    }
                });
    }

    private void initView() {
        mCountTitle.setText("终端数：");
        mFrameAplistRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mFrameAplistRecycler.setAdapter(adapter);
        mFrameAplistRecycler.addItemDecoration(new MyItemDecoration(getContext(),LinearLayoutManager.HORIZONTAL));
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
                intent.putExtra("channel",mData.get(position).getChannel());
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
                        StaListFragment.this);
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
        if (rxSub!=null)
        rxSub.unsubscribe();
        if (rxSubFromSearch!=null)
        rxSubFromSearch.unsubscribe();
        if (mVolRxBus!=null)mVolRxBus.unsubscribe();
    }

    @Override
    public void show(Integer type, String e) {
        Snackbar.make(mFrameAplistRecycler, e, Snackbar.LENGTH_SHORT).show();
    }
}