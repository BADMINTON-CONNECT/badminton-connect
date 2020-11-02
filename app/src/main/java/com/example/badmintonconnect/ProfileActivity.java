package com.example.badmintonconnect;

import android.content.Context;
import android.os.Bundle;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
        availableWeekday.setTag(availableWeekday.getKeyListener());
        availableWeekday.setKeyListener(null);
        availableFrom = (EditText) findViewById(R.id.availableFrom);
        availableFrom.setTag(availableFrom.getKeyListener());
        availableFrom.setKeyListener(null);
        availableTo = (EditText) findViewById(R.id.availableTo);
        availableTo.setTag(availableTo.getKeyListener());
        availableTo.setKeyListener(null);

        buttonAddRow = (Button) findViewById(R.id.buttonAddRow);
        buttonAddRow.setOnClickListener(v -> {
            Log.d(TAG, "add Row button clicked");
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.activity_addtablerow, availabilityTable);
            availableWeekday.setEnabled(true);
            availableFrom.setEnabled(true);
            availableTo.setEnabled(true);
            availableWeekday.setFocusable(true);
            availableFrom.setFocusable(true);
            availableTo.setFocusable(true);
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

        buttonEdit = (ImageButton) findViewById(R.id.imageButtonEdit);
        buttonEdit.setOnClickListener(v -> {
            Log.d(TAG, "Clicked settings button");
            editTextUserName.setKeyListener((KeyListener) editTextUserName.getTag());
            editTextUserEmail.setKeyListener((KeyListener) editTextUserEmail.getTag());
            availableWeekday.setKeyListener((KeyListener) availableWeekday.getTag());
            availableFrom.setKeyListener((KeyListener) availableFrom.getTag());
            availableTo.setKeyListener((KeyListener) availableTo.getTag());
            spinnerUserSkillLevel.setEnabled(true);
            spinnerUserSkillLevel.setClickable(true);
            buttonAddRow.setAlpha(1);
            buttonSave.setAlpha(1);
        });
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> {
            try {
                valid = sendUserInfoToBackend(UserInfo.getUserId()) && sendUserAvailabilityToBackend(UserInfo.getUserId());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if(valid) {
                Log.d(TAG, "Clicked Save Button");
                editTextUserName.setKeyListener(null);
                editTextUserEmail.setKeyListener(null);
                availableWeekday.setKeyListener(null);
                availableFrom.setKeyListener(null);
                availableTo.setKeyListener(null);
                spinnerUserSkillLevel.setEnabled(false);
                spinnerUserSkillLevel.setClickable(false);
                buttonAddRow.setAlpha(0);
                buttonSave.setAlpha(0);
            }
        });

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        getUserInfoFromBackend(UserInfo.getUserId());
        updateUI(account);
    }

    private String analyzeTable(ArrayList<hours_available> availabilities) throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        TableLayout availabilityTable = (TableLayout) findViewById(R.id.TableLayoutAvailability);
        for(int i = 1, j = availabilityTable.getChildCount(); i < j; i++){
            View view = availabilityTable.getChildAt(i);
            if(view instanceof TableRow){
                ArrayList<Integer> example = new ArrayList<>();
                hours_available oHoursAvail = new hours_available(1, example);
                int rowChildCount = ((TableRow) view).getChildCount();
                int from = 0;
                int to = 0;
                String weekday = "";
                boolean validFrom = false;
                boolean validWeekday = false;
                for(int k = 0; k < rowChildCount; k++) {
                    View viewChild = ((TableRow) view).getChildAt(k);
                    try {
                        String widgetId = viewChild.getResources().getResourceEntryName(viewChild.getId());
                        EditText editText = (EditText) viewChild;
                        switch(widgetId) {
                        case "availableWeekday":
                            weekday += editText.getText().toString();
                            break;
                        case "availableFrom":
                            if(editText.getText().toString() != ""){
                                from = Integer.parseInt(editText.getText().toString());
                                validFrom = true;
                            }
                            else {
                                from = -1;
                                sendToast("VALIDFROMTO");
                                return "";
                            }
                            break;
                        case "availableTo":
                            if(editText.getText().toString() != "" && validFrom){
                                to = Integer.parseInt(editText.getText().toString());
                                oHoursAvail.setHour(from, to);
                                validWeekday = oHoursAvail.setDay(weekday);
                                if(!validWeekday) {
                                    sendToast("VALIDWEEKDAY");
                                    return "";
                                }
                                else{
                                    availabilities.add(oHoursAvail);
                                }
                            }
                            else {
                                from = -1;
                                sendToast("VALIDFROMTO");
                                return "";
                            }
                            break;
                        default:
                            Log.d(TAG, widgetId);
                        }
                    } catch (Exception e){
                        Log.d(TAG, "error finding resource id");
                    }
                }
            }
        }
        String json = ow.writeValueAsString(availabilities);
        return json;
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
            String json = "";
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
            lastName += editTextUserName.getText().toString().substring(index+1, editTextUserName.getText().length());

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

    private boolean sendUserAvailabilityToBackend(String user_ID) throws JsonProcessingException{
        ArrayList<hours_available> availabilities = new ArrayList<>();
        String json = "";
        Log.d(TAG, "this is the table before!!! " + availabilities.toString());
        json += analyzeTable(availabilities);
        if (json == "") {
            return false;
        }
        JSONObject object1 = new JSONObject();
        JSONArray array = new JSONArray();
        JSONArray ja = new JSONArray();
        JSONObject object2 = new JSONObject();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "http://40.88.38.140:8080/availability/" + user_ID;
        JSONObject userInfo = new JSONObject();
        try {
            userInfo.put("hours_available", json);

            array.put("1");
            array.put("2");

            object1.put("day", 1);
            object1.put("hour", array);

            ja.put(object1);
            object2.put("available_hours", ja);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequestAvailability = new JsonObjectRequest(Request.Method.POST, URL, object2,
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
        requestQueue.add(jsonObjectRequestAvailability);
        return true;
    }

    private void getUserInfoFromBackend(String user_ID) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String usersURL = "http://40.88.38.140:8080/users/" + user_ID;
        String availabilityURL = "http://40.88.38.140:8080/availability/" + user_ID;

        Log.d(TAG, usersURL);
        JsonObjectRequest usersJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, usersURL, null, new Response.Listener<JSONObject>() {
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

//        JsonObjectRequest availabilityJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, availabilityURL, null, new Response.Listener<JSONObject>() {
//            public void onResponse(JSONObject response) {
//                Log.d(TAG, "successfully received user information");
//                // parse user info
//                try {
//                    if (user_ID == null) {
//                        Log.d(TAG, "ERROR - userID null");
//                    }
//                    if(response.getString("day") == "" || response.getString("hour") == "") {
//                        sendToast("ERROR");
//                    }
//                    // TODO
//                    editTextUserName.setText(response.getString("first_name") + " " + response.getString("last_name"));
//                    editTextUserEmail.setText(response.getString("email"));
//                    spinnerUserSkillLevel.setSelection(response.getInt("skill"));
//                } catch (JSONException e) {
//                    Log.d(TAG, "user JSON Object incorrectly loaded. Check stacktrace for more information");
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d(TAG, "Unable to retrieve user information see log below for details: ");
//                Log.d(TAG, error.toString());
//            }
//        }) {
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//        };

        requestQueue.add(usersJsonObjectRequest);
//        requestQueue.add(availabilityJsonObjectRequest);
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
            case "VALIDWEEKDAY":
                Toast.makeText(this, "Please enter a valid weekday.\nFormat should be e.g. Monday, Tuesday, Wednesday, etc.", Toast.LENGTH_SHORT).show();
                break;
            case "VALIDFROMTO":
                Toast.makeText(this, "Please enter a valid availability time range.\nFormat should be in 24-hour format.", Toast.LENGTH_SHORT).show();
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
