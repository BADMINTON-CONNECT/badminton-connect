package com.example.badmintonconnect;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.*;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private ImageView imageViewProfilePicture;
    private ImageButton buttonEdit;
    private EditText editTextUserEmail;
    private EditText editTextUserName;
    private EditText availableFrom;
    private EditText availableTo;
    private EditText availableWeekday;
    private Spinner spinnerUserSkillLevel;
    private Button buttonAddRow;
    private Button tableLayoutAvailability;
    private Button buttonSave;
    private String TAG = "LoginActivity";
    private boolean valid;
    public static String user_ID;
    private Map<String, String> userInfo = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TableLayout availabilityTable = (TableLayout) findViewById(R.id.TableLayoutAvailability);

        availableWeekday = (EditText) findViewById(R.id.availableWeekday);
        availableFrom = (EditText) findViewById(R.id.availableFrom);
        availableTo = (EditText) findViewById(R.id.availableTo);

        buttonAddRow = (Button) findViewById(R.id.buttonAddRow);
        buttonAddRow.setOnClickListener(v -> {
            Log.d(TAG, "add Row button clicked");
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.activity_addtablerow, availabilityTable);
        });

        imageViewProfilePicture = (ImageView) findViewById(R.id.imageViewProfilePicture);
        // create email text and disable editing
        editTextUserEmail = (EditText) findViewById(R.id.textViewUserEmail);
        editTextUserEmail.setTag(editTextUserEmail.getKeyListener());
        editTextUserEmail.setKeyListener(null);

        // create username text and disable editing
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextUserName.setTag(editTextUserName.getKeyListener());
        editTextUserName.setKeyListener(null);

        // create user skill level text and disable editing
        spinnerUserSkillLevel = (Spinner) findViewById(R.id.spinnerUserSkillLevel);
        spinnerUserSkillLevel.setEnabled(false);
        spinnerUserSkillLevel.setClickable(false);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.skillLevel, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserSkillLevel.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        spinnerUserSkillLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonEdit = (ImageButton) findViewById(R.id.imageButtonSettings);
        buttonEdit.setOnClickListener(v -> {
            Log.d(TAG, "Clicked settings button");
            editTextUserName.setKeyListener((KeyListener) editTextUserName.getTag());
            editTextUserEmail.setKeyListener((KeyListener) editTextUserEmail.getTag());
            availableWeekday.setEnabled(true);
            availableFrom.setEnabled(true);
            availableTo.setEnabled(true);
            availableWeekday.setFocusable(true);
            availableFrom.setFocusable(true);
            availableTo.setFocusable(true);
//            availableWeekday.setKeyListener((KeyListener) availableWeekday.getTag());
//            availableFrom.setKeyListener((KeyListener) availableFrom.getTag());
//            availableTo.setKeyListener((KeyListener) availableTo.getTag());
            spinnerUserSkillLevel.setEnabled(true);
            spinnerUserSkillLevel.setClickable(true);
            buttonSave.setAlpha(1);
            buttonAddRow.setAlpha(1);
        });
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> {
            valid = sendUserInfoToBackend(UserInfo.getUserId());
            if(valid) {
                Log.d(TAG, "Clicked Save Button");
                editTextUserName.setFocusable(false);
                editTextUserEmail.setFocusable(false);
                availableWeekday.setEnabled(false);
                availableFrom.setEnabled(false);
                availableTo.setEnabled(false);
                availableWeekday.setFocusable(false);
                availableFrom.setFocusable(false);
                availableTo.setFocusable(false);
                spinnerUserSkillLevel.setEnabled(false);
                spinnerUserSkillLevel.setClickable(false);
                buttonSave.setAlpha(0);
                buttonAddRow.setAlpha(0);
            }
        });

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        getUserInfoFromBackend(UserInfo.getUserId());
        updateUI(account);
    }

    private void getTableContent() {
        TableLayout availabilityTable = (TableLayout) findViewById(R.id.TableLayoutAvailability);
        for(int i = 0, j = availabilityTable.getChildCount(); i < j; i++){

        }
    }

    private boolean checkEmptyEditText() {
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextUserEmail = (EditText) findViewById(R.id.textViewUserEmail);
        String username = editTextUserName.getText().toString();
        String email = editTextUserEmail.getText().toString();

        if (username.matches("")) {
            sendToast("VALIDNAME");
            return false;
        }
        else {
            int index = 0;
            for (int i = 0; i < username.length(); i++) {
                if(username.charAt(i) == ' ') {
                    index = i;
                }
            }
            if(index == 0) {
                sendToast("VALIDNAME");
                return false;
            }
        }
        if (email.matches("")) {
            sendToast("VALIDEMAIL");
            return false;
        }
        else {
            int index = -1;
            for (int i = 0; i < email.length(); i++) {
                if(email.charAt(i) == '@') {
                    index = i;
                }
            }
            if(index == -1) {
                sendToast("VALIDEMAIL");
                return false;
            }
        }
        return true;
    }

    private boolean sendUserInfoToBackend(String user_ID) {
        try {
            editTextUserName = (EditText) findViewById(R.id.editTextUserName);
            editTextUserEmail = (EditText) findViewById(R.id.textViewUserEmail);
            spinnerUserSkillLevel = (Spinner) findViewById(R.id.spinnerUserSkillLevel);
            String firstName = "";
            String lastName = "";
            int index;
            boolean validEntry = checkEmptyEditText();
            if (validEntry == false) {
                Log.d(TAG, "Could not save user info");
                return false;
            }
            for (index = 0; editTextUserName.getText().charAt(index) != ' '; index++) {
                firstName += editTextUserName.getText().charAt(index);
            }
            lastName += editTextUserName.getText().toString().substring(index, editTextUserName.getText().length());

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://40.88.38.140:8080/users/" + user_ID;
            JSONObject userInfo = new JSONObject();
            // this is the json body that backend would use to get information
            userInfo.put("user_ID", user_ID);
            userInfo.put("first_name", firstName);
            userInfo.put("last_name", lastName);
            userInfo.put("email", editTextUserEmail.getText().toString());
            userInfo.put("skill", spinnerUserSkillLevel.getSelectedItem());
            final String mRequestBody = userInfo.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "successfully stored user information");
                    sendToast("SAVED");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        //responseString = String.valueOf(response.statusCode);
                        return super.parseNetworkResponse(response);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }
        return true;
    }

    private void getUserInfoFromBackend(String user_ID) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "http://40.88.38.140:8080/users/" + user_ID;
        Log.d(TAG, URL);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                Log.d(TAG, "successfully received user information");
                // parse user info
                try {
                    if (user_ID == null) {
                        Log.d(TAG, "ERROR - userID null");
                    }
                    if (response.getString("first_name") == "NULL" || response.getString("last_name") == "NULL") {
                        sendToast("VALIDNAME");
                    }
                    editTextUserName.setText(response.getString("first_name") + " " + response.getString("last_name"));
                    editTextUserEmail.setText(response.getString("email"));
                    spinnerUserSkillLevel.setSelection(response.getInt("skill"));
                } catch (JSONException e) {
                    Log.d(TAG, "user JSON Object incorrectly loaded. Check stacktrace for more information");
                    e.printStackTrace();
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

        requestQueue.add(jsonObjectRequest);
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            if (account.getPhotoUrl() == null) {
                imageViewProfilePicture.setImageResource(R.drawable.defaultprofilepicture);
            } else {
                String personPhotoUrl = account.getPhotoUrl().toString();
                Glide.with(this).load(personPhotoUrl).into(imageViewProfilePicture);
            }
        } else {
            Log.d(TAG, "Failed to update profile UI");
        }
    }

    private void sendToast(String toastType) {
        switch (toastType) {
            case "ERROR":
                Toast.makeText(this, "An Error has Occurred", Toast.LENGTH_SHORT).show();
                break;
            case "VALIDNAME":
                Toast.makeText(this, "Please enter a valid name.", Toast.LENGTH_SHORT).show();
                break;
            case "VALIDEMAIL":
                Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_SHORT).show();
                break;
            case "SAVED":
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "An Unknown Error Has Occurred", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
