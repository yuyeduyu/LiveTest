package com.ascend.wangfeng.locationbyhand.adapter;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ascend.wangfeng.locationbyhand.R;

import java.util.List;

/**
 * Created by fengye on 2017/8/10.
 * email 1040441325@qq.com
 */

public class BleAdapter extends RecyclerView.Adapter<BleAdapter.ViewHolder>{
    private List<BluetoothDevice> mDevices;
    private OnItemListener mOnItemListener;

    public BleAdapter(List<BluetoothDevice> devices) {
        mDevices = devices;
    }
    public void setOnItemListener(OnItemListener listener){
        this.mOnItemListener =listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ble,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.name.setText(mDevices.get(position).getName());
        holder.mac.setText(mDevices.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        TextView mac;
        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            mac = (TextView) itemView.findViewById(R.id.mac);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemListener!=null){
                mOnItemListener.onClick(view, (Integer) itemView.getTag());
            }
        }
    }

    public interface OnItemListener{
        void onClick(View view,int position);
    }

}
