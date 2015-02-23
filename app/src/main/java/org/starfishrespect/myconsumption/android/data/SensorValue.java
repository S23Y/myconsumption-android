package org.starfishrespect.myconsumption.android.data;

/**
 * Object used to store sensor values
 */
public class SensorValue implements Comparable<SensorValue> {
    private int timestamp;
    private int value;

    public SensorValue(int timestamp, int value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int compareTo(SensorValue another) {
        if (this.timestamp == another.timestamp) {
            return 0;
        }
        if (this.timestamp > another.timestamp) {
            return 1;
        }
        return -1;
    }
}
