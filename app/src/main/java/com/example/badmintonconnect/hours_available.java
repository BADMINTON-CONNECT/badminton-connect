package com.example.badmintonconnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class hours_available {
    private int day;
    private JSONArray hour = new JSONArray();
    private JSONObject availability = new JSONObject();

    public hours_available(int day, JSONArray hour) {
        this.day = day;
        this.hour = hour;
    }

    public int getDay() {
        return this.day;
    }

    public JSONArray getHour() {
        return this.hour;
    }

    public JSONObject getHoursAvailable() {
        return availability;
    }

    // return true if valid string was passed, otherwise return false and set day = -1
    public boolean setDay(String weekday) {
        switch (weekday.toLowerCase()) {
            case "monday":
                this.day = 0;
                break;
            case "tuesday":
                this.day = 1;
                break;
            case "wednesday":
                this.day = 2;
                break;
            case "thursday":
                this.day = 3;
                break;
            case "friday":
                this.day = 4;
                break;
            case "saturday":
                this.day = 5;
                break;
            case "sunday":
                this.day = 6;
                break;
            default:
                this.day = -1;
                return false;
        }
        try {
            availability.put("day", this.day);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean setHour(int start, int end) {
        if (end - start <= 0 || end > 24 || end < 0 || start > 24 || end < 0) {
            return false;
        }
        for (int i = 0; i < end - start+1; i++) {
            hour.put(start+i);
        }
        try {
            availability.put("hour", this.hour);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}
