package org.starfishrespect.myconsumption.android.sensorviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Base class for View used to fill sensor settings
 */
public abstract class AbstractSensorView extends LinearLayout {

    public AbstractSensorView(Context context) {
        super(context);
        initParentLinearLayout();
    }

    public AbstractSensorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initParentLinearLayout();
    }

    public AbstractSensorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initParentLinearLayout();
    }

    private void initParentLinearLayout() {
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public abstract Object getSensorSettings();

    public abstract boolean areSettingsValid();

    public abstract void setEditMode(boolean editMode);
}
