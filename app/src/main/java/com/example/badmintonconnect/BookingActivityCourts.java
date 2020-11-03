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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class BookingActivityCourts extends Activity {
    final static String TAG = "Booking Activity";
    private RequestQueue queue;
    Map<String, String> bookingDetails;
    private Spinner timeSlot1;
    private Spinner timeSlot2;
    private Spinner timeSlot3;
    private Spinner timeSlot4;
    private TextView bookingDate;
    private Button bookingButton;
    private Button checkAnotherDateButton;
    private TextView avail1;
    private TextView avail2;
    private TextView avail3;
    private TextView avail4;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_courts);

        Bundle extras = getIntent().getExtras();
        bookingDetails = new HashMap<>();

        if(extras != null){

            bookingDetails.put("court_id", extras.getString("court_id"));
            bookingDetails.put("time_slot1_original", extras.getString("time_slot1_original"));
            bookingDetails.put("time_slot2_original", extras.getString("time_slot2_original"));
            bookingDetails.put("time_slot3_original", extras.getString("time_slot3_original"));
            bookingDetails.put("time_slot4_original", extras.getString("time_slot4_original"));
            bookingDetails.put("day", extras.getString("day"));
            bookingDetails.put("month", extras.getString("month"));
            bookingDetails.put("year", extras.getString("year"));
        }
        else{
            Toast.makeText(this, "An Error has Occurred", Toast.LENGTH_SHORT).show();
        }

        timeSlot1 = (Spinner) findViewById(R.id.time_slot1);
        timeSlot2 = (Spinner) findViewById(R.id.time_slot2);
        timeSlot3 = (Spinner) findViewById(R.id.time_slot3);
        timeSlot4 = (Spinner) findViewById(R.id.time_slot4);
        avail1 = (TextView) findViewById(R.id.time_slot1_availability);
        avail2 = (TextView) findViewById(R.id.time_slot2_availability);
        avail3 = (TextView) findViewById(R.id.time_slot3_availability);
        avail4 = (TextView) findViewById(R.id.time_slot4_availability);

        setSpinners(parseInt(bookingDetails.get("time_slot1_original")), parseInt(bookingDetails.get("time_slot2_original")),
                parseInt(bookingDetails.get("time_slot3_original")), parseInt(bookingDetails.get("time_slot4_original")));
        SetAvailabilityText(bookingDetails.get("time_slot1_original"), bookingDetails.get("time_slot2_original"),
                bookingDetails.get("time_slot3_original"), bookingDetails.get("time_slot4_original"));

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                int parentId = parent.getId();
                String value = (String) parent.getItemAtPosition(pos);

                switch(parentId){
                    case R.id.time_slot1:
                        if(value != "No courts available at this time"){
                            bookingDetails.put("time_slot1", value);
                            Log.d(TAG, value);
                        }
                        break;
                    case R.id.time_slot2:
                        if(value != "No courts available at this time"){
                            bookingDetails.put("time_slot2", value);
                            Log.d(TAG, value);
                        }
                        break;
                    case R.id.time_slot3:
                        if(value != "No courts available at this time"){
                            bookingDetails.put("time_slot3", value);
                            Log.d(TAG, value);
                        }
                        break;
                    case R.id.time_slot4:
                        if(value != "No courts available at this time"){
                            bookingDetails.put("time_slot4", value);
                            Log.d(TAG, value);
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        timeSlot1.setOnItemSelectedListener(onItemSelectedListener);
        timeSlot2.setOnItemSelectedListener(onItemSelectedListener);
        timeSlot3.setOnItemSelectedListener(onItemSelectedListener);
        timeSlot4.setOnItemSelectedListener(onItemSelectedListener);

        bookingDate = (TextView) findViewById(R.id.courtAvailabilityDateText);
        String bookingText = "Showing available courts for: " + bookingDetails.get("month") + "/" +
                bookingDetails.get("day") + "/" + bookingDetails.get("year");
        bookingDate.setText(bookingText);

        bookingButton = (Button) findViewById(R.id.bookingButton3);

        bookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeBooking();
                Log.d(TAG, "Attempting to book");
            }
        });

        checkAnotherDateButton = (Button) findViewById(R.id.checkAnotherDate);

        checkAnotherDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "checking another date");
                Intent bookingIntent = new Intent(BookingActivityCourts.this, BookingActivity.class);
                startActivity(bookingIntent);
            }
        });

        queue = Volley.newRequestQueue(this);

    }

    /*
    * @desc: this function sets the time slot spinners with the appropriate availability
    * */
    private void setSpinners(Integer time_slot1_avail, Integer time_slot2_avail, Integer time_slot3_avail, Integer time_slot4_avail) {
        List<String> time_slot1_avail_array = new ArrayList<>();
        List<String> time_slot2_avail_array = new ArrayList<>();
        List<String> time_slot3_avail_array = new ArrayList<>();
        List<String> time_slot4_avail_array = new ArrayList<>();

        for (int i = 0; i <= time_slot1_avail; i++) {
            time_slot1_avail_array.add(Integer.toString(i));
        }
        for (int i = 0; i <= time_slot2_avail; i++) {
            time_slot2_avail_array.add(Integer.toString(i));
        }
        for (int i = 0; i <= time_slot3_avail; i++) {
            time_slot3_avail_array.add(Integer.toString(i));
        }
        for (int i = 0; i <= time_slot4_avail; i++) {
            time_slot4_avail_array.add(Integer.toString(i));
        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(BookingActivityCourts.this,
                android.R.layout.simple_spinner_item, time_slot1_avail_array);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(BookingActivityCourts.this,
                android.R.layout.simple_spinner_item, time_slot2_avail_array);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(BookingActivityCourts.this,
                android.R.layout.simple_spinner_item, time_slot3_avail_array);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(BookingActivityCourts.this,
                android.R.layout.simple_spinner_item, time_slot4_avail_array);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        timeSlot1.setAdapter(adapter1);
        timeSlot2.setAdapter(adapter2);
        timeSlot3.setAdapter(adapter3);
        timeSlot4.setAdapter(adapter4);

    }

    /*
     * @desc: this function creates a confirmation dialogue for making a booking
     * It checks that the booking is valid (ie. not all zeroes).
     * Upon confirmation it calls a post request to make the booking and moves to the confirmation page.
     * Upon cancellation it dismisses the dialogue.
     * */
    private void makeBooking(){
        if(bookingDetails.get("time_slot1").equals("0") && bookingDetails.get("time_slot2").equals("0")
                && bookingDetails.get("time_slot3").equals("0") && bookingDetails.get("time_slot4").equals("0")){
            Toast.makeText(this, "No courts selected", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivityCourts.this);


        String bookingText = "Date: " + bookingDetails.get("day") + "/" +
                bookingDetails.get("month") + "/" + bookingDetails.get("year") + " At the following time slots:" +
                "\n Time Slot 1: " + bookingDetails.get("time_slot1") + "\n Time Slot 2: " + bookingDetails.get("time_slot2") +
                "\n Time Slot 3: " + bookingDetails.get("time_slot3") + "\n Time Slot 4: " + bookingDetails.get("time_slot4");
        Log.d(TAG, bookingText);

        builder.setTitle("Confirm Booking");
        builder.setMessage("Would you like to book for: " + bookingText);

        JSONObject objectBooking = new JSONObject();
        try {
            objectBooking.put("user_id", UserInfo.getUserId());
            objectBooking.put("Year", bookingDetails.get("year"));
            objectBooking.put("Month", bookingDetails.get("month"));
            objectBooking.put("Date", bookingDetails.get("day"));
            objectBooking.put("time_slot1", bookingDetails.get("time_slot1"));
            objectBooking.put("time_slot2", bookingDetails.get("time_slot2"));
            objectBooking.put("time_slot3", bookingDetails.get("time_slot3"));
            objectBooking.put("time_slot4", bookingDetails.get("time_slot4"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Integer time_slot1 = Integer.parseInt(bookingDetails.get("time_slot1_original")) - Integer.parseInt(bookingDetails.get("time_slot1"));
                Integer time_slot2 = Integer.parseInt(bookingDetails.get("time_slot2_original")) - Integer.parseInt(bookingDetails.get("time_slot2"));
                Integer time_slot3 = Integer.parseInt(bookingDetails.get("time_slot3_original")) - Integer.parseInt(bookingDetails.get("time_slot3"));
                Integer time_slot4 = Integer.parseInt(bookingDetails.get("time_slot4_original")) - Integer.parseInt(bookingDetails.get("time_slot4"));


                String urlCourts = "http://40.88.38.140:8080/courts";
                String urlBookings = "http://40.88.38.140:8080/bookings";

                JSONObject object = new JSONObject();
                try {
                    //input your API parameters
                    object.put("time_slot1", time_slot1.toString());
                    object.put("time_slot2", time_slot2.toString());
                    object.put("time_slot3", time_slot3.toString());
                    object.put("time_slot4", time_slot4.toString());
                    object.put("court_id", bookingDetails.get("court_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Updating court availability
                JsonObjectRequest jsonObjectRequestCourts = new JsonObjectRequest(Request.Method.PUT, urlCourts, object,
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
                queue.add(jsonObjectRequestCourts);

                //Updating user booking
                JsonObjectRequest jsonObjectRequestBookings = new JsonObjectRequest(Request.Method.POST, urlBookings, objectBooking,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("JSON", String.valueOf(response));
                                goToConfirmation();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                });
                queue.add(jsonObjectRequestBookings);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(BookingActivityCourts.this, "Booking cancelled", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "booking cancelled");
            }
        });

        AlertDialog confirmBookingDialog = builder.create();
        confirmBookingDialog.show();

    }

    /*
     * @desc: This function creates an intent to go to the confirmation page and populates the
     * booking details in the intent, so that the next page will have access to them.
     * */
    private void goToConfirmation(){
        Intent bookingConfirmationIntent =
                new Intent(BookingActivityCourts.this, BookingActivityConfirmation.class);
        bookingConfirmationIntent.putExtra("day", bookingDetails.get("day"));
        bookingConfirmationIntent.putExtra("month", bookingDetails.get("month"));
        bookingConfirmationIntent.putExtra("year", bookingDetails.get("year"));
        bookingConfirmationIntent.putExtra("time_slot1", bookingDetails.get("time_slot1"));
        bookingConfirmationIntent.putExtra("time_slot2", bookingDetails.get("time_slot2"));
        bookingConfirmationIntent.putExtra("time_slot3", bookingDetails.get("time_slot3"));
        bookingConfirmationIntent.putExtra("time_slot4", bookingDetails.get("time_slot4"));

        startActivity(bookingConfirmationIntent);
    }

    /*
     * @desc: this function sets the time slot availability text with the appropriate availability
     * */
    private void SetAvailabilityText(String time_slot1, String time_slot2, String time_slot3, String time_slot4){
        avail1.setText("Courts available: " + time_slot1);
        avail2.setText("Courts available: " + time_slot2);
        avail3.setText("Courts available: " + time_slot3);
        avail4.setText("Courts available: " + time_slot4);

    }
}
