package com.ascend.wangfeng.locationbyhand.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.util.hashMap.MapBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengye on 2018/2/23.
 * email 1040441325@qq.com
 */

public class VirtualIdentityAdapter extends RecyclerView.Adapter<VirtualIdentityAdapter.SubViewHolder>{
    private List<MapBean> mMapBeans;
    public VirtualIdentityAdapter() {
        mMapBeans=new ArrayList<>();
    }
    public void setIdentities(List<MapBean> identities){
        this.mMapBeans.clear();
        mMapBeans.addAll(identities);
        notifyDataSetChanged();
    }
    @Override
    public SubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vid_sub,parent,false));
    }

    @Override
    public void onBindViewHolder(SubViewHolder holder, int position) {
        holder.typeTv.setText(mMapBeans.get(position).getKey().toString());
        holder.valueTv.setText(mMapBeans.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return mMapBeans.size();
    }

    class SubViewHolder extends RecyclerView.ViewHolder {
        TextView typeTv;
        TextView valueTv;
        public SubViewHolder(View itemView) {
            super(itemView);
            typeTv = (TextView) itemView.findViewById(R.id.type);
            valueTv = (TextView) itemView.findViewById(R.id.value);
        }
    }
}
