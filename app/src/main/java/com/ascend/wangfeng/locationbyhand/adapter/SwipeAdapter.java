package com.ascend.wangfeng.locationbyhand.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.bean.NoteDoDeal;
import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.ArrayList;



/**
 * Created by fengye on 2016/11/18.
 * email 1040441325@qq.com
 */
public class SwipeAdapter extends SwipeMenuAdapter<SwipeAdapter.ViewHolder> {

    private ArrayList<NoteDo> mList;


    public SwipeAdapter(ArrayList<NoteDo> list) {
        mList = list;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_target,parent,
                false);
    }

    @Override
    public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new ViewHolder(realContentView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final NoteDo noteDo = mList.get(position);
        holder.mMac.setText(noteDo.getMac()+"");
        holder.mNote.setText(noteDo.getNote()+"");
        if (noteDo.getRing()){
            holder.ring.setImageResource(R.drawable.ring_fill);
        }else {
            holder.ring.setImageResource(R.drawable.ring_no_fill);
        }
        holder.ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NoteDoDeal deal = new NoteDoDeal(mList);
                deal.upDate(noteDo.getMac(),!noteDo.getRing());
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        TextView mMac;
        TextView mNote;
        ImageView ring;
        public ViewHolder(View itemView) {
            super(itemView);
            ring= (ImageView) itemView.findViewById(R.id.ring);
            mMac= (TextView) itemView.findViewById(R.id.mac);
            mNote = (TextView) itemView.findViewById(R.id.note);
        }
    }
}
