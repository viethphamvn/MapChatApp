package com.example.mapchatapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class userListAdapter extends RecyclerView.Adapter<userListAdapter.MyViewHolder> {
    private ArrayList<user> userList = new ArrayList<>();

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView userName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.textView);
        }
    }

    public userListAdapter(ArrayList<user> userList){
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_recycler_view_row_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.userName.setText(userList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    user getItem(int position){
        return userList.get(position).getUser();
    }
}
