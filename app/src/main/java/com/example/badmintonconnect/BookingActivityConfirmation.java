package com.example.badmintonconnect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class BookingActivityConfirmation extends Activity {
    final private static String TAG = "Booking Activity";

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        ImageView checkMark = (ImageView) findViewById(R.id.checkmark);
        TextView bookingConfirmation = (TextView) findViewById(R.id.booking_confirmation_text2);
        Button returnHomeButton = (Button) findViewById(R.id.returnHomeButton);

        checkMark.setImageResource(R.drawable.checkmark);
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            String bookingConfirmationText = "Booking made for \n Date: " + extras.getString("month") + "/" +
                    extras.getString("day") + "/" + extras.getString("year") +
                    "\n At the following time slots:" + "\n Time Slot 1: " + extras.get("time_slot1") +
                    "\n Time Slot 2: " + extras.get("time_slot2") +
                    "\n Time Slot 3: " + extras.get("time_slot3") +
                    "\n Time Slot 4: " + extras.get("time_slot4");

            bookingConfirmation.setText(bookingConfirmationText);
        }

        returnHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Returning home");
                Intent homePageIntent = new Intent(BookingActivityConfirmation.this, HomePageActivity.class);
                startActivity(homePageIntent);
            }
        });


    }

}
