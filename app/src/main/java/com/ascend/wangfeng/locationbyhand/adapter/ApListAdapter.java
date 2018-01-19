package com.ascend.wangfeng.locationbyhand.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.bean.ApVo;

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
                .inflate(R.layout.item_scan, parent, false);
        return new ApListViewHolder(itemView, mClickLisener, mLongClickListener);
    }

    @Override
    public void onBindViewHolder(ApListViewHolder holder, int position) {
        ApVo data = mData.get(position);

        holder.img.setBackgroundResource(R.drawable.icon_wifi);
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

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
