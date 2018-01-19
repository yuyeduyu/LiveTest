package com.ascend.wangfeng.locationbyhand.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;

/**
 * Created by fengye on 2017/3/15.
 * email 1040441325@qq.com
 */

public class ApListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

    ImageView img;
    TextView name;
    TextView mac;
    TextView signal;
    TextView time;
    LinearLayout mLayout;
    OnItemClickLisener mClickLisener;
    OnItemClickLisener mLongClickLisener;

    public ApListViewHolder(View itemView,OnItemClickLisener clickLisener,OnItemClickLisener longClickLisener) {
        super(itemView);
        img = (ImageView)itemView.findViewById(R.id.img);
        name = (TextView)itemView.findViewById(R.id.name);
        mac = (TextView)itemView.findViewById(R.id.mac);
        signal =(TextView)itemView.findViewById(R.id.signal);
        time =(TextView)itemView.findViewById(R.id.time);
        mLayout = (LinearLayout) itemView.findViewById(R.id.item_scan);
        this.mClickLisener=clickLisener;
        this.mLongClickLisener =longClickLisener;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (mClickLisener!=null){
            mClickLisener.onClick(itemView,getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (mLongClickLisener!=null){
            mLongClickLisener.onClick(itemView,getAdapterPosition());
        }
        return true;
    }
}
