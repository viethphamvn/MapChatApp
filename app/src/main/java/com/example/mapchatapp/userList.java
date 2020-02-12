package com.example.mapchatapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class userList extends Fragment {
    private static final String ARG_PARAM1 = "myLatitude";
    private static final String ARG_PARAM2 = "myLongitude";

    //RecyclerView Stuff
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private userListAdapter adapter;
    //API stuff
    private String url = "https://kamorris.com/lab/get_locations.php";
    ArrayList<user> userList = new ArrayList<>();
    //
    private Double myLat, myLon;
    user mySelf;
    DistanceCalculator calculator = new DistanceCalculator();
    // TODO: Rename and change types and number of parameters
    public static userList newInstance(Double lat, Double lon) {
        userList fragment = new userList();
        Bundle args = new Bundle();
        args.putDouble(ARG_PARAM1, lat);
        args.putDouble(ARG_PARAM2, lon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            myLat = getArguments().getDouble(ARG_PARAM1);
            myLon = getArguments().getDouble(ARG_PARAM2);
            mySelf = new user("myself",Double.valueOf(myLat),Double.valueOf(myLon));
            mySelf.setDistanceToMe(0);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_user_list, container, false);
        //Create userList
        RequestQueue queue = Volley.newRequestQueue(getContext());
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONArray userListFromGet = new JSONArray(response);
                            userList.clear();
                            //Add myself object in index 0
                            userList.add(mySelf);
                            for (int i = 1; i <= userListFromGet.length(); i++){
                                //Instantiate userList[]
                                JSONObject e = userListFromGet.getJSONObject(i-1);
                                user person = new user(e.getString("username"), Double.valueOf(e.getString("latitude")), Double.valueOf(e.getString("longitude")));
                                person.setDistanceToMe(calculator.distance(Double.valueOf(e.getString("latitude")), myLat, Double.valueOf(e.getString("longitude")), myLon));
                                Log.d("here",person.getName() + " " + person.getDistanceToMe());
                                userList.add(person);
                            }
                            Collections.sort(userList);
                            userList.remove(0);
                            //Instantiate RecyclerView w userList
                            recyclerView = getView().findViewById(R.id.recyclerViewForUserList);
                            recyclerView.setHasFixedSize(true);
                            layoutManager = new LinearLayoutManager(getContext());
                            recyclerView.setLayoutManager(layoutManager);
                            //setAdapter
                            adapter = new userListAdapter(userList);
                            adapter.setOnItemClickListener((userListAdapter.onItemClick) getActivity());
                            recyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        return v;
    }

    public interface onItemClick{
        void onClick(String username);
    }
}
