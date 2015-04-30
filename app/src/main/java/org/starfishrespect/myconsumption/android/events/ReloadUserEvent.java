package org.starfishrespect.myconsumption.android.events;

/**
 * Created by thibaud on 30.04.15.
 */
public class ReloadUserEvent {
    private final boolean refreshData;

    public ReloadUserEvent(boolean refresh) {
        this.refreshData = refresh;
    }

    public boolean refreshData() {
        return refreshData;
    }
}
