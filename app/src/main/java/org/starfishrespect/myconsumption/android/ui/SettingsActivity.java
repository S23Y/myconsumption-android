package org.starfishrespect.myconsumption.android.ui;
/**
 * Created by thibaud on 19.03.15.
 */
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.starfishrespect.myconsumption.android.R;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = getActionBarToolbar();
        toolbar.setTitle("MyConsumption - Settings");

        overridePendingTransition(0, 0);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        // set this to have a nav drawer associated with this activity
        return NAVDRAWER_ITEM_SETTINGS;
    }

}