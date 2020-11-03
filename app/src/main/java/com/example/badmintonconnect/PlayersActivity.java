package com.example.badmintonconnect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class PlayersActivity extends Activity {
    final static String TAG = "Player Activity";
    private RequestQueue queue;
    private TextView playerName1;
    private TextView playerName2;
    private TextView playerName3;
    private TextView playerSkill1;
    private TextView playerSkill2;
    private TextView playerSkill3;
    private TextView playerAvailability1;
    private TextView playerAvailability2;
    private TextView playerAvailability3;
    private TextView playerEmail1;
    private TextView playerEmail2;
    private TextView playerEmail3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);
        queue = Volley.newRequestQueue(this);

        playerName1 = (TextView) findViewById(R.id.player1_name);
        playerName2 = (TextView) findViewById(R.id.player2_name);
        playerName3 = (TextView) findViewById(R.id.player3_name);
        playerSkill1 = (TextView) findViewById(R.id.player1_skill);
        playerSkill2 = (TextView) findViewById(R.id.player2_skill);;
        playerSkill3 = (TextView) findViewById(R.id.player3_skill);;
        playerAvailability1 = (TextView) findViewById(R.id.player1_availability);
        playerAvailability2 = (TextView) findViewById(R.id.player2_availability);
        playerAvailability3 = (TextView) findViewById(R.id.player3_availability);
        playerEmail1 = (TextView) findViewById(R.id.player1_email);
        playerEmail2 = (TextView) findViewById(R.id.player2_email);
        playerEmail3 = (TextView) findViewById(R.id.player3_email);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            setPlayerLocation(UserInfo.getUserId(), extras.getDouble("longitude"), extras.getDouble("latitude"));
        }
        else{
            Toast.makeText(this, "An Error has Occurred", Toast.LENGTH_SHORT).show();
            Intent homePageIntent = new Intent(PlayersActivity.this, HomePageActivity.class);
            startActivity(homePageIntent);
        }

    }

    private void setPlayerLocation(String user_ID, Double x, Double y){
        String URL = "http://40.88.38.140:8080/users/location/" + user_ID;
        JSONObject userInfo = new JSONObject();
        // this is the json body that backend would use to get information
        try {
            Log.d(TAG, String.valueOf(x));
            userInfo.put("location_x", x);
            userInfo.put("location_y", y);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, URL, userInfo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Display the result (what is send from server using res.send)
                Log.d(TAG, response.toString());
                getPlayerIds();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void getPlayerIds(){
        String url = "http://40.88.38.140:8080/availability/top10/" + UserInfo.getUserId();
        Log.d(TAG, url);

        // Request a json array response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Display the result (what is send from server using res.send)
                        Log.d(TAG, response.toString());
                        try {
                            for(int i = 0; i < response.length(); i++){
                                getPlayerInfo(response.getJSONObject(i), (i+1));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }

    private void getPlayerInfo(JSONObject player, int index) throws JSONException{
        String playerId = player.get("id").toString();
        String url = "http://40.88.38.140:8080/users/" + playerId;
        Log.d(TAG, url);

        // Request a json array response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the result (what is send from server using res.send)
                        Log.d(TAG, response.toString());
                        try {
                            setPlayerAttributes(response, index);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void setPlayerAttributes(JSONObject player, int index) throws JSONException {
        Log.d(TAG, player.toString());
        switch(index){
            case 1:
                playerName1.setText(player.get("first_name").toString() + " " + player.get("last_name").toString());
                playerEmail1.setText("Email: " + player.get("email").toString());
                playerSkill1.setText("Skill level: " + player.get("skill_level").toString());
                break;
            case 2:
                playerName2.setText(player.get("first_name").toString() + " " + player.get("last_name").toString());
                playerEmail2.setText("Email: " + player.get("email").toString());
                playerSkill2.setText("Skill level: " + player.get("skill_level").toString());
                break;
            case 3:
                playerName3.setText(player.get("first_name").toString() + " " + player.get("last_name").toString());
                playerEmail3.setText("Email: " + player.get("email").toString());
                playerSkill3.setText("Skill level: " + player.get("skill_level").toString());
                break;
        }

    }

}
