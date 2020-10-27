package com.example.badmintonconnect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

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
import java.util.List;
import java.util.Map;

public class BookingActivity extends Activity {
    final static String TAG = "Booking Activity";
    private RequestQueue queue;
    private Button checkDateButton;
    private Button bookingButton;
    private DatePicker datePicker;
    private TextView bookingDetailText;
    private Spinner timeSlot1;
    private Spinner timeSlot2;
    private Spinner timeSlot3;
    private Spinner timeSlot4;
    Map<String, String> bookingDetails;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        datePicker = (DatePicker) findViewById(R.id.bookingDatePicker);
        bookingDetailText = (TextView) findViewById(R.id.bookingDetailText);
        bookingButton = (Button) findViewById(R.id.bookingButton);
        checkDateButton = (Button) findViewById(R.id.checkDateButton);
        timeSlot1 = (Spinner) findViewById(R.id.time_slot1);
        timeSlot2 = (Spinner) findViewById(R.id.time_slot2);
        timeSlot3 = (Spinner) findViewById(R.id.time_slot3);
        timeSlot4 = (Spinner) findViewById(R.id.time_slot4);



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

        //initializing spinners
        setSpinners(no_court_availability, no_court_availability, no_court_availability, no_court_availability);

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


        checkDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckDate();
                Log.d(TAG, "Attempting to check date");
            }
        });

        bookingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeBooking();
                Log.d(TAG, "Attempting to book");
            }
        });

        queue = Volley.newRequestQueue(this);


    }

    private void CheckDate() {
        Integer day = datePicker.getDayOfMonth();
        Integer month = datePicker.getMonth();
        Integer year = datePicker.getYear();

        String url = "http://40.88.38.140:8080/courts/" + year + "/" + (month + 1) + "/" + day + "/"; // this is the server url
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
                            time_slot1_avail = (int) res.get("time_slot1");
                            time_slot2_avail = (int) res.get("time_slot2");
                            time_slot3_avail = (int) res.get("time_slot3");
                            time_slot4_avail = (int) res.get("time_slot4");
                            bookingDetails.put("court_id", res.get("court_id").toString());
                            bookingDetails.put("time_slot1_original", res.get("time_slot1").toString());
                            bookingDetails.put("time_slot2_original", res.get("time_slot2").toString());
                            bookingDetails.put("time_slot3_original", res.get("time_slot3").toString());
                            bookingDetails.put("time_slot4_original", res.get("time_slot4").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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

                        setSpinners(time_slot1_avail_array, time_slot2_avail_array, time_slot3_avail_array, time_slot4_avail_array);
                        // or alternately you can ask me to have another request which takes timeslots specifically

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

    private void MakeBooking(){
        if(bookingDetails.get("time_slot1").equals("0") && bookingDetails.get("time_slot2").equals("0")
        && bookingDetails.get("time_slot3").equals("0") && bookingDetails.get("time_slot4").equals("0")){
            bookingDetailText.setText("No bookings made");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);


        String bookingText = "Date: " + bookingDetails.get("day") + "/" +
                bookingDetails.get("month") + "/" + bookingDetails.get("year") + " At the following timeslots:" +
                "\n Time Slot 1: " + bookingDetails.get("time_slot1") + "\n Time Slot 2: " + bookingDetails.get("time_slot2") +
                "\n Time Slot 3: " + bookingDetails.get("time_slot3") + "\n Time Slot 4: " + bookingDetails.get("time_slot4");
        Log.d(TAG, bookingText);

        builder.setTitle("Confirm Booking");
        builder.setMessage("Would you like to book for: " + bookingText);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bookingDetailText.setText("Booking made for: " + bookingText);

                Integer time_slot1 = Integer.parseInt(bookingDetails.get("time_slot1_original")) - Integer.parseInt(bookingDetails.get("time_slot1"));
                Integer time_slot2 = Integer.parseInt(bookingDetails.get("time_slot2_original")) - Integer.parseInt(bookingDetails.get("time_slot2"));
                Integer time_slot3 = Integer.parseInt(bookingDetails.get("time_slot3_original")) - Integer.parseInt(bookingDetails.get("time_slot3"));
                Integer time_slot4 = Integer.parseInt(bookingDetails.get("time_slot4_original")) - Integer.parseInt(bookingDetails.get("time_slot4"));

                String url = "http://40.88.38.140:8080/courts";


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
                // Enter the correct url for your api service site
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, object,
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
                queue.add(jsonObjectRequest);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "booking cancelled");
            }
        });

        AlertDialog confirmBookingDialog = builder.create();
        confirmBookingDialog.show();

    }

    private void setSpinners(List<String> court_availability_1, List<String> court_availability_2, List<String> court_availability_3, List<String> court_availability_4) {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(BookingActivity.this,
                android.R.layout.simple_spinner_item, court_availability_1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(BookingActivity.this,
                android.R.layout.simple_spinner_item, court_availability_2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(BookingActivity.this,
                android.R.layout.simple_spinner_item, court_availability_3);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(BookingActivity.this,
                android.R.layout.simple_spinner_item, court_availability_4);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        timeSlot1.setAdapter(adapter1);
        timeSlot2.setAdapter(adapter2);
        timeSlot3.setAdapter(adapter3);
        timeSlot4.setAdapter(adapter4);

    }


}
