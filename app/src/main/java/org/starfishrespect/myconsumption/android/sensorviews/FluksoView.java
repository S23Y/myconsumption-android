package org.starfishrespect.myconsumption.android.sensorviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.server.api.dto.FluksoSensorSettingsDTO;

/**
 * View used to enter Flukso-specific settings
 */
public class FluksoView extends AbstractSensorView {

    private View view;
    private EditText editTextFluksoToken, editTextFluksoId;

    public FluksoView(Context context) {
        super(context);
        init(context);
    }

    public FluksoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FluksoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.settings_sensor_flukso, null);
        addView(view);
        editTextFluksoToken = (EditText) findViewById(R.id.editTextFluksoToken);
        editTextFluksoId = (EditText) findViewById(R.id.editTextFluksoId);
    }

    @Override
    public Object getSensorSettings() {
        FluksoSensorSettingsDTO settings = new FluksoSensorSettingsDTO();
        settings.setToken(editTextFluksoToken.getText().toString());
        settings.setFluksoId(editTextFluksoId.getText().toString());
        return settings;
    }

    @Override
    public boolean areSettingsValid() {
        return !editTextFluksoToken.getText().toString().equals("") && !editTextFluksoId.getText().toString().equals("");
    }

    @Override
    public void setEditMode(boolean editMode) {
        if (editMode) {
            editTextFluksoId.setEnabled(false);
            editTextFluksoId.setHint(R.string.hint_edit_not_changeable);
            editTextFluksoToken.setHint(R.string.hint_edit_type_value_not_changed);
        } else {
            editTextFluksoId.setEnabled(true);
            editTextFluksoId.setHint("");
            editTextFluksoToken.setHint("");
        }

    }
}
