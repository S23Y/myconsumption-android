package org.starfishrespect.myconsumption.android.events;

import org.starfishrespect.myconsumption.android.data.SensorData;

/**
 * Created by thibaud on 30.04.15.
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
