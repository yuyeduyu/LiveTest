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

public class VirtualIdentityAdapter extends RecyclerView.Adapter<VirtualIdentityAdapter.SubViewHolder> {
    private List<MapBean> mMapBeans;

    public VirtualIdentityAdapter() {
        mMapBeans = new ArrayList<>();
    }

    public void setIdentities(List<MapBean> identities) {
        this.mMapBeans.clear();
        mMapBeans.addAll(identities);
        notifyDataSetChanged();
    }

    @Override
    public SubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SubViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vid_sub, parent, false));
    }

    @Override
    public void onBindViewHolder(SubViewHolder holder, int position) {
        try {
            //根据返回类型，显示虚拟身份
            switch (Integer.valueOf(mMapBeans.get(position).getKey().toString())) {
                case 1:
                    holder.typeTv.setText("phone");
                    break;
                case 2:
                    holder.typeTv.setText("imei");
                    break;
                case 3:
                    holder.typeTv.setText("imsi");
                    break;
                case 4:
                    holder.typeTv.setText("qq");
                    break;
                case 5:
                    holder.typeTv.setText("weixin");
                    break;
                case 6:
                    holder.typeTv.setText("taobao");
                    break;
                case 7:
                    holder.typeTv.setText("weibo");
                    break;
                case 8:
                    holder.typeTv.setText("baidu");
                    break;
                case 16:
                    holder.typeTv.setText("jingdong");
                    break;
                case 18:
                    holder.typeTv.setText("miliao");
                    break;
                case 20:
                    holder.typeTv.setText("momo");
                    break;
                case 49:
                    holder.typeTv.setText("支付宝");
                    break;
                case 50:
                    holder.typeTv.setText("好友qq");
                    break;
            }
        } catch (Exception e) {
            holder.typeTv.setText(mMapBeans.get(position).getKey().toString());
        }

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
