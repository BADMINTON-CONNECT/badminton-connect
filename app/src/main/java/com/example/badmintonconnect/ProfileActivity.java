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
    private TextView textViewUserName;
    private TextView textViewUserSkillLevel;
    private String TAG = "LoginActivity";
    private String user_ID;
    private Map<String, String> userInfo = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageViewProfilePicture = (ImageView) findViewById(R.id.imageViewProfilePicture);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserSkillLevel = (TextView) findViewById(R.id.textViewUserSkillLevel);

        // TODO - allow the user to configure their profile page
//        buttonSettings = (ImageButton) findViewById(R.id.imageButtonSettings);
//        buttonSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Clicked settings button");
//            }
//        });

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        LoginActivity loginActivity = new LoginActivity();
//        this.user_ID = loginActivity.user_ID;
//        getUserID(account);
        getUserInfoFromBackend(user_ID);
        updateUI(account);
    }

//    private void getUserID(GoogleSignInAccount account) {
//        try {
//            RequestQueue requestQueue = Volley.newRequestQueue(this);
//            String URL = "http://40.88.38.140:8080/users";
//            JSONObject userInfo = new JSONObject();
//            Log.d(TAG, account.getEmail());
//            userInfo.put("email", account.getEmail());
//            final String mRequestBody = userInfo.toString();
//
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    Log.d(TAG, "successfully stored new user");
//                    user_ID = response;
//                    Log.d(TAG, "THIS IS USERID" + response);
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    Log.d(TAG, "BRAINSSSSSSSSSSSSSSSSSSSS ERRRRRRRRRRRRRRRRRRRRRRRRR");
//                    Log.d(TAG, error.toString());
//                }
//            }) {
//                @Override
//                public String getBodyContentType() {
//                    return "application/json; charset=utf-8";
//                }
//
//                @Override
//                public byte[] getBody() throws AuthFailureError {
//                    try {
//                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
//                    } catch (UnsupportedEncodingException uee) {
//                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
//                        return null;
//                    }
//                }
//
//                @Override
//                protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                    String responseString = "";
//                    if (response != null) {
//                        //responseString = String.valueOf(response.statusCode);
//                        return super.parseNetworkResponse(response);
//                    }
//                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
//                }
//            };
//
//            requestQueue.add(stringRequest);
//
//        } catch (JSONException e) {
//            Log.d(TAG, "error");
//            e.printStackTrace();
//        }
//    }

    private void getUserInfoFromBackend(String user_ID) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            // TODO - replace "48" with userID when issue is fixed
            String URL = "http://40.88.38.140:8080/users/" + "48";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "successfully received user information");
                    // parse user info
                    try {
                        if(user_ID == null) {
                            Log.d(TAG, "ERROR - userID null");
                        }
                        JSONArray userArray = response.getJSONArray("");

                        userInfo.put("first_name", userArray.getJSONObject(0).getString("first_name"));
                        userInfo.put("last_name", userArray.getJSONObject(0).getString("last_name"));
                        userInfo.put("email", userArray.getJSONObject(0).getString("email"));
                        userInfo.put("skill_level", userArray.getJSONObject(0).getString("skill_level"));
                        Log.d(TAG, "username is: " + userInfo.get("firstname"));
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
//            textViewUserName.setText(userInfo.get("first_name") + " " + userInfo.get("last_name"));
//            textViewUserEmail.setText(userInfo.get("email"));
//            textViewUserSkillLevel.setText(userInfo.get("skill_level"));

            textViewUserEmail.setText(account.getEmail());
            textViewUserName.setText(account.getDisplayName());
            String personPhotoUrl = account.getPhotoUrl().toString();
            if(personPhotoUrl == null) {
                Log.d(TAG, "photoURL is null");
            }
            else {
                Glide.with(this).load(personPhotoUrl).into(imageViewProfilePicture);
            }
        }
        else {
            Log.d(TAG, "Failed to update profile UI");
        }
    }
}
