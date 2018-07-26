package com.ascend.wangfeng.locationbyhand.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;
import com.ascend.wangfeng.locationbyhand.bean.StaVo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


/**
 * Created by fengye on 2016/12/19.
 * email 1040441325@qq.com
 */
public class FormAdapter extends RecyclerView.Adapter<FormAdapter.ViewHolder> {
    private static final Integer TYPE_AP = 0;
    private static final Integer TYPE_STA = 1;
    private ArrayList<StaVo> mdata = new ArrayList<>();
    private Context mContext;
    private Integer mType;

    private OnItemClickLisener mClickLisener;
    private OnItemClickLisener mLongClickLisener;

    public FormAdapter(ArrayList<StaVo> data, Context context, Integer type) {
        this.mdata = data;
        this.mContext = context;
        this.mType = type;
    }

    public void setClickLisener(OnItemClickLisener lisener) {
        this.mClickLisener = lisener;
    }

    public void setLongClickLisener(OnItemClickLisener lisener) {
        this.mLongClickLisener = lisener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scan, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        StaVo apBean = mdata.get(position);
        if (apBean.getOui() != null) {
            if (apBean.getOui().indexOf("Apple") > -1) {
                holder.mImageView.setBackgroundResource(R.mipmap.ios);
            } else if (apBean.getOui().indexOf("HUAWEI") > -1) {
                holder.mImageView.setBackgroundResource(R.mipmap.huawei);
            } else if (apBean.getOui().indexOf("OPPO") > -1) {
                holder.mImageView.setBackgroundResource(R.mipmap.oppo);
            } else if (apBean.getOui().indexOf("SAMSUNG") > -1) {
                holder.mImageView.setBackgroundResource(R.mipmap.sam);
            } else if (apBean.getOui().indexOf("Xiaomi") > -1) {
                holder.mImageView.setBackgroundResource(R.mipmap.xiaomi);
            } else {
                holder.mImageView.setBackgroundResource(R.mipmap.phone);
            }
        } else {
            holder.mImageView.setBackgroundResource(R.mipmap.phone);
        }
        if (position == 0 && mType == TYPE_STA) {
            holder.mLayout.setBackgroundColor(mContext.getResources().getColor(R.color.selected));
        } else if (apBean.isTag()) {
            holder.mLayout.setBackgroundColor(mContext.getResources().getColor(R.color.target));

        } else {
            holder.mLayout.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

        //由于复用aplist的item， mMac和mApName是相反的
        if ("00:00:00:00:00:00".equals(apBean.getApmac())) {
            //未连接
            holder.mMacView.setText("未连接");
            holder.mMacView.setTextColor(ContextCompat.getColor(mContext, R.color.gray));


        } else {
            if (apBean.getChannel() == 0) {
                //有连接信息，但Ap不在范围内，无ap信息
                holder.mMacView.setText("未知");
                holder.mMacView.setTextColor(ContextCompat.getColor(mContext, R.color.gray));

            } else {
                //正常情况
                holder.mMacView.setText(apBean.getEssid() + "");
                holder.mMacView.setTextColor(ContextCompat.getColor(mContext, R.color.secondary_text));
            }
        }
        holder.mNameView.setText(apBean.getMac() +
                (apBean.isTag() ? "(" + apBean.getNote() + ")" : ""));
        holder.mSingalView.setText(apBean.getSignal() + "dBm");
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String time = format.format(apBean.getLtime());
        holder.mLtimeView.setText(time + "");

    }

    @Override
    public int getItemCount() {
        return mdata.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView mImageView;
        TextView mNameView;
        TextView mMacView;
        TextView mSingalView;
        TextView mLtimeView;
        LinearLayout mLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.img);
            mNameView = (TextView) itemView.findViewById(R.id.name);
            mMacView = (TextView) itemView.findViewById(R.id.mac);
            mSingalView = (TextView) itemView.findViewById(R.id.signal);
            mLtimeView = (TextView) itemView.findViewById(R.id.time);
            mLayout = (LinearLayout) itemView.findViewById(R.id.item_scan);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mClickLisener != null) {
                mClickLisener.onClick(itemView, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mLongClickLisener != null) {
                mLongClickLisener.onClick(itemView, getAdapterPosition());
            }
            return false;
        }
    }

    /**
     * @param img   图片
     * @param level 信号强度
     *              为图片控件设置图片
     */
    private void setImage(ImageView img, int level) {
        if (level < -90) {
            img.setImageResource(R.drawable.ic_signal_wifi_0_bar_black_36dp);


        } else if (level < -70) {
            img.setImageResource(R.drawable.ic_signal_wifi_1_bar_black_36dp);

        } else if (level < -50) {
            img.setImageResource(R.drawable.ic_signal_wifi_2_bar_black_36dp);
        } else if (level < -30) {
            img.setImageResource(R.drawable.ic_signal_wifi_3_bar_black_36dp);
        } else {
            img.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_36dp);
        }
    }


    /**
     * @param mdata AP信息集合
     *              数据置入适配器
     */

}
