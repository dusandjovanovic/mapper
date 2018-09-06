package com.dushan.dev.mapper.Data;

import android.net.Uri;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserData {
    private static User user;
    private static String FIREBASE_CHILD;
    private static DatabaseReference database;

    public static UserData instance = null;

    private static UserData.ListUpdatedEventListener updateListener;

    private UserData(String userID) {
        user = new User();
        FIREBASE_CHILD = userID;
        database = FirebaseDatabase.getInstance().getReference("users/");
        database.child(FIREBASE_CHILD).addValueEventListener(valueEventListener);
    }

    public void setEventListener(UserData.ListUpdatedEventListener listener) {
        updateListener = listener;
    }

    public interface ListUpdatedEventListener {
        void onUserUpdated();
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            User userSnapshot = dataSnapshot.getValue(User.class);
            user = userSnapshot;
            if(updateListener != null)
                updateListener.onUserUpdated();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public static UserData getInstance(String userId) {
        if (instance == null)
            instance = new UserData(userId);
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void registerUser(String email, String name, String lastName, String about, String phoneNumber, Uri image) {
        user = new User(email, name, lastName, about, phoneNumber, image);
        database.child(FIREBASE_CHILD).setValue(user);
    }
}
