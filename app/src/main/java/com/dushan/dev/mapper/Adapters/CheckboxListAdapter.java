package com.dushan.dev.mapper.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dushan.dev.mapper.Data.User;
import com.dushan.dev.mapper.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CheckboxListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<User> modelList;

    private OnItemClickListener mItemClickListener;
    private OnCheckedListener mOnCheckedListener;

    private Set<Integer> checkSet = new HashSet<>();

    public CheckboxListAdapter(Context context, ArrayList<User> modelList) {
        this.mContext = context;
        this.modelList = modelList;
    }

    public void updateList(ArrayList<User> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_checkbox_list, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final User model = getItem(position);
            ViewHolder genericViewHolder = (ViewHolder) holder;

            genericViewHolder.itemTxtTitle.setText(model.getName());
            genericViewHolder.itemTxtMessage.setText(model.getPhoneNumber());
            Glide.with(mContext).load(model.getImage()).apply(RequestOptions.circleCropTransform()).into(genericViewHolder.imgUser);

            genericViewHolder.itemCheckList.setOnCheckedChangeListener(null);
            genericViewHolder.itemCheckList.setChecked(checkSet.contains(position));

            genericViewHolder.itemCheckList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        checkSet.add(position);
                    } else {
                        checkSet.remove(position);
                    }

                    mOnCheckedListener.onChecked(buttonView, isChecked, position, modelList.get(position));

                }
            });
        }
    }


    @Override
    public int getItemCount() {

        return modelList.size();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void SetOnCheckedListener(final OnCheckedListener onCheckedListener) {
        this.mOnCheckedListener = onCheckedListener;

    }

    private User getItem(int position) {
        return modelList.get(position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position, User model);
    }


    public interface OnCheckedListener {
        void onChecked(View view, boolean isChecked, int position, User model);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgUser;
        private TextView itemTxtTitle;
        private TextView itemTxtMessage;


        private CheckBox itemCheckList;

        public ViewHolder(final View itemView) {
            super(itemView);

            this.imgUser = (ImageView) itemView.findViewById(R.id.img_user);
            this.itemTxtTitle = (TextView) itemView.findViewById(R.id.item_txt_title);
            this.itemTxtMessage = (TextView) itemView.findViewById(R.id.item_txt_message);
            this.itemCheckList = (CheckBox) itemView.findViewById(R.id.check_list);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mItemClickListener.onItemClick(itemView, getAdapterPosition(), modelList.get(getAdapterPosition()));

                }
            });

        }
    }

}