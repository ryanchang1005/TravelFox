package com.travelfox.ryan.utils;

import java.util.Map;

public class URLUtils {
    public static String getURLParameters(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            String value = map.get(key);
            if (value != null) {
                if (sb.length() != 0) {
                    sb.append("&");
                }
                sb.append(StrUtils.format("%s=%s", key, value));
            }
        }
        return sb.toString();
    }
}
