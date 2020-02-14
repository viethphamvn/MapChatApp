package com.example.mapchatapp;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class userMap extends Fragment implements OnMapReadyCallback {
    private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";
    //Map Stuff
    MapView mapView;
    GoogleMap map;

    // TODO: Rename and change types of parameters
    private ArrayList<user> userList;

    public userMap() {
        // Required empty public constructor
    }

    public static userMap newInstance(ArrayList<user> userList) {
        userMap fragment = new userMap();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, userList);
        //args.putStringArrayList(ARG_PARAM2, userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userList = getArguments().getParcelableArrayList(ARG_PARAM1);
            //userName = getArguments().getStringArrayList(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_map, container, false);
        //Set up MapView
        if (v.findViewById(R.id.mapView) != null) {
            mapView = v.findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }
        return v;
    }

    public void updateMap(ArrayList<user> userList){
        if (map != null) {
            map.clear();
            for (int i = 0; i < userList.size(); i++) {
                map.addMarker(new MarkerOptions().position(userList.get(i).getLatLng()).title(userList.get(i).getName()));
            }
        }
    }

    public void focusOn(user user){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(user.getLatLng(), 15);
        map.animateCamera(cameraUpdate);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mapView != null) {
            mapView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mapView != null) {
            mapView.onStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        for (int i = 0; i < userList.size(); i++){
            map.addMarker(new MarkerOptions().position(userList.get(i).getLatLng()).title(userList.get(i).getName()));
        }
    }

}
