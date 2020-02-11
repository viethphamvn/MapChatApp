package com.example.mapchatapp;

public class user implements Comparable<user> {
    private String username;
    private double lat;
    private double lon;
    private double distanceToMe;

    public user (String username){
        this.username = username;
    }

    public user (String username, double lat, double lon){
        this.username = username;
        this.lat = lat;
        this.lon = lon;
    }

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

    public double getDistanceToMe(){
        return distanceToMe;
    }
}
