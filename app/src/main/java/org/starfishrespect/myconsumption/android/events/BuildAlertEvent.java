package org.starfishrespect.myconsumption.android.events;

/**
 * Created by thibaud on 19.05.15.
 */
public class BuildAlertEvent {
    private final boolean buildAlert;

    public BuildAlertEvent(boolean b) {
        this.buildAlert = b;
    }

    public boolean buildAlert() {
        return buildAlert;
    }
}
