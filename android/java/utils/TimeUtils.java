package com.travelfox.ryan.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public static String ISO8601ToDisplayDate(String text) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(text);
            return new SimpleDateFormat("yyyy/MM/dd").format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    public static String dateToISO8601(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date);
    }

    public static String dateToDisplayDate(Date date) {
        return new SimpleDateFormat("yyyy/MM/dd").format(date);
    }

    public static String getUTS() {
        return String.valueOf(System.currentTimeMillis());
    }

}
