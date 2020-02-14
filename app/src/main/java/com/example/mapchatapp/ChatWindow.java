package com.example.mapchatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ChatWindow extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        Intent chatIntent = getIntent();
        String username = chatIntent.getStringExtra("username");

        TextView userNameTextView = findViewById(R.id.userNameTextView);
        userNameTextView.setText(username);
    }
}
