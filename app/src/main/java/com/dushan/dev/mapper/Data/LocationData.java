package com.dushan.dev.mapper.Data;

import android.app.ActivityManager;
import android.content.Context;

import com.dushan.dev.mapper.Handlers.NotificationHandler;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class LocationData {
    private static Location location;
    private static String FIREBASE_LATITUDE = "latitude";
    private static String FIREBASE_LONGITUDE = "longitude";
    private static DatabaseReference database;

    private static LocationData instance = null;
    private SocialData socialData;

    private static LocationUpdatedEventListener updateListener;
    private static boolean notificationPush = false;

    private NotificationHandler notificationHandler;
    private Context context;

    private LocationData(String userID, Context context) {
        location = new Location();
        database = FirebaseDatabase.getInstance().getReference("users/" + userID);
        database.child(FIREBASE_LATITUDE).addValueEventListener(latitudeEventListener);
        database.child(FIREBASE_LONGITUDE).addValueEventListener(longitudeEventListener);
        socialData = SocialData.getInstance(userID, context);
        notificationHandler = NotificationHandler.getInstance(context);
        this.context = context;
    }

    public void setEventListener(LocationUpdatedEventListener listener) {
        updateListener = listener;
    }

    public interface LocationUpdatedEventListener {
        void onLocationUpdated();
    }

    ValueEventListener latitudeEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            double locationSnapshot = 0.0;
            if (dataSnapshot.getValue() instanceof Long){
                locationSnapshot = 0.0;
            } else if (dataSnapshot.getValue() instanceof Double){
                locationSnapshot = (Double)dataSnapshot.getValue();
            }
            location.setLatitude(locationSnapshot);

            markersNearby();
            if (applicationBackground() && usersNearby())
                notificationHandler.createSimpleNotification(context, "There is someone nearby. Tap to open application.");
            if(updateListener != null)
                updateListener.onLocationUpdated();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ValueEventListener longitudeEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            double locationSnapshot = 0.0;
            if (dataSnapshot.getValue() instanceof Long){
                locationSnapshot = 0.0;
            } else if (dataSnapshot.getValue() instanceof Double){
                locationSnapshot = (Double)dataSnapshot.getValue();
            }
            location.setLongitude(locationSnapshot);

            markersNearby();
            if (applicationBackground() && usersNearby())
                notificationHandler.createSimpleNotification(context, "There is someone nearby. Tap to open application.");
            if(updateListener != null)
                updateListener.onLocationUpdated();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private boolean usersNearby() {
        for (User user: socialData.getSocialFriends()) {
            float[] distance = new float[3];
            android.location.Location.distanceBetween(location.getLatitude(), location.getLongitude(), user.getLatitude(), user.getLongitude(), distance);
            if (distance[0] <= 30)
                return true;
        }
        return false;
    }

    private void markersNearby() {
        for (Marker marker: socialData.getSocialMarkers()) {
            float[] distance = new float[3];
            android.location.Location.distanceBetween(location.getLatitude(), location.getLongitude(), marker.getLatitude(), marker.getLongitude(), distance);
            if (distance[0] <= 30)
                socialData.visitedMarkerEvent(marker);
        }
    }

    private boolean applicationBackground() {
        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);
        return !(appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
    }

    public void changeLocation(double latitude, double longitude) {
        database.child(FIREBASE_LATITUDE).setValue(latitude);
        database.child(FIREBASE_LONGITUDE).setValue(longitude);
    }

    public static LocationData getInstance(String userId, Context context) {
        if (instance == null)
            instance = new LocationData(userId, context);
        return instance;
    }

    public double getLatitude() {
        return location.getLatitude();
    }
    public double getLongitude() {
        return location.getLongitude();
    }

    public void reinitateSingleton() {
        instance = null;
    }
}
