/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.starfishrespect.myconsumption.android.ui;

/**
 * Created by thibaud on 19.03.15.
 */
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.content.IntentCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.util.PrefUtils;

/**
 * Activity for customizing app settings.
 */
public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = getActionBarToolbar();
        toolbar.setTitle("MyConsumption - Settings");
        toolbar.setNavigationIcon(R.drawable.ic_up);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateUpToFromChild(SettingsActivity.this,
                        IntentCompat.makeMainActivity(new ComponentName(SettingsActivity.this,
                                ChartActivity.class)));
            }
        });

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        public SettingsFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setupSimplePreferencesScreen();
            PrefUtils.registerOnSharedPreferenceChangeListener(getActivity(), this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            PrefUtils.unregisterOnSharedPreferenceChangeListener(getActivity(), this);
        }

        private void setupSimplePreferencesScreen() {
            // Add 'general' preferences.
            addPreferencesFromResource(R.xml.preferences);
/*            if (PrefUtils.hasEnabledBle(getActivity())) {
                addPreferencesFromResource(R.xml.ble_preferences);
            }*/
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
/*            if (PrefUtils.PREF_SYNC_CALENDAR.equals(key)) {
                Intent intent;
                if (PrefUtils.shouldSyncCalendar(getActivity())) {
                    // Add all calendar entries
                    intent = new Intent(SessionCalendarService.ACTION_UPDATE_ALL_SESSIONS_CALENDAR);
                } else {
                    // Remove all calendar entries
                    intent = new Intent(SessionCalendarService.ACTION_CLEAR_ALL_SESSIONS_CALENDAR);
                }

                intent.setClass(getActivity(), SessionCalendarService.class);
                getActivity().startService(intent);
            }*/
        }
    }
}


/*
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import org.starfishrespect.myconsumption.android.R;

public class SettingsActivity extends BaseActivity {

    private int mProfileHouse = 1;
    private TextView mTextViewChoose;
    private RadioGroup mRadioGroup1;
    private Spinner mIamSpinner;
    private TextView mTextViewConsumption;
    private EditText mConsumptionEdit;
    private boolean mHouseShow;

    private final static int RESID_POS = 0;
    private final static int PRO_POS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = getActionBarToolbar();
        toolbar.setTitle("MyConsumption - Settings");

        mTextViewChoose = (TextView) findViewById(R.id.textViewChoose);
        mRadioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
        mTextViewConsumption = (TextView) findViewById(R.id.textViewConsumption);
        mConsumptionEdit = (EditText) findViewById(R.id.consumptionEdit);

        // Populate spinner
        mIamSpinner = (Spinner) findViewById(R.id.iam_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> iamAdapter = ArrayAdapter.createFromResource(this,
                R.array.iam_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        iamAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mIamSpinner.setAdapter(iamAdapter);
        mIamSpinner.setSelection(RESID_POS, false);


        final Button button = (Button) findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                //        short privacy = Somtehing.RESID;
                //        if (mIamSpinner.getSelectedItemPosition() == PRO_POS)
                //            privacy = Somtehing.PRO

                // Handle mProfileHouse

                // Take into account edit text mConsumptionEdit

                if (mHouseShow) {

                }
                else {

                }
            }
        });

        overridePendingTransition(0, 0);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        // set this to have a nav drawer associated with this activity
        return NAVDRAWER_ITEM_SETTINGS;
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_yes:
                if (checked)
                    showInputConsumption(true);
                break;
            case R.id.radio_no:
                if (checked)
                    showInputConsumption(false);
                break;

            case R.id.radio_profile_1:
                if (checked)
                    mProfileHouse = 1;
                break;
            case R.id.radio_profile_2:
                if (checked)
                    mProfileHouse = 2;
                break;
            case R.id.radio_profile_3:
                if (checked)
                    mProfileHouse = 3;
                break;
        }
    }

    private void showInputConsumption(boolean show) {
        mHouseShow = !show;
        if (show) {
            mTextViewChoose.setVisibility(View.GONE);
            mRadioGroup1.setVisibility(View.GONE);
            mTextViewConsumption.setVisibility(View.VISIBLE);
            mConsumptionEdit.setVisibility(View.VISIBLE);
        }
        else {
            mTextViewChoose.setVisibility(View.VISIBLE);
            mRadioGroup1.setVisibility(View.VISIBLE);
            mTextViewConsumption.setVisibility(View.GONE);
            mConsumptionEdit.setVisibility(View.GONE);
        }
    }
}*/
