package com.ascend.wangfeng.locationbyhand.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;
import com.ascend.wangfeng.locationbyhand.bean.Ghz;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by fengye on 2017/2/8.
 * email 1040441325@qq.com
 */

public class ApListAdapter extends RecyclerView.Adapter<ApListViewHolder> {
    private List<ApVo> mData;
    private OnItemClickLisener mClickLisener;
    private OnItemClickLisener mLongClickListener;

    public ApListAdapter(List<ApVo> data) {
        mData = data;
    }

    public ApListAdapter(List<ApVo> data, OnItemClickLisener clickLisener,
                         OnItemClickLisener longClickListener) {
        mData = data;
        mClickLisener = clickLisener;
        mLongClickListener = longClickListener;
    }

    @Override
    public ApListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ap, parent, false);
        return new ApListViewHolder(itemView, mClickLisener, mLongClickListener);
    }

    @Override
    public void onBindViewHolder(ApListViewHolder holder, int position) {
        ApVo data = mData.get(position);

//        holder.img.setBackgroundResource(R.drawable.icon_wifi);
        if (MyApplication.mGhz == Ghz.G24) {
            holder.tv_ghz.setText("2.4G");
        } else {
            holder.tv_ghz.setText("5.8G");
        }
        if (data.isTag()) {
            holder.mLayout.setBackgroundResource(R.color.accent);
            holder.name.setText(data.getEssid() + "(" + data.getNote() + ")");
        } else {
            holder.mLayout.setBackgroundResource(R.color.white);
            holder.name.setText(data.getEssid() + "");
        }
        holder.mac.setText(data.getBssid() + "");
        holder.signal.setText(data.getSignal() + "dBm");
        SimpleDateFormat format =  new SimpleDateFormat("HH:mm:ss");
        String time = format.format(data.getLtime());
        holder.time.setText(time + "");
        if (Config.getApPasswordMac()!=null&& Config.getApPasswordMac().equals(data.getBssid())){
            holder.mShowPasswordImg.setVisibility(View.VISIBLE);
        }else {
            holder.mShowPasswordImg.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
