package com.travelfox.ryan.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtils {


    public static final String USER_ID = "Cm0HHL8bOc8kHrIkf0x4tJ2BP448rkYcNl7_6Y_hI08";
    public static final String ACCESS_TOKEN = "7MhYKbdkoc-lF9-unXhaDaDCnZ4QKI79oUeduD1gA6s";
    public static final String GUEST_EMAIL = "kVW5EKswv8IvU2BOx9wwTWCh6RI5P6FYa9ekTWWSiaQ";
//    public static final String XXX = "vHiX8kWvROS9wpVjEh3_bJz_u2ptDoFiOwu6MlJL8fY";
//    public static final String XXX = "GIfpnemMKGKyCkIiCNkY1AiloV6hGZqqwF-51v0wVLo";
//    public static final String XXX = "bwfnhkMpGfm4q9ooqK4IGj2Y7sKKxeuvIxSUjU41IbU";
    SharedPreferences pref;

    public PrefUtils(Context context) {
        pref = context.getSharedPreferences("Planner", Context.MODE_PRIVATE);
    }

    public void setString(String k, String v) {
        pref.edit().putString(k, v).apply();
    }

    public String getString(String k) {
        return pref.getString(k, null);
    }

    public void setLong(String k, long v) {
        pref.edit().putLong(k, v).apply();
    }

    public long getLong(String k) {
        return pref.getLong(k, -1);
    }

    public void clear() {
        pref.edit().clear().apply();
    }
}
