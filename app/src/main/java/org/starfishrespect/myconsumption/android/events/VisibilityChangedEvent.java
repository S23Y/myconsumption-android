package org.starfishrespect.myconsumption.android.events;

import org.starfishrespect.myconsumption.android.data.SensorData;

/**
 * Event triggered when the visibility of a sensor is changed (ChartActivity)
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class VisibilityChangedEvent {
    private final SensorData sensor;

    public VisibilityChangedEvent(SensorData sensor) {
        this.sensor = sensor;
    }

    public SensorData getSensor() {
        return sensor;
    }
}
