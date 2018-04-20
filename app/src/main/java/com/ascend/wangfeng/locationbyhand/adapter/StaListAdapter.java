package com.ascend.wangfeng.locationbyhand.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;
import com.ascend.wangfeng.locationbyhand.view.activity.VirtualIdentityActivity;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fengye on 2017/2/8.
 * email 1040441325@qq.com
 */

public class StaListAdapter extends RecyclerView.Adapter<ApListViewHolder> {
    public static final int VIRTURAL_IDENTITY_ICON_COUNT = 3;//最多显示几个图标
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
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scan, parent, false);
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
        if (MyApplication.mContext.getResources().getString(R.string.ap_mac_null)
                .equals(data.getApmac())){
            holder.mac.setText("未连接");
            holder.mac.setTextColor(MyApplication.mContext.getResources()
                    .getColor(R.color.gray));
        }else {
            holder.mac.setTextColor(MyApplication.mContext.getResources()
                    .getColor(R.color.secondary_text));
        holder.mac.setText(data.getEssid() + "");}
        holder.signal.setText(data.getSignal() + "dBm");
        SimpleDateFormat format =  new SimpleDateFormat("HH:mm:ss");
        String time = format.format(data.getLtime());
        holder.time.setText(time + "");
        addVId(holder, data);
    }

    private void addVId(final ApListViewHolder holder, final StaVo data) {
        final HashMap<Integer,String> identities = data.getIdentities();
        holder.mVIdLayout.removeAllViews();

        if (identities != null&&identities.size() > 0){
            holder.mVIdLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转虚拟身份页面
                    Intent intent = new Intent( holder.itemView.getContext(),
                            VirtualIdentityActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("sta",data);
                    intent.putExtras(bundle);
                    holder.itemView.getContext().startActivity(intent);
                }
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.MATCH_PARENT,1.0f);
            for (Map.Entry<Integer,String> entry: identities.entrySet()) {
                ImageView imageView = new ImageView(holder.itemView.getContext());
                imageView.setLayoutParams(lp);
                imageView.setPadding(0,0,0,0);
                switch (entry.getKey()){
                    case 1:
                        imageView.setImageResource(R.drawable.phone);
                        holder.mVIdLayout.addView(imageView);
                        break;
                    case 49:
                        imageView.setImageResource(R.drawable.alipay);
                        holder.mVIdLayout.addView(imageView);
                        break;
                    case 4:
                        imageView.setImageResource(R.drawable.qq);
                        holder.mVIdLayout.addView(imageView);
                        break;
                    case 5:
                        imageView.setImageResource(R.drawable.wechat);
                        holder.mVIdLayout.addView(imageView);
                        break;
                    case 6:
                        imageView.setImageResource(R.drawable.taobao);
                        holder.mVIdLayout.addView(imageView);
                        break;
                    case 3:
                        imageView.setImageResource(R.drawable.sim_card);
                        holder.mVIdLayout.addView(imageView);
                        break;
                    default:
                        break;
                }
                if (holder.mVIdLayout.getChildCount()>= VIRTURAL_IDENTITY_ICON_COUNT)break;
            }
            if (holder.mVIdLayout.getChildCount()<identities.size()){
                ImageView imageView = new ImageView(holder.itemView.getContext());
                imageView.setLayoutParams(lp);
                imageView.setPadding(0,0,0,0);
                imageView.setImageResource(R.drawable.more);
                holder.mVIdLayout.addView(imageView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
