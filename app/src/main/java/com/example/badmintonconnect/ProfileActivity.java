package com.example.badmintonconnect;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.*;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ImageView imageViewProfilePicture;
    private ImageButton buttonSettings;
    private TextView textViewUserEmail;
    private TextView editTextUserName;
    private TextView textViewUserSkillLevel;
    private String TAG = "LoginActivity";
    public static String user_ID;
    private Map<String, String> userInfo = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageViewProfilePicture = (ImageView) findViewById(R.id.imageViewProfilePicture);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        editTextUserName = (TextView) findViewById(R.id.editTextUserName);
        textViewUserSkillLevel = (TextView) findViewById(R.id.textViewUserSkillLevel);

        buttonSettings = (ImageButton) findViewById(R.id.imageButtonSettings);
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked settings button");
                Intent profileSettingsIntent = new Intent(ProfileActivity.this, ProfileSettingsActivity.class);
                startActivity(profileSettingsIntent);
            }
        });

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        getUserInfoFromBackend(UserInfo.getUserId());
        updateUI(account);
    }

    private void getUserInfoFromBackend(String user_ID) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://40.88.38.140:8080/users/" + user_ID;
            Log.d(TAG, URL);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "successfully received user information");
                    // parse user info
                    try {
                        if(user_ID == null) {
                            Log.d(TAG, "ERROR - userID null");
                        }
//                        userInfo.put("first_name", response.getString("first_name"));
//                        userInfo.put("last_name", response.getString("last_name"));
//                        userInfo.put("email", response.getString("email"));
//                        userInfo.put("skill_level", response.getString("skill_level"));
                        editTextUserName.setText(response.getString("first_name") + " " + response.getString("last_name"));
                        textViewUserEmail.setText(response.getString("email"));
                        textViewUserSkillLevel.setText(response.getString("skill_level"));
                    } catch (JSONException e) {
                        Log.d(TAG, "user JSON Object incorrectly loaded. Check stacktrace for more information");
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Unable to retrieve user information see log below for details: ");
                    Log.d(TAG, error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            requestQueue.add(jsonObjectRequest);
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null){
            editTextUserName.setText(userInfo.get("first_name") + " " + userInfo.get("last_name"));
            textViewUserEmail.setText(userInfo.get("email"));
            textViewUserSkillLevel.setText(userInfo.get("skill_level"));

//            textViewUserEmail.setText(account.getEmail());
//            textViewUserName.setText(account.getDisplayName());
            // use default image
            if(account.getPhotoUrl() == null) {
                imageViewProfilePicture.setImageResource(R.drawable.defaultprofilepicture);
            }
            else {
                String personPhotoUrl = account.getPhotoUrl().toString();
                Glide.with(this).load(personPhotoUrl).into(imageViewProfilePicture);
            }
        }
        else {
            Log.d(TAG, "Failed to update profile UI");
        }
    }
}
