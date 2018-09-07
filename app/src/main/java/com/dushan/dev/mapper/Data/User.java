package com.dushan.dev.mapper.Data;

import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@IgnoreExtraProperties
public class User {
    private String name;
    private String lastName;
    private String about;
    private String phoneNumber;
    private String email;
    private String image;
    private ArrayList<Long> reach;

    private double latitude;
    private double longitude;

    @Exclude
    public String key;

    public User() {}

    public User(String email, String name, String lastName, String about, String phoneNumber, Uri image) {
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.about = about;
        this.image = image.toString();
        this.reach = new ArrayList<Long>();
        latitude = 0.0;
        longitude = 0.0;
    }

    public void impactReach() {
        this.reach.add(System.currentTimeMillis());
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


    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public ArrayList<Long> getReach() { return reach; }

    public void setReach(ArrayList<Long> reach) { this.reach = reach; }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
