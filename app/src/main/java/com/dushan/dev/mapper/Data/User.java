package com.dushan.dev.mapper.Data;

import android.net.Uri;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String name;
    private String lastName;
    private String about;
    private String phoneNumber;
    private String email;
    private String image;
    private long reachImpact;

    private double latitude;
    private double longitude;

    @Exclude
    public String key;

    public User() { }

    public User(String email, String name, String lastName, String about, String phoneNumber, Uri image) {
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.about = about;
        this.image = image.toString();
        this.reachImpact = 0;
        this.latitude = 43.3209022;
        this.longitude = 21.8957589;
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


    public long getReachImpact() {
        return reachImpact;
    }

    public void setReachImpact(long reachImpact) {
        this.reachImpact = reachImpact;
    }
}
