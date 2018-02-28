package com.chikchiksoftware.service;

import java.util.concurrent.TimeUnit;

/**
 * Created by
 *
 * @authors Anton Nagornyi
 * on 01.03.2018.
 */
public class TimeConversionService {

    public static String millisToDHMS(long duration) {
        String result;

        if(duration < 0) {
            return "00:00:00";
        }
        long days  = TimeUnit.MILLISECONDS.toDays(duration);
        long hours = TimeUnit.MILLISECONDS.toHours(duration)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
        if (days == 0) {
            result = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        else {
            result = String.format("%dd%02d:%02d:%02d", days, hours, minutes, seconds);
        }
        return result;
    }
}
