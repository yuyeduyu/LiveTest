package com.ascend.wangfeng.locationbyhand.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.ConnectRelation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by fengye on 2017/6/27.
 * email 1040441325@qq.com
 */

public class LogRelationAdapter extends RecyclerView.Adapter<LogRelationAdapter.ViewHolder>{
    private ArrayList<ConnectRelation> mList;

    public LogRelationAdapter(ArrayList<ConnectRelation> list) {
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_log_relation, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ConnectRelation con = mList.get(position);
        holder.mac.setText(con.getMac()+"");
        holder.count.setText(con.getCount()+"");
        SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        String timeStart =simpleDateFormat.format(new Date(con.getTimeStart()));
        String timeEnd = " ";
        if (con.getTimeEnd()>0)
        timeEnd=simpleDateFormat.format(new Date(con.getTimeEnd()));
        holder.timeSart.setText(timeStart+"");
        holder.timeEnd.setText(timeEnd+"");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mac;
        TextView count;
        TextView timeSart;
        TextView timeEnd;

        public ViewHolder(View itemView) {
            super(itemView);
            mac = (TextView) itemView.findViewById(R.id.mac);
            count = (TextView) itemView.findViewById(R.id.count);
            timeSart = (TextView) itemView.findViewById(R.id.time_start);
            timeEnd = (TextView) itemView.findViewById(R.id.time_end);

        }
    }
}
