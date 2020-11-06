package com.example.badmintonconnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.view.View.OnClickListener;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomePageActivity extends AppCompatActivity{
    final private static String TAG = "HomePageActivity";
    private GoogleSignInClient mGoogleSignInClient;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        queue = Volley.newRequestQueue(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //get user Id based off of google account
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        fetchUserId(account);

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

        ImageButton imageButtonSeeBooking = (ImageButton) findViewById(R.id.imageButtonSeeBooking);
        imageButtonSeeBooking.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Trying to open See Bookings Page via imageButton");

                Intent SeeBookingsActivity = new Intent(HomePageActivity.this, SeeBookingsActivity.class);
                startActivity(SeeBookingsActivity);
            }
        });

        ImageButton imageButtonPlayers = (ImageButton) findViewById(R.id.imageButtonPlayers);
        imageButtonPlayers.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Trying to open Players via imageButton");

                Intent playersIntent = new Intent(HomePageActivity.this, PlayersLocationActivity.class);
                startActivity(playersIntent);
            }
        });

        ImageButton signOutButton = (ImageButton) findViewById(R.id.logoutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
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

    private void sendUserToken(String token, String userId){
        Log.d(TAG, "sending user token");
        Log.d(TAG, token);
        Log.d(TAG, "this is user ID: " + userId);
        String url = "http://40.88.148.58:8080/users/RegistrationToken/" + userId;

        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("Registration_Token",token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Enter the correct url for your api service site
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("JSON", String.valueOf(response));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });
        queue.add(jsonObjectRequest);
    }

    private void fetchUserId(GoogleSignInAccount account) {

        Log.d(TAG, account.getEmail());

        String URL = "http://40.88.148.58:8080/users/email?email=" + account.getEmail();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "successfully retrieved userID");
                try {
                    JSONObject obj = (JSONObject) response.get(0);
                    Log.d(TAG, obj.get("user_id").toString());
                    UserInfoHelper.setUserId(obj.get("user_id").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //get firebase id
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                    return;
                                }

                                // Get new FCM registration token
                                String token = task.getResult();

                                // Log and toast
                                Log.d(TAG, token);
                                sendUserToken(token, UserInfoHelper.getUserId());
                            }
                        });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };

        queue.add(jsonArrayRequest);


    }
}