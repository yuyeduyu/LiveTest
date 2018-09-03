package com.ascend.wangfeng.locationbyhand.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.Config;
import com.ascend.wangfeng.locationbyhand.MyApplication;
import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.Log;
import com.ascend.wangfeng.locationbyhand.util.TimeUtil;

import java.util.List;

/**
 * 作者：lishanhui on 2018-06-11.
 * 描述：
 */

public class AllLogAdapter extends RecyclerView.Adapter<AllLogAdapter.Holder> {


    private Context mContext;
    private List<Log> datas;
    private OnItemClickListener mItemClickListener;

    public AllLogAdapter(Context context, List<Log> datas) {
        mContext = context;
        this.datas = datas;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(mContext).inflate(R.layout.item_alllogs, parent, false);
        return new Holder(root);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.mac.setText(datas.get(position).getMac());
        holder.time.setText(TimeUtil.getTime(datas.get(position).getLtime(),"yy-MM-dd HH:mm:ss"));
        if (MyApplication.getAppVersion() == Config.C_MINI){
            holder.dbm.setText(format(datas.get(position).getDistance()));
            holder.image.setImageResource(getResourceID(datas.get(position).getDistance()));
        }else
            holder.dbm.setText(datas.get(position).getDistance() + "");
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView mac;
        TextView time;
        TextView dbm;
        ImageView image;
        Holder(View itemView) {
            super(itemView);
            mac = (TextView) itemView.findViewById(R.id.mac);
            time = (TextView) itemView.findViewById(R.id.time);
            dbm = (TextView) itemView.findViewById(R.id.dbm);
            image = (ImageView) itemView.findViewById(R.id.image);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

            }
        }

        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
            }
            return false;
        }
    }
    protected int getResourceID(Integer integer) {
        if (integer > -50) {
            return R.drawable.ic_dis_fir;
        } else if (integer > -60) {
            return R.drawable.ic_dis_sec;
        } else {
            return R.drawable.ic_dis_thi;
        }
    }
    public String format(Integer integer) {
        if (integer > -50) {
            return "2米内";
        } else if (integer > -60) {
            return "5米内";
        } else {
            return "全范围";
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }
}