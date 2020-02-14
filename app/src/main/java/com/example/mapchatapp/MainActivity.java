package com.example.mapchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements userListAdapter.onItemClick, userList.onGetUserList {
    LocationManager locationManager;
    LocationListener locationListener;
    Location myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get Location Service
        locationManager = getSystemService(LocationManager.class);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("here","Location changed");
                myLocation = location;
                generateUserListFragment(myLocation);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //Check permission
        if (checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 111);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
        }
    }

    private void generateUserListFragment(Location location) {
        Log.d("here","heree");
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        //Mark my location on Google Map
//        if (mapView != null) {
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
//            if (map != null) {
//                map.animateCamera(cameraUpdate);
//                if (marker == null) {
//                    map.addMarker(new MarkerOptions().position(latLng)
//                            .title("Your current location"));
//                } else {
//                    marker.setPosition(latLng);
//                }
//            }
//        }
        userList fragment = (userList) getSupportFragmentManager().findFragmentByTag("userListFragment");
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.userListContainer, userList.newInstance(latLng.latitude, latLng.longitude), "userListFragment")
                .commit();
    }

    @Override
    public void onClick(String username) {
        //map focus on username
        ArrayList<user> userList = ((userList)getSupportFragmentManager().findFragmentByTag("userListFragment")).userList;
        for (user u : userList){
            if (u.getName() == username){
                ((userMap)getSupportFragmentManager().findFragmentByTag("userMapFragment")).focusOn(u);
                break;
            }
        }
    }

    @Override
    public void getUserLocation(ArrayList<user> userList) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("userMapFragment");
        if (fragment == null){
            Log.d("here","Map Fragment is null");
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.userMapContainer, userMap.newInstance(userList),"userMapFragment")
                    .commit();
        } else {
            //tell Map fragment to update
            Log.d("here","Map Fragment is not null");
            ((userMap)getSupportFragmentManager().findFragmentByTag("userMapFragment")).updateMap(userList);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }
}
