package com.example.mapchatapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class user implements Comparable<user>, Parcelable {
    private String username;
    private LatLng latLng;
    private double distanceToMe;

    public user (String username){
        this.username = username;
    }

    public user (String username, LatLng latLng){
        this.username = username;
        this.latLng = latLng;
    }

    protected user(Parcel in) {
        username = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        distanceToMe = in.readDouble();
    }

    public static final Creator<user> CREATOR = new Creator<user>() {
        @Override
        public user createFromParcel(Parcel in) {
            return new user(in);
        }

        @Override
        public user[] newArray(int size) {
            return new user[size];
        }
    };

    @Override
    public int compareTo(user o) {
        return this.distanceToMe < o.distanceToMe ? -1 : 1;
    }

    public void setDistanceToMe(double distance){
        distanceToMe = distance;
    }

    public String getName(){
        return username;
    }

    public user getUser(){
        return this;
    }

    public LatLng getLatLng(){
        return latLng;
    }

    public double getDistanceToMe(){
        return distanceToMe;
    }

    public double getLat(){
        return latLng.latitude;
    }

    public double getLon(){
        return latLng.longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeParcelable(latLng, flags);
        dest.writeDouble(distanceToMe);
    }
}
