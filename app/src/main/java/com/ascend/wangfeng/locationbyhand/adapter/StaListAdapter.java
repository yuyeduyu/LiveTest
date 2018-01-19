package com.ascend.wangfeng.locationbyhand.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by fengye on 2017/2/8.
 * email 1040441325@qq.com
 */

public class StaListAdapter extends RecyclerView.Adapter<ApListViewHolder> {
    private List<StaVo> mData;
    private OnItemClickLisener mClickLisener;
    private OnItemClickLisener mLongClickListener;

    public StaListAdapter(List<StaVo> data) {
        mData = data;
    }

    public StaListAdapter(List<StaVo> data, OnItemClickLisener clickLisener,
                          OnItemClickLisener longClickListener) {
        mData = data;
        mClickLisener = clickLisener;
        mLongClickListener = longClickListener;
    }

    @Override
    public ApListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan, parent,
                false);
        return new ApListViewHolder(item, mClickLisener, mLongClickListener);
    }

    @Override
    public void onBindViewHolder(ApListViewHolder holder, int position) {
        StaVo data = mData.get(position);
        holder.img.setBackgroundResource(R.drawable.icon_phone);
        if (data.isTag()) {
            holder.mLayout.setBackgroundResource(R.color.accent);
            holder.name.setText(data.getMac() + "(" + data.getNote() + ")");
        } else {
            holder.mLayout.setBackgroundResource(R.color.white);
            holder.name.setText(data.getMac() + "");
        }
        if (MyApplication.mContext.getResources().getString(R.string.ap_mac_null).equals(data.getApmac())){
            holder.mac.setText("未连接");
            holder.mac.setTextColor(MyApplication.mContext.getResources().getColor(R.color.gray));
        }else {
            holder.mac.setTextColor(MyApplication.mContext.getResources().getColor(R.color.secondary_text));
        holder.mac.setText(data.getEssid() + "");}
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
