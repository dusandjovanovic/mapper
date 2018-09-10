package com.dushan.dev.mapper.Data;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LocationData {
    private static Location location;
    private static String FIREBASE_LATITUDE = "latitude";
    private static String FIREBASE_LONGITUDE = "longitude";
    private static DatabaseReference database;

    private static LocationData instance = null;
    private SocialData socialData;

    private static LocationUpdatedEventListener updateListener;

    private Context context;

    private LocationData(String userID, Context context) {
        location = new Location();
        database = FirebaseDatabase.getInstance().getReference("users/" + userID);
        database.child(FIREBASE_LATITUDE).addValueEventListener(latitudeEventListener);
        database.child(FIREBASE_LONGITUDE).addValueEventListener(longitudeEventListener);
        socialData = SocialData.getInstance(userID, context);
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

            if(updateListener != null)
                updateListener.onLocationUpdated();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

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

    public void reinitiateSingleton() {
        instance = null;
    }
}
