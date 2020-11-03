package com.example.badmintonconnect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static java.time.LocalDate.now;

public class BookingActivity extends Activity {
    final private static String TAG = "Booking Activity";
    private RequestQueue queue;
    private DatePicker datePicker;
    private Map<String, String> bookingDetails;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        datePicker = (DatePicker) findViewById(R.id.bookingDatePicker);
        Button checkDateButton = (Button) findViewById(R.id.checkDateButton);

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
                checkDate();
                Log.d(TAG, "Attempting to check date");
            }
        });

        queue = Volley.newRequestQueue(this);


    }

    /*
    * @desc: checks the date chosen by the datepicker. If it is before today the date is invalid.
    * If the date is invalid, it will display a toast error message and not allow the user to move forward.
    * If the date is valid the function will then make a get request to the db to check the court
    * availability for this date and move to the next page
    * @params: none
    * @return: none
    * */
    private void checkDate() {
        Integer day = datePicker.getDayOfMonth();
        Integer month = datePicker.getMonth() + 1;
        Integer year = datePicker.getYear();
        String dayString = day.toString();
        String monthString = month.toString();
        if(day < 10){
            dayString = "0" + day;
        }
        if(month < 10){
            monthString = "0" + month;
        }

        String date = year + "-" + monthString + "-" + dayString;
        LocalDate bookingDate = LocalDate.parse(date);
        LocalDate today = now();

        if(today.isAfter(bookingDate)){
            Toast.makeText(BookingActivity.this, "Must check date on or after today", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://40.88.38.140:8080/courts/" + year + "/" + month + "/" + day + "/"; // this is the server url
        Log.d(TAG, url);

        bookingDetails.put("day", day.toString());
        bookingDetails.put("month", month.toString());
        bookingDetails.put("year", year.toString());

        // Get request for court availability
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Display the result (what is send from server using res.send)
                        Log.d(TAG, response.toString());

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

    /*
    * @desc: This function creates an intent to go to the court booking page and populates the
    * booking details in the intent, so that the next page will have access to them.
    * */
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
