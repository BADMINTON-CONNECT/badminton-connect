package com.example.badmintonconnect;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.stream.IntStream;

public class BookingActivity extends Activity {
    TextView textView;
    RequestQueue queue;
    Button checkDateButton;
    DatePicker datePicker;
    Spinner timeslot1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        textView = (TextView) findViewById(R.id.textView3);
        datePicker = (DatePicker) findViewById(R.id.bookingDatePicker);
        timeslot1 = (Spinner) findViewById(R.id.time_slot1);

        checkDateButton = findViewById(R.id.checkDateButton);

        checkDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckDate();
                Log.d("Booking Activity", "Attempting to check date");
            }
        });

        queue = Volley.newRequestQueue(this);


    }

    private void CheckDate(){
        //TODO: make this a variable
        // the 2020/10/26 will comes from the app and change base on the date the user choose
        int day = datePicker.getDayOfMonth();
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        String url ="http://40.88.38.140:8080/courts/" + year + "/" + (month+1) + "/" + day + "/" ; // this is the server url
        Log.d("Booking activity", url);

        // Request a string response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Display the result (what is send from server using res.send
                        // TODO: parse this data to display correctly
                        int time_slot1_avail = 0;
                        try {
                            JSONObject res = response.getJSONObject(0);
                            time_slot1_avail = (int) res.get("time_slot1");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String [] time_slot1_avail_array = new String[time_slot1_avail];

                        for(int i = 0; i < time_slot1_avail; i++){
                            time_slot1_avail_array[i] = Integer.toString(i+1);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter <String> (BookingActivity.this,
                                android.R.layout.simple_spinner_item, time_slot1_avail_array);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                        timeslot1.setAdapter(adapter);
                        // or alternately you can ask me to have another request which takes timeslots specifically
                        textView.setText(response.toString());

                        Log.d("Booking activity", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText(error.toString());
                Log.d("Booking activity", error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);
    }
}
