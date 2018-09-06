package com.dushan.dev.mapper.Data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dushan.dev.mapper.Handlers.NotificationHandler;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SocialData {
    private static Social social;
    private static ArrayList<User> socialFriends;
    private static ArrayList<User> socialRequests;
    private static ArrayList<Marker> socialMarkers;

    private static String FIREBASE_ROOT = "users";
    private static String FIREBASE_CHILD_REQUESTS = "requests";
    private static String FIREBASE_CHILD_FRIENDS = "friends";
    private static String FIREBASE_CHILD_MARKERS = "markers";
    private static DatabaseReference database;
    private static DatabaseReference root;

    public static SocialData instance = null;

    private static FriendsUpdatedEventListener friendsUpdatedListener;
    private static RequestsUpdatedEventListener requestsUpdatedListener;
    private static MakersUpdatedEventListener markersUpdatedListener;

    private NotificationHandler notificationHandler;
    private Context context;

    private SocialData(String userID, Context context) {
        social = new Social();
        root = FirebaseDatabase.getInstance().getReference(FIREBASE_ROOT);
        database = FirebaseDatabase.getInstance().getReference(FIREBASE_ROOT + "/" + userID);
        database.child(FIREBASE_CHILD_REQUESTS).addChildEventListener(requestsEventListener);
        database.child(FIREBASE_CHILD_REQUESTS).addListenerForSingleValueEvent(requestsValueEventListener);
        database.child(FIREBASE_CHILD_FRIENDS).addChildEventListener(friendsEventListener);
        database.child(FIREBASE_CHILD_FRIENDS).addListenerForSingleValueEvent(friendsValueEventListener);

        this.context = context;
        notificationHandler = NotificationHandler.getInstance(this.context);
    }

    public void setFriendsListener(FriendsUpdatedEventListener listener) {
        friendsUpdatedListener = listener;
    }

    public interface FriendsUpdatedEventListener {
        void onFriendsUpdated();
    }

    public void setRequestsListener(RequestsUpdatedEventListener listener) {
        requestsUpdatedListener = listener;
    }

    public interface RequestsUpdatedEventListener {
        void onRequestsUpdated();
    }

    public void setMarkersListener(MakersUpdatedEventListener listener) {
        markersUpdatedListener = listener;
    }

    public interface MakersUpdatedEventListener {
        void onMarkersUpdated();
    }

    ValueEventListener friendsValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(friendsUpdatedListener != null)
                friendsUpdatedListener.onFriendsUpdated();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ChildEventListener friendsEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String delta = dataSnapshot.getValue(String.class);
            addUserFriend(delta);
            if(friendsUpdatedListener != null)
                friendsUpdatedListener.onFriendsUpdated();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

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
            if(requestsUpdatedListener != null)
                requestsUpdatedListener.onRequestsUpdated();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ChildEventListener requestsEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String delta = dataSnapshot.getValue(String.class);
            //notificationHandler.createSimpleNotification(context, "You have a new friend request.");
            addUserRequest(delta);
            if(requestsUpdatedListener != null)
                requestsUpdatedListener.onRequestsUpdated();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String delta = dataSnapshot.getValue(String.class);
            removeUserRequest(delta);
            if(requestsUpdatedListener != null)
                requestsUpdatedListener.onRequestsUpdated();
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

    private void addUserFriend(String userId) {
        social.addFriend(userId);
        Query connectedUser = root.equalTo(userId);
        connectedUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userSnapshot = dataSnapshot.getValue(User.class);
                userSnapshot.setKey(userId);

                socialFriends.add(userSnapshot);
                addMarkersForUser(userId);

                if(friendsUpdatedListener != null)
                    friendsUpdatedListener.onFriendsUpdated();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        connectedUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userSnapshot = dataSnapshot.getValue(User.class);
                userSnapshot.setKey(userId);

                changeUserFriend(userSnapshot);

                if(friendsUpdatedListener != null)
                    friendsUpdatedListener.onFriendsUpdated();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void changeUserFriend(User userSnapshot) {
        for (int i = 0; i < socialFriends.size(); i++)
            if (socialFriends.get(i).getKey() == userSnapshot.getKey())
                socialFriends.set(i, userSnapshot);
    }

    private void addMarkersForUser(String userId) {
        root.child(userId).child(FIREBASE_CHILD_MARKERS)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String myPlaceKey = dataSnapshot.getKey();
                        Marker marker = dataSnapshot.getValue(Marker.class);
                        marker.setKey(myPlaceKey);
                        socialMarkers.add(marker);

                        if(markersUpdatedListener != null)
                            markersUpdatedListener.onMarkersUpdated();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void addUserRequest(String userId) {
        social.addRequest(userId);
        root.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userSnapshot = dataSnapshot.getValue(User.class);
                userSnapshot.setKey(userId);

                socialRequests.add(userSnapshot);

                if(friendsUpdatedListener != null)
                    friendsUpdatedListener.onFriendsUpdated();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void removeUserRequest(String userId) {
        social.removeRequest(userId);
    }

    public static SocialData getInstance(String userId, Context context) {
        if (instance == null)
            instance = new SocialData(userId, context);
        return instance;
    }

    public Social getSocialData() {
        return social;
    }

    public static ArrayList<User> getSocialFriends() {
        return socialFriends;
    }

    public static void setSocialFriends(ArrayList<User> socialFriends) {
        SocialData.socialFriends = socialFriends;
    }

    public static ArrayList<User> getSocialRequests() {
        return socialRequests;
    }

    public static void setSocialRequests(ArrayList<User> socialRequests) {
        SocialData.socialRequests = socialRequests;
    }

    public static ArrayList<Marker> getSocialMarkers() {
        return socialMarkers;
    }

    public static void setSocialMarkers(ArrayList<Marker> socialMarkers) {
        SocialData.socialMarkers = socialMarkers;
    }
}
