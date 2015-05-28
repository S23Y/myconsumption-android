package org.starfishrespect.myconsumption.android.events;

/**
 * Event triggered by a fragment when it is ready (ChartActivity)
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class FragmentsReadyEvent {
    private final boolean value;
    private final Class<?> aClass;

    public FragmentsReadyEvent(Class<?> aClass, boolean value) {
        this.value = value;
        this.aClass = aClass;
    }

    public boolean isValue() {
        return value;
    }

    public Class<?> getFragmentClass() {
        return aClass;
    }
}
