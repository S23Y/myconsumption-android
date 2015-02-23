package org.starfishrespect.myconsumption.android.data;

/**
 * Class used to store frequencies that are displayed on
 * spinners and transferred to the chart fragment
 */
public class FrequencyData {
    private String name;
    private int delay;

    // delays in seconds
    public static final int DELAY_MINUTE = 60;
    public static final int DELAY_FIVE_MINUTES = 300;
    public static final int DELAY_15MIN = 900;
    public static final int DELAY_HOUR = 3600;
    public static final int DELAY_DAY = 86400;
    public static final int DELAY_WEEK = 604800;
    public static final int DELAY_MONTH = 2592000;
    public static final int DELAY_YEAR = 31536000;
    public static final int DELAY_EVERYTHING = -1;


    public FrequencyData(String name, int delay) {
        this.name = name;
        this.delay = delay;
    }

    public String getName() {
        return name;
    }

    public int getDelay() {
        return delay;
    }

}
