package com.travelfox.ryan.utils;

import java.util.Locale;

public class StrUtils {
    public static String format(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }
}
