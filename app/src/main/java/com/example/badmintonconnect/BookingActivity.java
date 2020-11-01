package com.example.badmintonconnect;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingActivity extends Activity {
    final static String TAG = "Booking Activity";
    private RequestQueue queue;
    private Button checkDateButton;
    private DatePicker datePicker;
    private String userId;
    Map<String, String> bookingDetails;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        datePicker = (DatePicker) findViewById(R.id.bookingDatePicker);
        checkDateButton = (Button) findViewById(R.id.checkDateButton);

        List<String> no_court_availability = new ArrayList<>();
        no_court_availability.add("No courts available at this time");

        //initialize booking details with empty details
        bookingDetails = new HashMap<>();
        bookingDetails.put("court_id", "0");
        bookingDetails.put("time_slot1", "0");
        bookingDetails.put("time_slot2", "0");
        bookingDetails.put("time_slot3", "0");
        bookingDetails.put("time_slot4", "0");
        bookingDetails.put("time_slot1_original", "0");
        bookingDetails.put("time_slot2_original", "0");
        bookingDetails.put("time_slot3_original", "0");
        bookingDetails.put("time_slot4_original", "0");
        bookingDetails.put("day", "0");
        bookingDetails.put("month", "0");
        bookingDetails.put("year", "0");

        checkDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckDate();
                Log.d(TAG, "Attempting to check date");
            }
        });

        queue = Volley.newRequestQueue(this);


    }

    private void CheckDate() {
        Integer day = datePicker.getDayOfMonth();
        Integer month = datePicker.getMonth() + 1;
        Integer year = datePicker.getYear();
        String date = year + "-" + month + "-" + day;
        LocalDate bookingDate = LocalDate.parse(date);
        LocalDate today = java.time.LocalDate.now();

        if(today.isAfter(bookingDate)){
            Toast.makeText(BookingActivity.this, "Must check date on or after today", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://40.88.38.140:8080/courts/" + year + "/" + month + "/" + day + "/"; // this is the server url
        Log.d(TAG, url);

        bookingDetails.put("day", day.toString());
        bookingDetails.put("month", month.toString());
        bookingDetails.put("year", year.toString());

        // Request a json array response from the provided URL.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Display the result (what is send from server using res.send)
                        Log.d(TAG, response.toString());
                        int time_slot1_avail = 0;
                        int time_slot2_avail = 0;
                        int time_slot3_avail = 0;
                        int time_slot4_avail = 0;

                        try {
                            JSONObject res = response.getJSONObject(0);

                            bookingDetails.put("court_id", res.get("court_id").toString());
                            bookingDetails.put("time_slot1_original", res.get("time_slot1").toString());
                            bookingDetails.put("time_slot2_original", res.get("time_slot2").toString());
                            bookingDetails.put("time_slot3_original", res.get("time_slot3").toString());
                            bookingDetails.put("time_slot4_original", res.get("time_slot4").toString());
                            goToCourtsPage();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d(TAG, response.toString());
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

    private void goToCourtsPage(){
        Intent bookingCourtsIntent =
                new Intent(BookingActivity.this, BookingActivityCourts.class);
        bookingCourtsIntent.putExtra("day", bookingDetails.get("day"));
        bookingCourtsIntent.putExtra("month", bookingDetails.get("month"));
        bookingCourtsIntent.putExtra("year", bookingDetails.get("year"));
        bookingCourtsIntent.putExtra("court_id", bookingDetails.get("court_id"));
        bookingCourtsIntent.putExtra("time_slot1_original", bookingDetails.get("time_slot1_original"));
        bookingCourtsIntent.putExtra("time_slot2_original", bookingDetails.get("time_slot2_original"));
        bookingCourtsIntent.putExtra("time_slot3_original", bookingDetails.get("time_slot3_original"));
        bookingCourtsIntent.putExtra("time_slot4_original", bookingDetails.get("time_slot4_original"));

        startActivity(bookingCourtsIntent);
    }



}
