package com.example.mapchatapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
    private static final String ARG_PARAM1 = "userList";

    //RecyclerView Stuff
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private userListAdapter adapter;
    //API stuff
    public ArrayList<user> userList = new ArrayList<>();
    //Interface stuff
    private onGetUserList fragmentParent;

    //-------------------------------------------------
    public static userList newInstance(ArrayList<user> userList) {
        userList fragment = new userList();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, userList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userList = getArguments().getParcelableArrayList(ARG_PARAM1);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_user_list, container, false);
        if (v != null) {
            recyclerView = v.findViewById(R.id.recyclerViewForUserList);
            //setAdapter and RecyclerView
            adapter = new userListAdapter(userList);
            adapter.setOnItemClickListener((userListAdapter.onItemClick) getActivity());
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);

            //Send userList to MainActivity to pass to MapFragment
            fragmentParent.getUserLocation(userList);
        }
        return v;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentParent = (onGetUserList) context;

    }

    public interface onGetUserList{
        void getUserLocation(ArrayList<user> userList);
    }
}
