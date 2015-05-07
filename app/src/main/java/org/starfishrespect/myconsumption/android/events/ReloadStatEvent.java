package org.starfishrespect.myconsumption.android.events;

/**
 * Created by thibaud on 07.05.15.
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
