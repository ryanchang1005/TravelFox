package com.travelfox.ryan.utils;

import android.util.Log;

public class L {
    public static final String TAG = "TravelPlanner";
    public static void d(String msg){
        Log.d(TAG, msg);
    }
    public static void d(String key, String msg){
        Log.d(key, msg);
    }
}
