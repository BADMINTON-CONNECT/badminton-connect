package com.example.badmintonconnect;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.location.LocationManager.GPS_PROVIDER;

public class PlayersLocationActivity extends Activity implements LocationListener {
    private LocationManager locationManager;
    private RequestQueue queue;
    private final int REQUEST_PERMISSION_LOCATION=1;
    private Map<String, Double> locationDetails;
    private Intent findPlayersIntent;
    final static String TAG = "Player Location Activity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playerlocation);
        locationDetails = new HashMap<>();
        queue = Volley.newRequestQueue(this);
        findPlayersIntent = new Intent(PlayersLocationActivity.this, PlayersActivity.class);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showExplanation("Allow location access?", "In order to use the find players"  +
                            "functionality, we need your location. This is so we can find players that are closest to you!",
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSION_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(GPS_PROVIDER, 0, 0, this);

        Button findPlayersButton = (Button) findViewById(R.id.findPlayersButton);
        findPlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPlayerFields(UserInfo.getUserId());
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        locationDetails.put("longitude", location.getLongitude());
        locationDetails.put("latitude", location.getLatitude());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(PlayersLocationActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PlayersLocationActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void showExplanation(String title, String message, String[] permissions, final int permissionCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permissions, permissionCode);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Returning home");
                        Intent homePageIntent = new Intent(PlayersLocationActivity.this, HomePageActivity.class);
                        startActivity(homePageIntent);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String[] permissions, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                permissions, permissionRequestCode);
    }

    private void checkPlayerFields(String user_ID) {
        String URL = "http://40.88.38.140:8080/users/" + user_ID;
        Log.d(TAG, URL);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                Log.d(TAG, "successfully received user information");
                Log.d(TAG, response.toString());
                // parse user info
                if (user_ID == null) {
                    Log.d(TAG, "ERROR - userID null");
                }
                try {
                    if (response.isNull("first_name") || response.isNull("last_name") ||
                            response.getInt("skill_level") == 0 || response.getInt("distance_preference") == 0) {
                        goToProfilePage();
                    }
                    else{
                        checkPlayerAvailability(user_ID);
                    }
                } catch (JSONException e) {
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

        queue.add(jsonObjectRequest);
    }

    private void checkPlayerAvailability(String user_ID){
        String URL = "http://40.88.38.140:8080/availability/" + user_ID;
        Log.d(TAG, URL);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                Log.d(TAG, "successfully received availability information");
                Log.d(TAG, response.toString());
                // parse user info
                if (user_ID == null) {
                    Log.d(TAG, "ERROR - userID null");
                }
                if (response.length() == 0){
                    goToProfilePage();
                }
                else{
                    try {
                        JSONObject obj = (JSONObject) response.get(0);
                        if (obj.getInt("day") == -1){
                            goToProfilePage();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    findPlayersIntent.putExtra("longitude", locationDetails.get("longitude"));
                    findPlayersIntent.putExtra("latitude", locationDetails.get("latitude"));
                    startActivity(findPlayersIntent);
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

        queue.add(jsonArrayRequest);
    }

    private void goToProfilePage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Profile Fields not set")
                .setMessage("Please go back to the profile page to set your" +
                        "availability, skill level, and distance preference in order to" +
                        " use this functionality")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "Going to profile page");
                        Intent profilePageIntent = new Intent(PlayersLocationActivity.this, ProfileActivity.class);
                        startActivity(profilePageIntent);
                    }
                });
        builder.create().show();
    }
}
