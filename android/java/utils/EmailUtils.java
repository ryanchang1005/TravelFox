package com.travelfox.ryan.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class EmailUtils {
    private static String generateRandomText(int length) {
        Random random = new Random();
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (random.nextFloat() * letters.length());
            sb.append(letters.charAt(index));
        }
        return sb.toString();
    }

    public static String generateGuestEmail() {

        String prefix = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String random = EmailUtils.generateRandomText(10);
        return StrUtils.format("%s.%s@invest-planner.com", prefix, random);
    }
}
