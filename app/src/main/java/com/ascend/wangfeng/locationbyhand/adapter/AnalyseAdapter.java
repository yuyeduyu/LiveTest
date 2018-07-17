package com.ascend.wangfeng.locationbyhand.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.bean.HistoryMacBean;

import java.util.List;

/**
 * 数据分析 adapter
 * @author lishanhui
 * created at 2018-07-03 15:09
 */
public class AnalyseAdapter extends RecyclerView.Adapter<AnalyseAdapter.ViewHolder>{
    private List<HistoryMacBean> mList ;

    public AnalyseAdapter(List<HistoryMacBean> list) {
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_analye_chart,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.id.setText(position+1+"");
        holder.mac.setText(mList.get(position).getMac());
        holder.time.setText(mList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView id;
        TextView mac;
        TextView time;
        public ViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            mac = (TextView) itemView.findViewById(R.id.mac);
            time = (TextView) itemView.findViewById(R.id.time);
        }
    }
}
