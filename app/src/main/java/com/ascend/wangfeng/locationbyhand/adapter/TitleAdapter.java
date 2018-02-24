package com.ascend.wangfeng.locationbyhand.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.util.hashMap.MapBean;

import java.util.ArrayList;

/**
 * Created by fengye on 2018/2/23.
 * email 1040441325@qq.com
 */

public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.ViewHolder>{
    private ArrayList<MapBean> mMapBeans;
    public TitleAdapter() {
        mMapBeans =new ArrayList<>();
    }
    public void setKeyValues(ArrayList<MapBean> values){
        mMapBeans.clear();
        mMapBeans.addAll(values);
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_virtual_identity_title,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.typeTv.setText(mMapBeans.get(position).getKey().toString());
        holder.valueTv.setText(mMapBeans.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return mMapBeans.size();
    }

    class  ViewHolder extends RecyclerView.ViewHolder {
        TextView typeTv;
        TextView valueTv;
        public ViewHolder(View itemView) {
            super(itemView);
            typeTv = (TextView) itemView.findViewById(R.id.type);
            valueTv = (TextView) itemView.findViewById(R.id.value);
        }
    }
}
