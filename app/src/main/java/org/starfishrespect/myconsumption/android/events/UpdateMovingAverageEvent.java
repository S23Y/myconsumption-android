package org.starfishrespect.myconsumption.android.events;

/**
 * Event triggered when the seekbar of the moving average is modified (ChartActivity)
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class UpdateMovingAverageEvent {
    private final int seekBarPosition;

    public UpdateMovingAverageEvent(int seekBarPosition) {
        this.seekBarPosition = seekBarPosition;
    }

    public int getSeekBarPosition() {
        return seekBarPosition;
    }
}
