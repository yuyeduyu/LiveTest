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

import butterknife.BindView;

/**
 * 统计 adapter
 *
 * @author lishanhui
 *         created at 2018-07-03 15:09
 */
public class StatisticAdapter extends RecyclerView.Adapter<StatisticAdapter.ViewHolder> {

    private List<Log> mList;
    private Context context;
    public StatisticAdapter(List<Log> list, Context context) {
        mList = list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistic, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.id.setText(position + 1 + "");
        holder.mac.setText(mList.get(position).getMac());
        holder.time.setText(TimeUtil.getTime(mList.get(position).getLtime(), "HH:mm:ss"));
//        private int type;  0:ap, 1:sta
        if (mList.get(position).getType()==0){
            //ap
            holder.style.setText("AP");
            holder.style.setTextColor(ContextCompat.getColor(context,R.color.c13));
        }else if (mList.get(position).getType()==1){
            holder.style.setText("终端");
            holder.style.setTextColor(ContextCompat.getColor(context,R.color.orange));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView mac;
        TextView time;
        TextView style;

        public ViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            mac = (TextView) itemView.findViewById(R.id.mac);
            time = (TextView) itemView.findViewById(R.id.time);
            style = (TextView) itemView.findViewById(R.id.style);
        }
    }
}
