package com.example.badmintonconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.View.OnClickListener;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class HomePageActivity extends AppCompatActivity{
    final static String TAG = "HomePageActivity";
    private Button signOutbutton;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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

        signOutbutton = findViewById(R.id.logoutButton);
        signOutbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleSignInClient.signOut().addOnCompleteListener(HomePageActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "Signed out succesful.");
                    }
                });
                Intent loginIntent = new Intent(HomePageActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}