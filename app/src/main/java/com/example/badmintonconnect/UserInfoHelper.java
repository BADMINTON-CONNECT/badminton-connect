package com.example.badmintonconnect;

public class UserInfoHelper {
    private static String userId;
    public static String getUserId() {return userId;}
    public static void setUserId(String userId) {
        UserInfoHelper.userId = userId;
    }
}
