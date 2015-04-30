package org.starfishrespect.myconsumption.android.events;

/**
 * Created by thibaud on 30.04.15.
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
