package org.starfishrespect.myconsumption.android.events;

import org.starfishrespect.myconsumption.android.data.SensorData;

/**
 * Event triggered when color is changed (ChartActivity)
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class ColorChangedEvent {
    private final SensorData sensor;

    public ColorChangedEvent(SensorData sensor) {
        this.sensor = sensor;
    }

    public SensorData getSensor() {
        return sensor;
    }
}
