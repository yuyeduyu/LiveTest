package com.ascend.wangfeng.locationbyhand.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.bean.EventBean;
import com.ascend.wangfeng.locationbyhand.util.TimeUtil;

import java.util.List;

/**
 * Created by fengye on 2017/8/21.
 * email 1040441325@qq.com
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{
    private List<EventBean>  mList;
    private  IOnListener mListener;

    public TaskAdapter(List<EventBean> list) {
        mList = list;
    }
    public void setListener(IOnListener listener){
        this.mListener =listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final EventBean bean =mList.get(position);
        holder.itemView.setTag(position);
        holder.title.setText(bean.getTitle());
        if (bean.isFinish()){
            holder.start.setText(TimeUtil.getTime(bean.getStart()));
            holder.end.setText(TimeUtil.getTime(bean.getEnd()));
            holder.img.setVisibility(View.INVISIBLE);
        }else {
            holder.img.setVisibility(View.VISIBLE);
            if (bean.isRun()){
                holder.img.setBackgroundResource(R.drawable.stop);
                holder.start.setText(TimeUtil.getTime(bean.getStart()));
            }else {
                holder.img.setBackgroundResource(R.drawable.start);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;
        TextView start;
        TextView end;
        public ViewHolder(final View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
            title = (TextView) itemView.findViewById(R.id.title);
            start = (TextView) itemView.findViewById(R.id.start);
            end = (TextView) itemView.findViewById(R.id.end);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener!=null)
                        mListener.onClick(view, (Integer) itemView.getTag());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mListener!=null)
                        mListener.onLongClick(view, (Integer) itemView.getTag());
                    return false;
                }
            });
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener!=null)
                        mListener.onButtonClick(view, (Integer) itemView.getTag());
                }
            });
        }
    }
    public interface  IOnListener{
        void onClick(View view,int position);
        void onLongClick(View view,int position);
        void onButtonClick(View view,int position);
    }
}
