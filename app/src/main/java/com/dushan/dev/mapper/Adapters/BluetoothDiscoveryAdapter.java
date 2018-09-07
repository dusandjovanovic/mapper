package com.dushan.dev.mapper.Adapters;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dushan.dev.mapper.Data.BluetoothDeviceInstance;
import com.dushan.dev.mapper.Data.User;
import com.dushan.dev.mapper.Interfaces.GlideApp;
import com.dushan.dev.mapper.R;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDiscoveryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<BluetoothDevice> modelList;
    private OnItemClickListener mItemClickListener;


    public BluetoothDiscoveryAdapter(Context context, List<BluetoothDevice> modelList) {
        this.mContext = context;
        this.modelList = modelList;
    }

    public void updateList(List<BluetoothDevice> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bluetooth_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        //Here you can fill your row view
        if (holder instanceof ViewHolder) {
            final BluetoothDevice model = modelList.get(position);
            ViewHolder genericViewHolder = (ViewHolder) holder;

            genericViewHolder.deviceName.setText(model.getName());
            genericViewHolder.deviceHardwareAddress.setText(model.getAddress());
        }
    }


    @Override
    public int getItemCount() {

        return modelList.size();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, BluetoothDevice model);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView deviceName;
        private TextView deviceHardwareAddress;

        public ViewHolder(final View itemView) {
            super(itemView);

            // ButterKnife.bind(this, itemView);

            this.deviceName = (TextView) itemView.findViewById(R.id.deviceName);
            this.deviceHardwareAddress = (TextView) itemView.findViewById(R.id.deviceHardwareAddress);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(itemView, getAdapterPosition(), modelList.get(getAdapterPosition()));


                }
            });

        }
    }

}

