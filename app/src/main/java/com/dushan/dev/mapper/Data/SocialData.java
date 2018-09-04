package com.dushan.dev.mapper.Data;

import android.content.Context;

import com.dushan.dev.mapper.Handlers.NotificationHandler;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SocialData {
    private static Social social;
    private static String FIREBASE_CHILD_REQUESTS = "requests";
    private static String FIREBASE_CHILD_FRIENDS = "friends";
    private static DatabaseReference database;

    public static SocialData instance = null;
    private static ListUpdatedEventListener updateListener;

    private NotificationHandler notificationHandler;
    private Context context;

    private SocialData(String userID, Context context) {
        social = new Social();
        database = FirebaseDatabase.getInstance().getReference("users/" + userID);
        database.child(FIREBASE_CHILD_REQUESTS).addChildEventListener(requestsEventListener);
        database.child(FIREBASE_CHILD_REQUESTS).addListenerForSingleValueEvent(requestsValueEventListener);
        database.child(FIREBASE_CHILD_FRIENDS).addChildEventListener(friendsEventListener);
        database.child(FIREBASE_CHILD_FRIENDS).addListenerForSingleValueEvent(friendsValueEventListener);

        this.context = context;
        notificationHandler = NotificationHandler.getInstance(this.context);
    }

    public void setEventListener(ListUpdatedEventListener listener) {
        updateListener = listener;
    }

    public interface ListUpdatedEventListener {
        void onSocialUpdated();
    }

    ValueEventListener friendsValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(updateListener != null) {
                updateListener.onSocialUpdated();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ChildEventListener friendsEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String delta = dataSnapshot.getValue(String.class);
            social.addFriend(delta);
            if(updateListener != null)
                updateListener.onSocialUpdated();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String delta = dataSnapshot.getValue(String.class);
            social.addFriend(delta);
            if(updateListener != null)
                updateListener.onSocialUpdated();
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String delta = dataSnapshot.getValue(String.class);
            social.removeFriend(delta);
            if(updateListener != null)
                updateListener.onSocialUpdated();
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

    ValueEventListener requestsValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(updateListener != null) {
                updateListener.onSocialUpdated();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ChildEventListener requestsEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String delta = dataSnapshot.getValue(String.class);
            notificationHandler.createSimpleNotification(context, "You have a new friend request from " + delta);
            social.addRequest(delta);
            if(updateListener != null)
                updateListener.onSocialUpdated();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String delta = dataSnapshot.getValue(String.class);
            social.addRequest(delta);
            if(updateListener != null)
                updateListener.onSocialUpdated();
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String delta = dataSnapshot.getValue(String.class);
            social.removeRequest(delta);
            if(updateListener != null)
                updateListener.onSocialUpdated();
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

    public static SocialData getInstance(String userId, Context context) {
        if (instance == null)
            instance = new SocialData(userId, context);
        return instance;
    }

    public Social getSocialData() {
        return social;
    }
}
