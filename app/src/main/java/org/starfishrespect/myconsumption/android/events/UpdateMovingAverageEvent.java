package org.starfishrespect.myconsumption.android.events;

/**
 * Created by thibaud on 30.04.15.
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
