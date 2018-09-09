package com.dushan.dev.mapper.Data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dushan.dev.mapper.Handlers.NotificationHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SocialData {
    private static Social social;
    private static ArrayList<User> socialFriends;
    private static ArrayList<User> socialRequests;
    private static ArrayList<Marker> socialMarkers;
    private static ArrayList<String> visitedMarkers;

    private static final String FIREBASE_ROOT = "users";
    private static final String FIREBASE_CHILD_REQUESTS = "requests";
    private static final String FIREBASE_CHILD_FRIENDS = "friends";
    private static final String FIREBASE_CHILD_MARKERS = "markers";
    private static final String FIREBASE_CHILD_VISITED = "markersVisited";
    private static DatabaseReference database;
    private static DatabaseReference root;

    public static SocialData instance = null;

    private static FriendsUpdatedEventListener friendsUpdatedListener;
    private static RequestsUpdatedEventListener requestsUpdatedListener;
    private static MarkersUpdatedEventListener markersUpdatedListener;
    private static VisitedUpdatedEventListener visitedListener;

    private NotificationHandler notificationHandler;
    private Context context;
    private static FirebaseAuth mAuth;
    private String userID;

    private SocialData(String userID, Context context) {
        social = new Social();
        root = FirebaseDatabase.getInstance().getReference(FIREBASE_ROOT);
        database = FirebaseDatabase.getInstance().getReference(FIREBASE_ROOT).child(userID);
        database.child(FIREBASE_CHILD_REQUESTS).addChildEventListener(requestsEventListener);
        database.child(FIREBASE_CHILD_REQUESTS).addListenerForSingleValueEvent(requestsValueEventListener);
        database.child(FIREBASE_CHILD_FRIENDS).addChildEventListener(friendsEventListener);
        database.child(FIREBASE_CHILD_FRIENDS).addListenerForSingleValueEvent(friendsValueEventListener);
        database.child(FIREBASE_CHILD_VISITED).addChildEventListener(visitedEventListener);
        database.child(FIREBASE_CHILD_VISITED).addListenerForSingleValueEvent(visitedValueEventListener);
        mAuth = FirebaseAuth.getInstance();

        socialRequests = new ArrayList<User>();
        socialFriends = new ArrayList<User>();
        socialMarkers = new ArrayList<Marker>();
        visitedMarkers = new ArrayList<String>();

        this.userID = userID;
        this.context = context;
        notificationHandler = NotificationHandler.getInstance(this.context);
    }

    public static SocialData getInstance(String userId, Context context) {
        if (instance == null)
            instance = new SocialData(userId, context);
        return instance;
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

    public void setMarkersListener(MarkersUpdatedEventListener listener) {
        markersUpdatedListener = listener;
    }

    public interface MarkersUpdatedEventListener {
        void onMarkersUpdated();
    }

    public void setVisitedListener(VisitedUpdatedEventListener listener) {
        visitedListener = listener;
    }

    public interface VisitedUpdatedEventListener {
        void onVisitedUpdated();
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
            String delta = dataSnapshot.getKey();
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
            String delta = dataSnapshot.getKey();
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
            String delta = dataSnapshot.getKey();
            addUserFriend(delta);
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
        root.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userSnapshot = dataSnapshot.getValue(User.class);
                userSnapshot.setKey(userId);

                if (!social.userPresentFriends(userId)) {
                    social.addFriend(userId);
                    socialFriends.add(userSnapshot);
                    addMarkersForUser(userId);
                }
                else {
                    changeUserFriend(userSnapshot);
                }

                if(friendsUpdatedListener != null)
                    friendsUpdatedListener.onFriendsUpdated();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    ValueEventListener visitedValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(visitedListener != null)
                visitedListener.onVisitedUpdated();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ChildEventListener visitedEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            String delta = dataSnapshot.getValue(String.class);
            if (!visitedMarkers.contains(delta))
                visitedMarkers.add(delta);

            if(visitedListener != null)
                visitedListener.onVisitedUpdated();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String delta = dataSnapshot.getValue(String.class);
            if (visitedMarkers.contains(delta))
                visitedMarkers.remove(delta);

            if(visitedListener != null)
                visitedListener.onVisitedUpdated();
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

    public boolean visitedMarkerEvent(Marker marker) {
        if (!visitedMarkers.contains(marker.getKey())) {
            visitedMarkers.add(marker.getKey());
            String visitedMarkerKey = database.push().getKey();
            root.child(userID)
                    .child(FIREBASE_CHILD_VISITED).child(visitedMarkerKey).setValue(marker.getKey());

            SavedMarkerData.getInstance(userID).impactMarkerReach(marker);
            return true;
        }
        return false;
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
        root.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userSnapshot = dataSnapshot.getValue(User.class);
                userSnapshot.setKey(userId);

                if (!social.userPresentRequests(userId)) {
                    social.addRequest(userId);
                    socialRequests.add(userSnapshot);
                }

                if(friendsUpdatedListener != null)
                    friendsUpdatedListener.onFriendsUpdated();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void acceptUserRequest(String userId) {
        social.removeRequest(userId);
        for (User user: socialRequests)
            if (user.getKey().equals(userId)) {
                socialRequests.remove(user);
                break;
            }

        root.child(mAuth.getCurrentUser().getUid())
                .child(FIREBASE_CHILD_REQUESTS).child(userId).removeValue();
        root.child(mAuth.getCurrentUser().getUid())
                .child(FIREBASE_CHILD_FRIENDS).child(userId).setValue(true);
        root.child(userId).child(FIREBASE_CHILD_FRIENDS)
                .child(mAuth.getCurrentUser().getUid()).setValue(true);
    }

    public void sendUserRequest(String userId) {
        root.child(userId).child(FIREBASE_CHILD_REQUESTS)
                .child(mAuth.getCurrentUser().getUid()).setValue(true);
    }

    public Social getSocialData() {
        return social;
    }

    public ArrayList<User> getSocialFriends() {
        return socialFriends;
    }

    public void setSocialFriends(ArrayList<User> socialFriends) {
        SocialData.socialFriends = socialFriends;
    }

    public ArrayList<User> getSocialRequests() {
        return socialRequests;
    }

    public void setSocialRequests(ArrayList<User> socialRequests) {
        SocialData.socialRequests = socialRequests;
    }

    public ArrayList<Marker> getSocialMarkers() {
        return socialMarkers;
    }

    public void setSocialMarkers(ArrayList<Marker> socialMarkers) {
        SocialData.socialMarkers = socialMarkers;
    }

    public static ArrayList<String> getVisitedMarkers() {
        return visitedMarkers;
    }

    public static void setVisitedMarkers(ArrayList<String> visitedMarkers) {
        SocialData.visitedMarkers = visitedMarkers;
    }

    public void reinitateSingleton() {
        instance = null;
    }
}
