package com.dushan.dev.mapper.Data;

import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class User {
    private String name;
    private String lastName;
    private String about;
    private String phoneNumber;
    private String email;
    private String image;

    private ArrayList<String> friends;
    private ArrayList<String> requests;

    @Exclude
    public String key;

    public User() {}

    public User(String email, String name, String lastName, String about, String phoneNumber, Uri image
            , ArrayList<String> friends, ArrayList<String> requests) {
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.about = about;
        this.image = image.toString();
        this.friends = friends;
        this.requests = requests;
    }

    public User(String email, String name, String lastName, String about, String phoneNumber, Uri image) {
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.about = about;
        this.image = image.toString();
        this.friends = new ArrayList<String>();
        this.requests = new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNuber) {
        this.phoneNumber = phoneNuber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public void addRequest(String request) {
        this.requests.add(request);
    }

    public void removeRequest(String request) {
        this.requests.remove(request);
    }
}
