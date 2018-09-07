package com.dushan.dev.mapper.Data;

import java.util.ArrayList;

public class Social {
    private ArrayList<String> friends;
    private ArrayList<String> requests;

    public Social() {
        friends = new ArrayList<String>();
        requests = new ArrayList<String>();
    }

    public Social(ArrayList<String> friends, ArrayList<String> requests) {
        this.friends = friends;
        this.requests = requests;
    }

    public ArrayList<String> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public ArrayList<String> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<String> requests) {
        this.requests = requests;
    }

    public void addFriend(String friend) {
        this.friends.add(friend);
    }

    public void removeFriend(String friend) {
        this.friends.remove(friend);
    }

    public void addRequest(String request) {
        this.requests.add(request);
    }

    public void removeRequest(String request) {
        this.requests.remove(request);
    }

    public boolean userPresentFriends(String userId) {
        for (String key : friends) {
            if (key == userId)
                return true;
        }
        return false;
    }

    public boolean userPresentRequests(String userId) {
        for (String key : requests) {
            if (key == userId)
                return true;
        }
        return false;
    }
}
