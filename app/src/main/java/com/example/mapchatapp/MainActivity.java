package com.example.mapchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements userListAdapter.onItemClick, userList.onGetUserList {
    LocationManager locationManager;
    LocationListener locationListener;
    Location myLocation;
    Timer timerRef;

    private FusedLocationProviderClient fusedLocationClient;

    int minTime = 0, minDis = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Location Service
        locationManager = getSystemService(LocationManager.class);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLocation = location;
                //Update my location to the server
                UpdateMyLocation();
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
            //getLastKnownLocation
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                myLocation = location;
                                if (timerRef == null) {
                                    Timer timer = new Timer();
                                    timerRef = timer;
                                    timer.schedule(new UpdateUserList(), 0, 30000);
                                }
                                generateUserListFragment(myLocation);
                            }
                        }
                    });
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDis, locationListener);
        }
    }

    class UpdateUserList extends TimerTask {
        public void run() {
            generateUserListFragment(myLocation);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //getLastKnownLocation
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                myLocation = location;
                                Timer timer = new Timer();
                                //Start thread to getUserList every 30 seconds
                                timer.schedule(new UpdateUserList(), 0, 30000);
                                generateUserListFragment(myLocation);
                            }
                        }
                    });
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDis, locationListener);
        }
    }

    private void generateUserListFragment(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
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
        Intent chatIntent = new Intent(this, ChatWindow.class);
        chatIntent.putExtra("username",username);
        startActivity(chatIntent);
    }

    @Override
    public void getUserLocation(ArrayList<user> userList) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("userMapFragment");
        if (fragment == null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.userMapContainer, userMap.newInstance(userList),"userMapFragment")
                    .commit();
        } else {
            //tell Map fragment to update
            ((userMap)getSupportFragmentManager().findFragmentByTag("userMapFragment")).updateMap(userList);
        }
        ((TextView)findViewById(R.id.titleTextView)).setText("Friends");
    }

    public void UpdateMyLocation(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://kamorris.com/lab/register_location.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("user", "viet");
                params.put("latitude", String.valueOf(myLocation.getLatitude()));
                params.put("longitude", String.valueOf(myLocation.getLongitude()));
                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
        timerRef.cancel();
        timerRef.purge();
    }
}
