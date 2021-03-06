package com.example.badmintonconnect;

import android.content.Context;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ProfileActivity extends AppCompatActivity {
    private ImageView imageViewProfilePicture;
    private EditText editTextUserEmail;
    private EditText editTextUserName;
    private EditText availableFrom;
    private EditText availableTo;
    private EditText availableWeekday;
    private Spinner spinnerUserSkillLevel;
    private Spinner spinnerUserDistancePref;
    private Button buttonAddRow;
    private Button buttonSave;
    private TableLayout availabilityTable;
    private final static String TAG = "ProfileActivity";
    private boolean valid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /* availability table
         * contains rows with children:
         * - available weekday : must be populated with full weekday - e.g. "Monday"
         * - available From : must be populated with an integer value (24 hour format) - e.g. 13 (represents 1pm)
         * - available To : must be populated with an integer value (24 hour format) - e.g. 16 (represents 4pm)
         * *Note that the availableFrom < availableTo
         */
        availabilityTable = (TableLayout) findViewById(R.id.TableLayoutAvailability);

        availableWeekday = (EditText) findViewById(R.id.availableWeekday);
        availableWeekday.setTag(availableWeekday.getKeyListener());
        availableWeekday.setKeyListener(null);

        availableFrom = (EditText) findViewById(R.id.availableFrom);
        availableFrom.setTag(availableFrom.getKeyListener());
        availableFrom.setKeyListener(null);

        availableTo = (EditText) findViewById(R.id.availableTo);
        availableTo.setTag(availableTo.getKeyListener());
        availableTo.setKeyListener(null);

        setButtons();

        imageViewProfilePicture = (ImageView) findViewById(R.id.imageViewProfilePicture);

        // create email text and disable editing
        editTextUserEmail = (EditText) findViewById(R.id.textViewUserEmail);
        editTextUserEmail.setTag(editTextUserEmail.getKeyListener());
        editTextUserEmail.setKeyListener(null);

        // create username text and disable editing
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextUserName.setTag(editTextUserName.getKeyListener());
        editTextUserName.setKeyListener(null);

        setSpinners();

        // get the current logged-in account information
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        // populate profile page with stored user information
        pullUserInfo(UserInfoHelper.getUserId());

        // update UI (profile image)
        updateUI(account);
    }

    private void setSpinners(){
        // create spinner for user skill level
        spinnerUserSkillLevel = (Spinner) findViewById(R.id.spinnerUserSkillLevel);
        spinnerUserSkillLevel.setEnabled(false);
        spinnerUserSkillLevel.setClickable(false);
        ArrayAdapter<CharSequence> adapterUserSkillLevel = ArrayAdapter.createFromResource(this, R.array.skillLevel, android.R.layout.simple_spinner_item);
        adapterUserSkillLevel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserSkillLevel.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapterUserSkillLevel,
                        R.layout.contact_spinner_row_nothing_selected,
                        this));
        adapterUserSkillLevel.notifyDataSetChanged();

        // create spinner for distance pref
        spinnerUserDistancePref = (Spinner) findViewById(R.id.spinnerUserDistancePreference);
        spinnerUserDistancePref.setEnabled(false);
        spinnerUserDistancePref.setClickable(false);
        ArrayAdapter<CharSequence> adapterUserDistancePref = ArrayAdapter.createFromResource(this, R.array.distancePref, android.R.layout.simple_spinner_item);
        adapterUserDistancePref.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserDistancePref.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapterUserDistancePref,
                        R.layout.contact_spinner_row_nothing_selected,
                        this));
        adapterUserDistancePref.notifyDataSetChanged();
    }

    private void setButtons(){
        // create button to allow users to add more rows to the availability table - by default, off
        buttonAddRow = (Button) findViewById(R.id.buttonAddRow);
        buttonAddRow.setEnabled(false);
        buttonAddRow.setOnClickListener(v -> {
            Log.d(TAG, "add Row button clicked");
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.activity_addtablerow, availabilityTable);
            availableWeekday.setKeyListener((KeyListener) availableWeekday.getTag());
            availableFrom.setKeyListener((KeyListener) availableFrom.getTag());
            availableTo.setKeyListener((KeyListener) availableTo.getTag());
        });

        // create edit button
        ImageButton buttonEdit = (ImageButton) findViewById(R.id.imageButtonEdit);
        buttonEdit.setOnClickListener(v -> {
            Log.d(TAG, "Clicked settings button");
            enableTableEdit(true);
            editTextUserName.setKeyListener((KeyListener) editTextUserName.getTag());
            editTextUserEmail.setKeyListener((KeyListener) editTextUserEmail.getTag());
            spinnerUserSkillLevel.setEnabled(true);
            spinnerUserSkillLevel.setClickable(true);
            spinnerUserDistancePref.setEnabled(true);
            spinnerUserDistancePref.setClickable(true);
            buttonAddRow.setAlpha(1);
            buttonAddRow.setEnabled(true);
            buttonSave.setAlpha(1);
            buttonSave.setEnabled(true);
        });

        // create save button (hidden by default until user accesses 'edit mode'
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setEnabled(false);
        buttonSave.setOnClickListener(v -> {
            valid = pushUserInfo(UserInfoHelper.getUserId());
            if (valid) {
                sendToast("SAVED");
                enableTableEdit(false);
                editTextUserName.setKeyListener(null);
                availableWeekday.setKeyListener(null);
                availableFrom.setKeyListener(null);
                availableTo.setKeyListener(null);
                spinnerUserSkillLevel.setEnabled(false);
                spinnerUserSkillLevel.setClickable(false);
                spinnerUserDistancePref.setEnabled(false);
                spinnerUserDistancePref.setClickable(false);
                buttonAddRow.setAlpha(0);
                buttonAddRow.setEnabled(false);
                buttonSave.setAlpha(0);
                buttonSave.setEnabled(false);
            }
        });
    }

    // function to allow contents in the table to be editable or not
    private void enableTableEdit(boolean enable) {
        TableLayout availabilityTable = (TableLayout) findViewById(R.id.TableLayoutAvailability);
        if (enable) {
            for (int i = 1, j = availabilityTable.getChildCount(); i < j; i++) {
                View view = availabilityTable.getChildAt(i);
                if (view instanceof TableRow) {
                    int rowChildCount = ((TableRow) view).getChildCount();
                    for (int k = 0; k < rowChildCount; k++) {
                        View viewChild = ((TableRow) view).getChildAt(k);
                        try {
                            String widgetId = viewChild.getResources().getResourceEntryName(viewChild.getId());
                            switch (widgetId) {
                                case "availableWeekday":
                                case "availableFrom":
                                case "availableTo":
                                    EditText editext = (EditText) viewChild;
                                    editext.setKeyListener((KeyListener) editTextUserEmail.getTag());
                                    break;
                                default:
                                    Log.d(TAG, widgetId);
                                    break;
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "error finding resource id");
                        }
                    }
                }
            }
        } else {
            for (int i = 1, j = availabilityTable.getChildCount(); i < j; i++) {
                View view = availabilityTable.getChildAt(i);
                if (view instanceof TableRow) {
                    int rowChildCount = ((TableRow) view).getChildCount();
                    for (int k = 0; k < rowChildCount; k++) {
                        View viewChild = ((TableRow) view).getChildAt(k);
                        try {
                            String widgetId = viewChild.getResources().getResourceEntryName(viewChild.getId());
                            switch (widgetId) {
                                case "availableWeekday":
                                case "availableFrom":
                                case "availableTo":
                                    EditText editext = (EditText) viewChild;
                                    editext.setKeyListener(null);
                                    break;
                                default:
                                    Log.d(TAG, widgetId);
                                    break;
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "error finding resource id");
                        }
                    }
                }
            }
        }
    }

    // function to analyze availability table content
    private JSONArray analyzeTable() throws JsonProcessingException {
        JSONArray availableTimesArray = new JSONArray();
        JSONObject analyzedRow;

        TableLayout availabilityTable = (TableLayout) findViewById(R.id.TableLayoutAvailability);
        for (int i = 1, j = availabilityTable.getChildCount(); i < j; i++) {
            View view = availabilityTable.getChildAt(i);
            if (view instanceof TableRow) {
                JSONArray temp = new JSONArray();
                Availability availability = new Availability(1, temp);
                int rowChildCount = ((TableRow) view).getChildCount();
                int from = 0;
                int to = 0;
                String weekday = "";
                boolean validFrom = false;
                boolean validTo = false;
                boolean validWeekday = false;

                // check if row is empty:
                View availableWeekdayViewChild = ((TableRow) view).getChildAt(0);
                View availableFromViewChild = ((TableRow) view).getChildAt(1);
                View availableToViewChild = ((TableRow) view).getChildAt(3);
                EditText availableWeekdayEditText = (EditText) availableWeekdayViewChild;
                EditText availableFromEditText = (EditText) availableFromViewChild;
                EditText availableToEditText = (EditText) availableToViewChild;
                Log.d(TAG, "available weekday edit text: " + availableWeekdayEditText.getText().toString().isEmpty());
                Log.d(TAG, "available From edit text: " + availableFromEditText.getText().toString().isEmpty());
                Log.d(TAG, "available To edit text: " + availableToEditText.getText().toString().isEmpty());
                if (availableWeekdayEditText.getText().toString().isEmpty() && availableFromEditText.getText().toString().isEmpty() && availableToEditText.getText().toString().isEmpty()) {
                    // delete that row:
                    availabilityTable.removeView((TableRow) view);
                    continue;
                }

                for (int k = 0; k < rowChildCount; k++) {
                    View viewChild = ((TableRow) view).getChildAt(k);
                    try {
                        String widgetId = viewChild.getResources().getResourceEntryName(viewChild.getId());
                        EditText editText = (EditText) viewChild;
                        switch (widgetId) {
                            case "availableWeekday":
                                if (!editText.getText().toString().isEmpty()) {
                                    weekday += editText.getText().toString();
                                    validWeekday = availability.setDay(weekday);
                                    if (!validWeekday) {
                                        sendToast("VALIDWEEKDAY");
                                    }
                                }
                                break;
                            case "availableFrom":
                                if (!editText.getText().toString().isEmpty()) {
                                    from = Integer.parseInt(editText.getText().toString());
                                    validFrom = true;
                                } else {
                                    validFrom = false;
                                    from = -1;
                                }
                                break;
                            case "availableTo":
                                if (!editText.getText().toString().isEmpty() && validFrom) {
                                    to = Integer.parseInt(editText.getText().toString());
                                    validTo = availability.setHour(from, to);
                                    if (!validTo) {
                                        from = -1;
                                        sendToast("VALIDFROMTO");
                                        return null;
                                    } else {
                                        // SUCCESS
                                        analyzedRow = availability.getHoursAvailable();
                                        availableTimesArray.put(analyzedRow);
                                        Log.d(TAG, "" + availableTimesArray);
                                    }
                                } else {
                                    sendToast("VALIDFROMTO");
                                }
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
        return availableTimesArray;
    }

    // function to populate the availability table with contents from the db
    private void populateAvailabilityTable(JSONArray dbTable) {
        Log.d(TAG, "dbTable: " + dbTable);
        Log.d(TAG, "dbTable.Length: " + dbTable.length());

        if (dbTable.length() == 0) {
            sendToast("AVAILEMPTY");
            return;
        }

        TableLayout availabilityTable = (TableLayout) findViewById(R.id.TableLayoutAvailability);

        if (dbTable.length() > 1) {
            int count = 1;
            while (count < dbTable.length()) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(R.layout.activity_addtablerow, availabilityTable);
                count++;
            }
        }

        for (int i = 1, j = availabilityTable.getChildCount(); i < j; i++) {
            int entryIndex = i - 1;
            Log.d(TAG, "entryIndex is : " + entryIndex);
            View view = availabilityTable.getChildAt(i);
            if (view instanceof TableRow) {
                int rowChildCount = ((TableRow) view).getChildCount();
                for (int k = 0; k < rowChildCount; k++) {
                    View viewChild = ((TableRow) view).getChildAt(k);
                    try {
                        JSONObject obj = (JSONObject) dbTable.getJSONObject(entryIndex);
                        JSONArray array = (JSONArray) obj.getJSONArray("hours");
                        String widgetId = viewChild.getResources().getResourceEntryName(viewChild.getId());
                        EditText editText = (EditText) viewChild;
                        switch (widgetId) {
                            case "availableWeekday":
                                editText.setKeyListener(null);
                                editText.setText(getWeekday(obj.getInt("day")));
                                break;
                            case "availableFrom":
                                if (array.length() == 0 || array == null) {
                                    sendToast("VALIDFROMTO");
                                    break;
                                }
                                editText.setKeyListener(null);
                                editText.setText(array.get(0).toString());
                                break;
                            case "availableTo":
                                if (array.length() == 0 || array == null) {
                                    sendToast("VALIDFROMTO");
                                    break;
                                }
                                editText.setKeyListener(null);
                                editText.setText(array.get(array.length() - 1).toString());
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
    }

    // function to convert backend weekday integer to String
    private String getWeekday(int weekdayNum) {
        String weekday = "";
        switch (weekdayNum) {
            case 0:
                weekday += "Monday";
                break;
            case 1:
                weekday += "Tuesday";
                break;
            case 2:
                weekday += "Wednesday";
                break;
            case 3:
                weekday += "Thursday";
                break;
            case 4:
                weekday += "Friday";
                break;
            case 5:
                weekday += "Saturday";
                break;
            case 6:
                weekday += "Sunday";
                break;
            default:
                sendToast("ERROR");
                break;
        }
        return weekday;
    }

    // function to validate edit text entries
    private boolean checkEmptyEditText() {
        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextUserEmail = (EditText) findViewById(R.id.textViewUserEmail);
        String username = editTextUserName.getText().toString();
        String email = editTextUserEmail.getText().toString();

        if (username.matches("")) {
            sendToast("VALIDNAME");
            return false;
        } else {
            int index = 0;
            for (int i = 0; i < username.length(); i++) {
                if (username.charAt(i) == ' ') {
                    index = i;
                }
            }
            if (index == 0) {
                sendToast("VALIDNAME");
                return false;
            }
        }
        if (email.matches("")) {
            sendToast("VALIDEMAIL");
            return false;
        } else {
            int index = -1;
            for (int i = 0; i < email.length(); i++) {
                if (email.charAt(i) == '@') {
                    index = i;
                }
            }
            if (index == -1) {
                sendToast("VALIDEMAIL");
                return false;
            }
        }
        return true;
    }

    // function to send basic user information
    private boolean pushUserInfo(String user_ID) {
        try {
            String usersInfoURL = "http://40.88.148.58:8080/users/" + user_ID;
            String availabilityURL = "http://40.88.148.58:8080/availability/" + user_ID;
            String firstName = "";
            String lastName = "";
            int index;
            boolean validEntry = checkEmptyEditText();

            RequestQueue requestQueue = Volley.newRequestQueue(this);

            // process user information
            if (!validEntry) {
                Log.d(TAG, "Could not save user info");
                return false;
            }
            for (index = 0; editTextUserName.getText().charAt(index) != ' '; index++) {
                firstName += editTextUserName.getText().charAt(index);
            }
            lastName += editTextUserName.getText().toString().substring(index + 1, editTextUserName.getText().length());

            JSONObject userInfo = new JSONObject();
            // this is the json body that backend would use to get information
            userInfo.put("user_ID", user_ID);
            userInfo.put("first_name", firstName);
            userInfo.put("last_name", lastName);
            userInfo.put("skill_level", spinnerUserSkillLevel.getSelectedItem());
            userInfo.put("distance_preference", spinnerUserDistancePref.getSelectedItem());
            final String mRequestBody = userInfo.toString();

            // process availability table
            JSONArray table = analyzeTable();
            if (table == null) {
                return false;
            }
            JSONObject userAvailabilityObject = new JSONObject();

            try {
                userAvailabilityObject.put("hours_available", table);
                Log.d(TAG, "" + userAvailabilityObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            StringRequest stringRequest = new StringRequest(Request.Method.PUT, usersInfoURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "successfully stored user information");
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
                        return super.parseNetworkResponse(response);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            requestQueue.add(stringRequest);
            requestAvailability(availabilityURL, userAvailabilityObject, requestQueue);
        } catch (JSONException | JsonProcessingException e) {
            e.printStackTrace();
            return true;
        }
        return true;
    }

    private void requestAvailability(String availabilityURL, JSONObject userAvailabilityObject, RequestQueue requestQueue){
        JsonObjectRequest jsonObjectRequestAvailability = new JsonObjectRequest(Request.Method.POST, availabilityURL, userAvailabilityObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "user availability has been successfully stored!");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        });
        requestQueue.add(jsonObjectRequestAvailability);
    }

    // populate profile page from information retrieved from db
    private void pullUserInfo(String user_ID) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String usersURL = "http://40.88.148.58:8080/users/" + user_ID;
        String availabilityURL = "http://40.88.148.58:8080/availability/" + user_ID;

        JsonObjectRequest usersJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, usersURL, null, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                Log.d(TAG, "successfully received user information");
                // parse user info
                try {
                    if (user_ID == null) {
                        Log.d(TAG, "ERROR - userID null");
                    }
                    if (response.getString("first_name").equals("NULL") || response.getString("last_name").equals("NULL")) {
                        sendToast("VALIDNAME");
                    }
                    editTextUserName.setText(response.getString("first_name") + " " + response.getString("last_name"));
                    editTextUserEmail.setText(response.getString("email"));
                    spinnerUserSkillLevel.setSelection(response.getInt("skill_level"));
                    spinnerUserDistancePref.setSelection(response.getInt("distance_preference"));

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

        JsonArrayRequest availabilityJsonObjectRequest = new JsonArrayRequest(Request.Method.GET, availabilityURL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, "successfully received user availability");
                // parse user info
                populateAvailabilityTable(response);
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

        requestQueue.add(usersJsonObjectRequest);
        requestQueue.add(availabilityJsonObjectRequest);
    }

    // update user profile image to google account profile image
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

    // shared toast sending function
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
            case "AVAILEMPTY":
                Toast.makeText(this, "Please enter your availability", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "An Unknown Error Has Occurred", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
