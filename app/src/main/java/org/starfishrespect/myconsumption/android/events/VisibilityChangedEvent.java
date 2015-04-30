package org.starfishrespect.myconsumption.android.events;

import org.starfishrespect.myconsumption.android.data.SensorData;

/**
 * Created by thibaud on 30.04.15.
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
