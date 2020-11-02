package com.example.badmintonconnect;

public class UserInfo {
    private static String userId;
    public static String getUserId() {return userId;}
    public static void setUserId(String userId) {
        UserInfo.userId = userId;
    }
}
