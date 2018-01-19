package com.ascend.wangfeng.locationbyhand.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by fengye on 2017/6/27.
 * email 1040441325@qq.com
 */

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder>{
    private ArrayList<Log> mData;

    public LogAdapter(ArrayList<Log> data) {
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_log_main,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log log = mData.get(position);
        SimpleDateFormat sim =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.timeView.setText(sim.format(new Date(log.getLtime())));
        holder.signalView.setText(log.getDistance()+" ");
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeView;
        TextView signalView;
        public ViewHolder(View itemView) {
            super(itemView);
            timeView = (TextView) itemView.findViewById(R.id.ltime);
            signalView = (TextView) itemView.findViewById(R.id.dis);
        }
    }
}
