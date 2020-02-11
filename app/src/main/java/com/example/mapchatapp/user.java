package com.example.mapchatapp;

public class user implements Comparable<user> {
    private String username;
    private float lat;
    private float lon;

    public user (String username){
        this.username = username;
    }

    public user (String username, float lat, float lon){
        this.username = username;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public int compareTo(user o) {
        return 0;
    }

    public String getName(){
        return username;
    }

    public user getUser(){
        return this;
    }
}
