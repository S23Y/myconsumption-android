package org.starfishrespect.myconsumption.android.events;

import java.util.Date;

/**
 * Created by thibaud on 30.04.15.
 */
public class DateChangedEvent {
    private final Date date;
    private final int dateDelay;
    private final int valueDelay;

    public DateChangedEvent(Date date, int dateDelay, int valueDelay) {
        this.date = date;
        this.dateDelay = dateDelay;
        this.valueDelay = valueDelay;
    }

    public Date getDate() {
        return date;
    }

    public int getDateDelay() {
        return dateDelay;
    }

    public int getValueDelay() {
        return valueDelay;
    }
}
