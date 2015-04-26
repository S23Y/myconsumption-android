package org.starfishrespect.myconsumption.android.util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by thibaud on 20.04.15.
 */
public class StatUtils {
    /**
     * Convert watt to kWh and round it up with two decimals.
     * @param watt the value you want to convert as a String
     */
    public static double w2kWh(int watt) {
        //double converted = ((double) watt*60.0) / 1000.0;
        double converted = watt / (60.0 * 60.0 * 1000.0); // TODO correct?
        return Math.round(converted * 100.0) / 100.0;
    }

    /**
     * Convert a linux timestamp to a Date time.
     * @param timestamp
     * @return a date formatted as a String.
     */
    public static String timestamp2DateString(int timestamp) {
        Date date = new Date((long) timestamp * 1000);
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }
}
