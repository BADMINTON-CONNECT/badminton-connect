package com.example.badmintonconnect;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players);
        queue = Volley.newRequestQueue(this);

        playerName1 = (TextView) findViewById(R.id.player1_name);
        playerName2 = (TextView) findViewById(R.id.player2_name);
        playerName3 = (TextView) findViewById(R.id.player3_name);

        getPlayers();


    }

    private void getPlayers(){
        String url = "http://40.88.38.140:8080/users";
        // Request a json array response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Display the result (what is send from server using res.send)
                        Log.d(TAG, response.toString());
                        try {
                            setPlayerAttributes(response);
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

    private void setPlayerAttributes(JSONArray players) throws JSONException {

        JSONObject player1 = players.getJSONObject(0);
        JSONObject player2 = players.getJSONObject(1);
        JSONObject player3 = players.getJSONObject(2);

        playerName1.setText(player1.get("first_name").toString() + " " + player1.get("last_name").toString());
        playerName2.setText(player2.get("first_name").toString() + " " + player2.get("last_name").toString());
        playerName3.setText(player3.get("first_name").toString() + " " + player3.get("last_name").toString());

    }
}
