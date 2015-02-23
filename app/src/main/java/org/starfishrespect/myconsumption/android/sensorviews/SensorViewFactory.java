package org.starfishrespect.myconsumption.android.sensorviews;

import android.content.Context;

/**
 * Factory for Sensor Views
 */
public class SensorViewFactory {
    public static AbstractSensorView makeView(Context context, String sensor) {
        sensor = sensor.toLowerCase();
        switch (sensor) {
            case "flukso":
                return new FluksoView(context);
        }
        return null;
    }
}
