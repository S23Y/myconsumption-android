package org.starfishrespect.myconsumption.android.events;

/**
 * Event triggered when user needs to be reloaded.
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class ReloadUserEvent {
    private final boolean refreshData;

    public ReloadUserEvent(boolean refresh) {
        this.refreshData = refresh;
    }

    public boolean refreshDataFromServer() {
        return refreshData;
    }
}
