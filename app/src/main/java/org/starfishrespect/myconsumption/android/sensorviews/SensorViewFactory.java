package org.starfishrespect.myconsumption.android.sensorviews;

import android.content.Context;

/**
 * Factory for Sensor Views
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
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
