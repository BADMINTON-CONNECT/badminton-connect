package com.example.badmintonconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.view.View.OnClickListener;
import android.util.Log;
import android.widget.ImageView;

public class HomePageActivity extends AppCompatActivity {
    final static String TAG = "HomePageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ImageButton imageButtonBooking = (ImageButton) findViewById(R.id.imageButtonBooking);
        imageButtonBooking.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Trying to open Booking via imageButton");

                Intent bookingIntent = new Intent(HomePageActivity.this, BookingActivity.class);
                startActivity(bookingIntent);
            }
        });

        ImageButton imageButtonProfile = (ImageButton) findViewById(R.id.imageButtonProfile);
        imageButtonProfile.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Trying to open Profile via imageButton");

                Intent profileIntent = new Intent(HomePageActivity.this, ProfileActivity.class);
                startActivity(profileIntent);
            }
        });

        ImageButton imageButtonPlayers = (ImageButton) findViewById(R.id.imageButtonPlayers);
        imageButtonPlayers.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Trying to open Players via imageButton");

                Intent playersIntent = new Intent(HomePageActivity.this, PlayersActivity.class);
                startActivity(playersIntent);
            }
        });

    }
}