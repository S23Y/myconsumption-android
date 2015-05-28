package org.starfishrespect.myconsumption.android.events;

/**
 * Event that triggers an alert in a specific activity.
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
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
