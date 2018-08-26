package com.dushan.dev.mapper.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dushan.dev.mapper.Data.Marker;
import com.dushan.dev.mapper.R;

import java.util.List;

public class MarkersAdapter extends RecyclerView.Adapter<MarkersAdapter.MarkersHolder> {

    private List<Marker> mMarkerList;
    private Context context;

    public MarkersAdapter(Context context, List<Marker> mMarkerList){
        this.context = context;
        this.mMarkerList = mMarkerList;
    }


    @NonNull
    @Override
    public MarkersAdapter.MarkersHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.marker_item, null);
        MarkersHolder viewHolder = new MarkersHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MarkersAdapter.MarkersHolder markersHandler, int i) {
        Marker markerInstance = mMarkerList.get(i);
        String category = markerInstance.getCategory();
        // ubaciti odgovarajuce boje
        switch (category){
            case ("travel"):
                markersHandler.tagHolder.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case ("nature"):
                markersHandler.tagHolder.setBackgroundColor(context.getResources().getColor(R.color.mainTextColor));
                break;
        }
        markersHandler.category_tag.setText(category);
        Glide.with(context).load(markerInstance.getImageURL()).into(markersHandler.markerImage);
        markersHandler.markerDescription.setText(markerInstance.getDescription());
        markersHandler.markerName.setText(markerInstance.getName());

    }

    @Override
    public int getItemCount() {
        if (mMarkerList != null)
            return mMarkerList.size();
        else
        return 0;
    }

    public class MarkersHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView category_tag, markerName, markerDescription;
        public ImageView markerImage;
        public LinearLayout tagHolder;
        public MarkersHolder(View view) {
            super(view);
            this.category_tag = view.findViewById(R.id.category_tag);
            this.markerName = view.findViewById(R.id.markerName);
            this.markerDescription = view.findViewById(R.id.markerDescription);
            this.markerImage = view.findViewById(R.id.markerImage);
            this.tagHolder = view.findViewById(R.id.tagHolder);
        }
    }

    public List<Marker> getmMarkerList() {
        return mMarkerList;
    }

    public void setmMarkerList(List<Marker> mMarkerList) {
        this.mMarkerList = mMarkerList;

    }
}
