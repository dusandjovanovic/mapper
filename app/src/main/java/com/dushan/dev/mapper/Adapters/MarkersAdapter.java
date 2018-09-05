package com.dushan.dev.mapper.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dushan.dev.mapper.Activities.HomeActivity;
import com.dushan.dev.mapper.Activities.LoginActivity;
import com.dushan.dev.mapper.Data.Marker;
import com.dushan.dev.mapper.Interfaces.ClickListener;
import com.dushan.dev.mapper.R;

import java.util.List;

public class MarkersAdapter extends RecyclerView.Adapter<MarkersAdapter.MarkersHolder> {

    private ClickListener mListener;
    private List<Marker> mMarkerList;
    private Context context;

    public MarkersAdapter(Context context, List<Marker> mMarkerList, ClickListener listener){
        this.context = context;
        this.mMarkerList = mMarkerList;
        mListener = listener;
    }

    @NonNull
    @Override
    public MarkersAdapter.MarkersHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_card, null);
        MarkersHolder viewHolder = new MarkersHolder(view, this.mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MarkersAdapter.MarkersHolder markersHandler, int i) {
        Marker markerInstance = mMarkerList.get(i);
        String category = markerInstance.getCategory();
        switch (category){
            case ("Travel"):
                markersHandler.markerCategoryImage.setColorFilter(context.getResources().getColor(R.color.markerTravel));
                markersHandler.markerCategory.setTextColor(context.getResources().getColor(R.color.markerTravel));
                break;
            case ("Nature"):
                markersHandler.markerCategoryImage.setColorFilter(context.getResources().getColor(R.color.markerNature));
                markersHandler.markerCategory.setTextColor(context.getResources().getColor(R.color.markerNature));
                break;
            case ("Avoid"):
                markersHandler.markerCategoryImage.setColorFilter(context.getResources().getColor(R.color.markerAvoid));
                markersHandler.markerCategory.setTextColor(context.getResources().getColor(R.color.markerAvoid));
                break;
            default:
                markersHandler.markerCategoryImage.setColorFilter(context.getResources().getColor(R.color.marker));
                markersHandler.markerCategory.setTextColor(context.getResources().getColor(R.color.marker));
        }
        Glide.with(context).load(markerInstance.getImageURL()).into(markersHandler.markerImage);
        markersHandler.markerName.setText(markerInstance.getName());
        markersHandler.markerDescription.setText(markerInstance.getDescription());
        markersHandler.markerCategory.setText(category);
    }

    @Override
    public int getItemCount() {
        if (mMarkerList != null)
            return mMarkerList.size();
        else
        return 0;
    }

    public class MarkersHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ClickListener mListener;
        // each data item is just a string in this case
        public TextView markerCategory, markerName, markerDescription;
        public ImageView markerImage, markerCategoryImage;
        public MarkersHolder(View view, ClickListener listener) {
            super(view);
            this.markerName = view.findViewById(R.id.markerName);
            this.markerImage = view.findViewById(R.id.markerImage);
            this.markerCategory = view.findViewById(R.id.markerCategory);
            this.markerDescription = view.findViewById(R.id.markerDescription);
            this.markerCategoryImage = view.findViewById(R.id.markerCategoryImage);
            mListener = listener;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    public List<Marker> getmMarkerList() {
        return mMarkerList;
    }

    public void setmMarkerList(List<Marker> mMarkerList) {
        this.mMarkerList = mMarkerList;
    }
}
