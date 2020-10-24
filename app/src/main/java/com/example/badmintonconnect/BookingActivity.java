package com.example.badmintonconnect;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class BookingActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        final TextView textView = (TextView) findViewById(R.id.MakeABooking);

        RequestQueue queue = Volley.newRequestQueue(this);

        //TODO: make this a variable
        // the 2020/10/26 will comes from the app and change base on the date the user choose
        String url ="http://40.88.38.140:8080/courts/2020/10/26"; // this is the server url

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the result (what is send from server using res.send
                        // TODO: parse this data to display correctly
                        // or alternately you can ask me to have another request which takes timeslots specifically
                        textView.setText(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText(error.toString());
                Log.d("Booking activity", error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
