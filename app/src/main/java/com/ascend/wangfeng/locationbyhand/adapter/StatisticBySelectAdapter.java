package com.ascend.wangfeng.locationbyhand.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.Log;
import com.ascend.wangfeng.locationbyhand.util.TimeUtil;

import java.util.List;
import java.util.Map;

/**
 * 统计 adapter
 *
 * @author lishanhui
 *         created at 2018-07-03 15:09
 */
public class StatisticBySelectAdapter extends RecyclerView.Adapter<StatisticBySelectAdapter.ViewHolder> {

    private List<Map<String,String>> mList;
    private Context context;
    public StatisticBySelectAdapter(List<Map<String,String>> list, Context context) {
        mList = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistic_select, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position==0){
            holder.id.setText("序号");
            holder.ap.setText("AP数量");
            holder.sta.setText("终端数量");
            holder.time.setText("日期");
        }else {
            Map<String,String> data = mList.get(position-1);
            holder.id.setText(String.valueOf(position));
            holder.ap.setText(data.get("ap"));
            holder.sta.setText(data.get("sta"));
            holder.time.setText(TimeUtil.getTime(Long.parseLong(data.get("time")), "yyyy/MM//dd"));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size()+1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView ap;
        TextView time;
        TextView sta;

        public ViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            ap = (TextView) itemView.findViewById(R.id.ap);
            time = (TextView) itemView.findViewById(R.id.time);
            sta = (TextView) itemView.findViewById(R.id.sta);
        }
    }
}
