package com.dushan.dev.mapper.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dushan.dev.mapper.Data.User;
import com.dushan.dev.mapper.R;

import java.util.ArrayList;

public class SimpleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<User> modelList;

    private OnItemClickListener mItemClickListener;


    public SimpleListAdapter(Context context, ArrayList<User> modelList) {
        this.mContext = context;
        this.modelList = modelList;
    }

    public void updateList(ArrayList<User> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_simple_list, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        //Here you can fill your row view
        if (holder instanceof ViewHolder) {
            final User model = getItem(position);
            ViewHolder genericViewHolder = (ViewHolder) holder;

            genericViewHolder.itemTxtTitle.setText(model.getName());
            genericViewHolder.itemTxtMessage.setText(model.getPhoneNumber());
            Glide.with(mContext).load(model.getImage()).apply(RequestOptions.circleCropTransform()).into(genericViewHolder.imgUser);
        }
    }


    @Override
    public int getItemCount() {

        return modelList.size();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    private User getItem(int position) {
        return modelList.get(position);
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position, User model);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgUser;
        private TextView itemTxtTitle;
        private TextView itemTxtMessage;

        public ViewHolder(final View itemView) {
            super(itemView);

            // ButterKnife.bind(this, itemView);

            this.imgUser = (ImageView) itemView.findViewById(R.id.img_user);
            this.itemTxtTitle = (TextView) itemView.findViewById(R.id.item_txt_title);
            this.itemTxtMessage = (TextView) itemView.findViewById(R.id.item_txt_message);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(itemView, getAdapterPosition(), modelList.get(getAdapterPosition()));


                }
            });

        }
    }

}

