package com.dushan.dev.mapper.Data;

import android.net.Uri;

import com.google.firebase.database.ChildEventListener;
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

    public static LocationData instance = null;

    private static LocationUpdatedEventListener updateListener;

    private LocationData(String userID) {
        location = new Location();
        database = FirebaseDatabase.getInstance().getReference("users/" + userID);
        database.child(FIREBASE_LATITUDE).addValueEventListener(latitudeEventListener);
        database.child(FIREBASE_LONGITUDE).addValueEventListener(longitudeEventListener);
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
            double locationSnapshot = (Double)dataSnapshot.getValue();
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
            double locationSnapshot = (Double)dataSnapshot.getValue();
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

    public static LocationData getInstance(String userId) {
        if (instance == null)
            instance = new LocationData(userId);
        return instance;
    }

    public double getLatitude() {
        return location.getLatitude();
    }
    public double getLongitude() {
        return location.getLongitude();
    }
}
