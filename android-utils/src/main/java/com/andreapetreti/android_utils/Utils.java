package com.andreapetreti.android_utils;

import java.util.Calendar;
import java.util.TimeZone;

public abstract class Utils {

    /**
     * Check if difference between current UTC time and last UTC time, is greater than threshold.
     * @param lastUTCMillis
     * @param thresholdMillis Threshold time in milliseconds.
     * @return True if difference is greater than {thresholdMillis}.
     */
    public static boolean checkTimeDifference(long lastUTCMillis, long thresholdMillis) {
        long current = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
        long difference = Math.abs(current - lastUTCMillis);

        return difference > thresholdMillis;
    }

    public static long currentTimeUTCMillis() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
    }

}
