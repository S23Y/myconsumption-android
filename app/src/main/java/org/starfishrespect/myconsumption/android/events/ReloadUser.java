package org.starfishrespect.myconsumption.android.events;

/**
 * Created by thibaud on 30.04.15.
 */
public class ReloadUser {
    private final boolean refreshData;

    public ReloadUser(boolean refresh) {
        this.refreshData = refresh;
    }

    public boolean refreshData() {
        return refreshData;
    }
}
