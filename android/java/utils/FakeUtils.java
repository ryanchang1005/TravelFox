package com.travelfox.ryan.utils;

import com.travelfox.ryan.R;

public class FakeUtils {
    public static int[] travelSamples = new int[]{
            R.drawable.travel_sample_1,
            R.drawable.travel_sample_2,
            R.drawable.travel_sample_3,
            R.drawable.travel_sample_4,
            R.drawable.travel_sample_5,
            R.drawable.travel_sample_6,
            R.drawable.travel_sample_7,
            R.drawable.travel_sample_8,
            R.drawable.travel_sample_9,
    };

    public static int getSampleImage(String key) {
        return travelSamples[Math.abs(key.hashCode()) % travelSamples.length];
    }
}
