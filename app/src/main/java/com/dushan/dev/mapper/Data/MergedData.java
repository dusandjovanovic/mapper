package com.dushan.dev.mapper.Data;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MergedData {
    public static MergedData instance = null;

    private static ArrayList<Marker> mergedMarkers;
    private static MergedUpdatedEventListener mergedUpdatedEventListener;
    private static MarkerData markerData;
    private static SocialData socialData;

    private MergedData(String userID, Context context) {
        mergedMarkers = new ArrayList<Marker>();
        markerData = MarkerData.getInstance(userID);
        socialData = SocialData.getInstance(userID, context);

        markerData.setEventListener(new MarkerData.ListUpdatedEventListener() {
            @Override
            public void onListUpdated() {
                rebuildMerged();
                if (mergedUpdatedEventListener != null)
                    mergedUpdatedEventListener.onUpdated();
            }
        });

        socialData.setMarkersListener(new SocialData.MarkersUpdatedEventListener() {
            @Override
            public void onMarkersUpdated() {
                rebuildMerged();
                if (mergedUpdatedEventListener != null)
                    mergedUpdatedEventListener.onUpdated();
            }
        });
    }

    public static MergedData getInstance(String userId, Context context) {
        if (instance == null)
            instance = new MergedData(userId, context);
        return instance;
    }

    private void rebuildMerged() {
        mergedMarkers = new ArrayList<Marker>();
        mergedMarkers.addAll(markerData.getMarkers());
        mergedMarkers.addAll(socialData.getSocialMarkers());
        Collections.sort(mergedMarkers, new Comparator<Marker>(){
            public int compare(Marker o1, Marker o2){
                if(o1.getDateTime() == o2.getDateTime())
                    return 0;
                return o1.getDateTime() > o2.getDateTime() ? -1 : 1;
            }
        });
    }

    public void setListener(MergedUpdatedEventListener listener) {
        mergedUpdatedEventListener = listener;
    }

    public interface MergedUpdatedEventListener {
        void onUpdated();
    }

    public ArrayList<Marker> getMarkers() {
        return mergedMarkers;
    }

    public void setMarkers(ArrayList<Marker> mergedMarkers) {
        MergedData.mergedMarkers = mergedMarkers;
    }
}
