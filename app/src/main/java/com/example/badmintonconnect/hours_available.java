package com.example.badmintonconnect;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class hours_available {
    private int day;
    private ArrayList<Integer> hour = new ArrayList<>();

    public hours_available(int day, ArrayList<Integer> hour) {
        this.day = day;
        this.hour = hour;
    }

    public int getDay() {
        return this.day;
    }

    public ArrayList<Integer> getHour() {
        return this.hour;
    }

    // return true if valid string was passed, otherwise return false and set day = -1
    public boolean setDay(String weekday) {
        switch (weekday) {
            case "Monday":
            case "monday":
                this.day = 0;
                break;
            case "Tuesday":
            case "tuesday":
                this.day = 1;
                break;
            case "Wednesday":
            case "wednesday":
                this.day = 2;
                break;
            case "Thursday":
            case "thursday":
                this.day = 3;
                break;
            case "Friday":
            case "friday":
                this.day = 4;
                break;
            case "Saturday":
            case "saturday":
                this.day = 5;
                break;
            case "Sunday":
            case "sunday":
                this.day = 6;
                break;
            default:
                this.day = -1;
                return false;
        }
        return true;
    }

    public boolean setHour(int start, int end) {
        if (end - start <= 0 || end > 24 || end < 0 || start > 24 || end < 0) {
            return false;
        }
        for (int i = 0; i < end - start+1; i++) {
            hour.add(start+i);
        }
        return true;
    }
}
