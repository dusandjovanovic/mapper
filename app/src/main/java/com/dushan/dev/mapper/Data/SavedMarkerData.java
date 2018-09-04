package com.dushan.dev.mapper.Data;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;

public class SavedMarkerData {
    private static ArrayList<Marker> markers;
    private static HashMap<String, Integer> myPlacesIndexMapping;
    private static String FIREBASE_CHILD = "saved";
    private static DatabaseReference database;

    public static SavedMarkerData instance = null;

    private static ListUpdatedEventListener updateListener;

    private SavedMarkerData(String userID) {
        markers = new ArrayList<Marker>();
        myPlacesIndexMapping = new HashMap<String, Integer>();
        database = FirebaseDatabase.getInstance().getReference("users/" + userID);
        database.child(FIREBASE_CHILD).addChildEventListener(childEventListener);
        database.child(FIREBASE_CHILD).addListenerForSingleValueEvent(valueEventListener);
    }

    public void setEventListener(ListUpdatedEventListener listener) {
        updateListener = listener;
    }

    public interface ListUpdatedEventListener {
        void onListUpdated();
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(updateListener != null) {
                updateListener.onListUpdated();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String myPlaceKey = dataSnapshot.getKey();
            if(!myPlacesIndexMapping.containsKey(myPlaceKey)) {
                Marker marker = dataSnapshot.getValue(Marker.class);
                marker.key = myPlaceKey;
                markers.add(marker);
                myPlacesIndexMapping.put(myPlaceKey, markers.size() - 1);

                if(updateListener != null)
                    updateListener.onListUpdated();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String myPlaceKey = dataSnapshot.getKey();
            Marker marker = dataSnapshot.getValue(Marker.class);
            marker.key = myPlaceKey;
            if(myPlacesIndexMapping.containsKey(myPlaceKey)){
                int index = myPlacesIndexMapping.get(myPlaceKey);
                markers.set(index, marker);
            }
            else {
                markers.add(marker);
                myPlacesIndexMapping.put(myPlaceKey, markers.size() - 1);
            }

            if(updateListener != null)
                updateListener.onListUpdated();
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String myPlaceKey = dataSnapshot.getKey();
            if(myPlacesIndexMapping.containsKey(myPlaceKey)) {
                int index = myPlacesIndexMapping.get(myPlaceKey);
                markers.remove(index);
                recreateIndexMapping();
            }

            if(updateListener != null)
                updateListener.onListUpdated();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            return;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            return;
        }
    };


    public static SavedMarkerData getInstance(String userId) {
        if (instance == null)
            instance = new SavedMarkerData(userId);
        return instance;
    }

    public ArrayList<Marker> getMarkers() {
        return markers;
    }

    public void addNewMarker(Marker place) {
        String key = database.push().getKey();
        markers.add(place);
        myPlacesIndexMapping.put(key, markers.size() - 1);
        database.child(FIREBASE_CHILD).child(key).setValue(place);
        place.key = key;
    }

    public Marker getPlace(int index) {
        return markers.get(index);
    }

    public void deletePlace(int index) {
        database.child(FIREBASE_CHILD).child(markers.get(index).key).removeValue();
        Marker place = markers.remove(index);
        recreateIndexMapping();
    }

    private void recreateIndexMapping() {
        myPlacesIndexMapping.clear();

        for(int i = 0; i < markers.size(); i++)
            myPlacesIndexMapping.put(markers.get(i).key, i);
    }
}
