package org.starfishrespect.myconsumption.android.events;

/**
 * Event triggered when config needs to be reloaded.
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class ReloadConfigEvent {
    private final boolean reload;

    public ReloadConfigEvent(boolean b) {
        this.reload = b;
    }

    public boolean isReload() {
        return reload;
    }
}
