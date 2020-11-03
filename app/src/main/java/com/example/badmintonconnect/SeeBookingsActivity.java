package com.example.badmintonconnect;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
import org.w3c.dom.Text;

public class SeeBookingsActivity extends Activity {
    final String TAG ="SeeBookingsActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seebooking);

        getUserBooking(UserInfoHelper.getUserId());
    }

    private void getUserBooking(String user_ID) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String userBookingURL = "http://40.88.38.140:8080/bookings/" + user_ID;

        JsonArrayRequest availabilityJsonObjectRequest = new JsonArrayRequest(Request.Method.GET, userBookingURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "successfully received user availability");
                // parse user info
                populateBookingsTable(response);
            }
        }, error -> {
            Log.d(TAG, "Unable to retrieve user information see log below for details: ");
            Log.d(TAG, error.toString());
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        requestQueue.add(availabilityJsonObjectRequest);
    }

    private void populateBookingsTable(JSONArray dbTable) {
        Log.d(TAG, "dbTable: " + dbTable);
        Log.d(TAG, "dbTable.Length: " + dbTable.length());

        TextView textViewNoBooking = (TextView) findViewById(R.id.textViewEmptyTable);
        ImageView imageViewNoBooking = (ImageView) findViewById(R.id.imageViewEmptyTable);
        textViewNoBooking.setAlpha(0);
        imageViewNoBooking.setAlpha(0.0f);

        if (dbTable.length() == 0) {
            Log.d(TAG, "db bookings table empty");
            textViewNoBooking.setAlpha(1);
            imageViewNoBooking.setAlpha(1.0f);
            return;
        }

        TableLayout bookingsTable = (TableLayout) findViewById(R.id.TableLayoutAvailability);

        if (dbTable.length() > 1) {
            int count = 1;
            while (count < dbTable.length()) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(R.layout.activity_addbookingtablerow, bookingsTable);
                count++;
            }
        }

        for (int i = 1, j = bookingsTable.getChildCount(); i < j; i++) {
            int entryIndex = i - 1;
            Log.d(TAG, "entryIndex is : " + entryIndex);
            View view = bookingsTable.getChildAt(i);
            if (view instanceof TableRow) {
                int rowChildCount = ((TableRow) view).getChildCount();
                for (int k = 0; k < rowChildCount; k++) {
                    View viewChild = ((TableRow) view).getChildAt(k);
                    try {
                        JSONObject obj = (JSONObject) dbTable.getJSONObject(entryIndex);
                        String widgetId = viewChild.getResources().getResourceEntryName(viewChild.getId());
                        TextView textView = (TextView) viewChild;
                        Log.d(TAG, "THIS IS WIDGETID" + widgetId);
                        switch (widgetId) {
                            case "date":
                                String displayText = obj.getString("Month") + "/" +
                                        obj.getString("Date") + "/"
                                        + obj.getString("Year");
                                textView.setText(displayText);
                                break;
                            case "slot1":
                                textView.setText(obj.getString("time_slot1"));
                                break;
                            case "slot2":
                                textView.setText(obj.getString("time_slot2"));
                                break;
                            case "slot3":
                                textView.setText(obj.getString("time_slot3"));
                                break;
                            case "slot4":
                                textView.setText(obj.getString("time_slot4"));
                                break;
                            default:
                                Log.d(TAG, widgetId);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "error finding resource id");
                    }
                }
            }
        }
        return;

    }

}
