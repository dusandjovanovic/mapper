package com.dushan.dev.mapper.Data;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class MergedData {
    public static MergedData instance = null;

    private static final String CATEGORY_ALL = "All";

    private static ArrayList<Marker> mergedMarkers;
    private static ArrayList<Marker> filteredMarkers;
    private static MergedUpdatedEventListener mergedUpdatedEventListener;
    private static MarkerData markerData;
    private static SocialData socialData;

    private MergedData(String userID, Context context) {
        mergedMarkers = new ArrayList<Marker>();
        filteredMarkers = new ArrayList<Marker>();
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

    public ArrayList<Marker> recentMarkers() {
        ArrayList<Marker> recentMarkers = new ArrayList<Marker>();
        long lastTwoDays = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) - 1;
        for (Marker marker: mergedMarkers)
            if (TimeUnit.MILLISECONDS.toDays(marker.getDateTime()) >= lastTwoDays)
                recentMarkers.add(marker);

        return recentMarkers;
    }

    public ArrayList<Marker> filteredMarkers(LatLng currentLocation, ArrayList<String> filterKeywords, String filterCategory, int filterDiameter) {
        filteredMarkers = new ArrayList<Marker>();
        for (Marker marker: mergedMarkers)
            if (filterMarker(marker, currentLocation, filterKeywords, filterCategory, filterDiameter))
                filteredMarkers.add(marker);

        return filteredMarkers;
    }

    private boolean filterMarker(Marker marker, LatLng currentLocation, ArrayList<String> filterKeywords, String filterCategory, int filterDiameter) {
        Location markerLocation = new Location();
        markerLocation.setLatitude(marker.getLatitude());
        markerLocation.setLongitude(marker.getLongitude());

        if (distanceBetween(marker.getLatitude(), marker.getLongitude(), currentLocation.latitude, currentLocation.longitude)
                > (double)filterDiameter)
            return false;

        if (!filterCategory.equals(CATEGORY_ALL) && !filterCategory.equals(marker.getCategory()))
            return false;

        if (filterKeywords.size() == 0)
            return true;

        for (String substring: filterKeywords)
            if (marker.getDescription().contains(substring) || marker.getName().contains(substring))
                return true;

        return false;
    }

    private double distanceBetween(double lat_a, double lng_a, double lat_b, double lng_b ) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        double meterConversion = 1609;
        return distance * meterConversion;
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

    public static ArrayList<Marker> getFilteredMarkers() {
        return filteredMarkers;
    }

    public static void setFilteredMarkers(ArrayList<Marker> filteredMarkers) {
        MergedData.filteredMarkers = filteredMarkers;
    }

    public void reinitateSingleton() {
        instance = null;
    }
}
