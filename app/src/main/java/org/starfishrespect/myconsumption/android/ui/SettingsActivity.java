package org.starfishrespect.myconsumption.android.ui;
/**
 * Created by thibaud on 19.03.15.
 */
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import org.starfishrespect.myconsumption.android.R;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = getActionBarToolbar();
        toolbar.setTitle("MyConsumption - Settings");

        // Populate spinners
        Spinner iamSpinner = (Spinner) findViewById(R.id.iam_spinner);
        Spinner simSpinner = (Spinner) findViewById(R.id.sim_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> iamAdapter = ArrayAdapter.createFromResource(this,
                R.array.iam_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        iamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        iamSpinner.setAdapter(iamAdapter);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> simAdapter = ArrayAdapter.createFromResource(this,
                R.array.sim_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        simAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        simSpinner.setAdapter(simAdapter);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.consumption_profile);

        overridePendingTransition(0, 0);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        // set this to have a nav drawer associated with this activity
        return NAVDRAWER_ITEM_SETTINGS;
    }

}