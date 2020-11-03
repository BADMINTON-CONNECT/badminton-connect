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
import java.util.HashMap;

public class PlayersActivity extends Activity {
    final static String TAG = "Player Activity";
    private HashMap<Integer, String> DAYS_OF_WEEK;
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
    private JSONArray userAvailability;
    private JSONArray playerAvailabilityArray1;
    private JSONArray playerAvailabilityArray2;
    private JSONArray playerAvailabilityArray3;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);
        queue = Volley.newRequestQueue(this);

        DAYS_OF_WEEK = new HashMap<>();
        DAYS_OF_WEEK.put(0, "Monday");
        DAYS_OF_WEEK.put(1, "Tuesday");
        DAYS_OF_WEEK.put(2, "Wednesday");
        DAYS_OF_WEEK.put(3, "Thursday");
        DAYS_OF_WEEK.put(4, "Friday");
        DAYS_OF_WEEK.put(5, "Saturday");
        DAYS_OF_WEEK.put(6, "Sunday");

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

    /*
     * @desc: this function sets the player location in their user, using the location passed from
     * the previous page.
     * @param: user_ID - current user id
     * @param: x - longitude
     * @param: y - latitude
     * */
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
                checkPlayerAvailability(user_ID, -1, null);
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

    /*
     * @desc: this function creates a get request to retrieve the top 10 most matched players.
     * We will only display the top 3 most matched players.
     * */
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

    /*
     * @desc: this function creates a get request to retrieve the according player information
     * to display
     * @param: JSONObject player - player information
     * @param: index - player rank
     * */
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
                            checkPlayerAvailability(playerId, index, response);
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

    /*
     * @desc: this function sets the according text views to display player information.
     * @param: JSONObject player - player information object
     * @param: index - the rank of this player
     * */
    private void setPlayerAttributes(JSONObject player, int index) throws JSONException {
        Log.d(TAG, player.toString());
        JSONArray overlap;

        switch(index){
            case 1:
                overlap = compareAvailabilities(userAvailability, playerAvailabilityArray1);
                displayAvailability(overlap, index);
                playerName1.setText(player.get("first_name").toString() + " " + player.get("last_name").toString());
                playerEmail1.setText("Email: " + player.get("email").toString());
                playerSkill1.setText("Skill level: " + player.get("skill_level").toString());
                break;
            case 2:
                overlap = compareAvailabilities(userAvailability, playerAvailabilityArray2);
                displayAvailability(overlap, index);
                playerName2.setText(player.get("first_name").toString() + " " + player.get("last_name").toString());
                playerEmail2.setText("Email: " + player.get("email").toString());
                playerSkill2.setText("Skill level: " + player.get("skill_level").toString());
                break;
            case 3:
                overlap = compareAvailabilities(userAvailability, playerAvailabilityArray3);
                displayAvailability(overlap, index);
                playerName3.setText(player.get("first_name").toString() + " " + player.get("last_name").toString());
                playerEmail3.setText("Email: " + player.get("email").toString());
                playerSkill3.setText("Skill level: " + player.get("skill_level").toString());
                break;
        }

    }

    /*
     * @desc: this function sends a get request to check the player availability
     * @param: user_ID: ID of player to check availability for
     * @param: JSONObject player - player information object
     * @param: index - the rank of this player
     * */
    private void checkPlayerAvailability(String user_ID, int index, JSONObject playerInfo){
        String URL = "http://40.88.38.140:8080/availability/" + user_ID;
        Log.d(TAG, URL);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            public void onResponse(JSONArray response) {
                Log.d(TAG, "successfully received availability information");
                Log.d(TAG, response.toString());
                // parse user info
                if (user_ID == null || response.length() == 0) {
                    Log.d(TAG, "ERROR - userID null");
                }

                switch(index){
                    case -1:
                        userAvailability = response;
                        getPlayerIds();
                        break;
                    case 1:
                        playerAvailabilityArray1 = response;
                        try {
                            setPlayerAttributes(playerInfo, index);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        playerAvailabilityArray2 = response;
                        try {
                            setPlayerAttributes(playerInfo, index);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 3:
                        playerAvailabilityArray3 = response;
                        try {
                            setPlayerAttributes(playerInfo, index);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
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

    /*
     * @desc: this function compares two availability arrays and returns the overlap in their availabilities
     * as a JSONArray
     * @param: player1 - first availability to compare
     * @param: player2 - second availability to compare
     * @return: overlap - JSONArray containing overlap of the two
     * */
    private JSONArray compareAvailabilities(JSONArray player1, JSONArray player2) throws JSONException {
        JSONArray overlap = new JSONArray();
        JSONObject avail1;
        JSONObject avail2;

        for (int i = 0; i < player1.length(); i++){
            avail1 = (JSONObject) player1.get(i);

            for (int j = 0; j < player2.length(); j++){
                avail2 = (JSONObject) player2.get(j);
                if(avail1.get("day").equals(avail2.get("day"))){
                    JSONObject temp = new JSONObject();
                    temp.put("day", avail1.get("day"));
                    //compare hours
                    temp.put("hours", compareHours((JSONArray) avail1.get("hours"), (JSONArray) avail2.get("hours")));
                    overlap.put(temp);
                }
            }
        }

        return overlap;
    }

    /*
     * @desc: this function compares the hours given in two arrays and returns the similarity
     * @param: avail1 - first availability to compare
     * @param: avail2 - second availability to compare
     * @return: finalHours - arraylist of hours which overlap
     * */
    private ArrayList<Integer> compareHours(JSONArray avail1, JSONArray avail2) throws JSONException {
        ArrayList<Integer> finalHours = new ArrayList<>();

        for(int i = 0; i < avail1.length(); i++){
            for(int j = 0; j < avail2.length(); j++){
                if(avail1.get(i).equals(avail2.get(j))){
                    finalHours.add(avail1.getInt(i));
                }
            }
        }

        return finalHours;
    }

    /*
     * @desc: this function sets the according text views to display player availability.
     * @param: JSONArray availability - availability information array
     * @param: index - the rank of this player
     * */
    private void displayAvailability(JSONArray availability, int index) throws JSONException {
        int longestIndex = 0;

        for(int i = 0; i < availability.length(); i++){
            JSONObject temp = availability.getJSONObject(i);
            ArrayList<Integer> avail = (ArrayList<Integer>) temp.get("hours");
            if(avail.size() > longestIndex){
                longestIndex = i;
            }
        }

        JSONObject finalAvail = availability.getJSONObject(longestIndex);
        ArrayList<Integer> finalHours = (ArrayList<Integer>) finalAvail.get("hours");
        Integer day = finalAvail.getInt("day");
        Integer hourBegin = finalHours.get(0);
        Integer hourEnd =  finalHours.get(finalHours.size()-1);
        String availText = "";

        if(hourBegin != hourEnd){
            availText = "Most available on " + DAYS_OF_WEEK.get(day) + "s at " + hourBegin.toString() + " to " + hourEnd.toString();
        }
        else{
            availText = "Most available on " + DAYS_OF_WEEK.get(day) + "s at " + hourBegin.toString();
        }

        switch(index){
            case 1:
                playerAvailability1.setText(availText);
                break;
            case 2:
                playerAvailability2.setText(availText);
                break;
            case 3:
                playerAvailability3.setText(availText);
                break;
        }
    }

}
