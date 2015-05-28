package org.starfishrespect.myconsumption.android.events;

/**
 * Event triggered when stats need to be reloaded.
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class ReloadStatEvent {
    private final boolean reload;

    public ReloadStatEvent(boolean b) {
        this.reload = b;
    }

    public boolean isReload() {
        return reload;
    }
}
