package org.starfishrespect.myconsumption.android.events;

import java.util.Date;

/**
 * Event triggered when date is changed (ChartActivity)
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
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
