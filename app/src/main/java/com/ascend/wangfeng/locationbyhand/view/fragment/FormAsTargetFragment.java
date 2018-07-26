package com.ascend.wangfeng.locationbyhand.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.adapter.FormAdapter;
import com.ascend.wangfeng.locationbyhand.adapter.MyItemDecoration;
import com.ascend.wangfeng.locationbyhand.adapter.OnItemClickLisener;
import com.ascend.wangfeng.locationbyhand.api.BaseSubcribe;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.NoteVo;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.contract.FormContract;
import com.ascend.wangfeng.locationbyhand.dialog.IShowView;
import com.ascend.wangfeng.locationbyhand.dialog.TargetSetDialog;
import com.ascend.wangfeng.locationbyhand.event.RxBus;
import com.ascend.wangfeng.locationbyhand.event.ble.RelationEvent;
import com.ascend.wangfeng.locationbyhand.presenter.FormPresenterImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


/**
 * Created by fengye on 2016/12/19.
 * email 1040441325@qq.com
 * 定位目标的雷达显示
 */
public class FormAsTargetFragment extends Fragment implements FormContract.View, IShowView {


    @BindView(R.id.img)
    ImageView mImg;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.mac)
    TextView mMac;
    @BindView(R.id.signal)
    TextView mSignal;
    @BindView(R.id.time)
    TextView mTime;
    @BindView(R.id.item_scan)
    LinearLayout mItemScan;
    @BindView(R.id.sta_count)
    TextView mStaCount;
    @BindView(R.id.recycler)
    RecyclerView mRecycler;
    private FormPresenterImpl mPresenter;
    private FormAdapter adapter;
    private ArrayList<StaVo> mData;
    private ApVo ap;
    private int apOrMac;
    private String mac;
    private Subscription formRxbus;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void initView() {
        mRecycler.setAdapter(adapter);
        mRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycler.addItemDecoration(new MyItemDecoration(getContext(), LinearLayoutManager.HORIZONTAL));
    }

    private void initListener() {
        adapter.setLongClickLisener(new OnItemClickLisener() {
            @Override
            public void onClick(View view, int position) {
                if (position == -1) return;
                else {
                    NoteVo noteVo = new NoteVo();
                    noteVo.setMac(mData.get(position).getMac());
                    noteVo.setNote(mData.get(position).getNote() + "");
                    TargetSetDialog.showDialog((AppCompatActivity) getActivity(), noteVo,
                            FormAsTargetFragment.this,false);
                }

            }
        });
    }

    private void initData() {
        apOrMac = getActivity().getIntent().getIntExtra("type", 0);
        mac = getActivity().getIntent().getStringExtra("mac");
        // mPresenter = new FormPresenterImpl(this);
        mData = new ArrayList<>();
        adapter = new FormAdapter(mData, getContext(), apOrMac);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initData();
        initListener();
        initView();
        initRxbus();
        //mPresenter.update(mac, apOrMac);
    }

    private void initRxbus() {
        formRxbus = RxBus.getDefault().toObservable(RelationEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubcribe<RelationEvent>() {
                    @Override
                    public void onNext(RelationEvent event) {

                        update(event.getApVo(),
                                event.getStaVos());
                        updateStaSum(event.getStaVos().size());
                    }
                });
    }

    @Override
    public void onDestroy() {
        // mPresenter.stopUpdate();
        super.onDestroy();
        if (formRxbus != null) formRxbus.unsubscribe();
    }

    @Override
    public void update(@Nullable ApVo apVo, List<StaVo> staVos) {
        ap = apVo;
        mItemScan.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (ap != null) {
                    NoteVo noteVo = new NoteVo();
                    noteVo.setMac(ap.getBssid());
                    noteVo.setNote(ap.getNote() + "");
                    TargetSetDialog.showDialog((AppCompatActivity) getActivity(), noteVo,
                            FormAsTargetFragment.this,false);
                }

                return false;
            }
        });
        //刷新ap
        if (ap.getBssid() != null) {
            mImg.setImageResource(R.drawable.icon_wifi);
            mName.setText("" + ap.getBssid());
            mMac.setText("" + ap.getEssid());
            mSignal.setText(ap.getSignal() + "dBm");
            SimpleDateFormat format =  new SimpleDateFormat("HH:mm:ss");
            String time = format.format(ap.getLtime());
            mTime.setText(time);
            if (apOrMac == 0) {
                mItemScan.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.selected));
            }
            if (ap.isTag()) {
                mName.setText(ap.getBssid() + "(" + ap.getNote() + ")");
                if (apOrMac == 1) {
                    mItemScan.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.target));
                }
            }
        }
        //刷新sta列表
        mData.clear();
        mData.addAll(staVos);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void show(Integer type, String e) {
        switch (type) {
            case 0:
                Snackbar.make(mImg, e, Snackbar.LENGTH_LONG).show();
                break;
            case 1:
                Snackbar.make(mImg, e, Snackbar.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void updateStaSum(int count) {
        mStaCount.setText("终  端  数    " + count);
    }

}
