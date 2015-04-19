package org.starfishrespect.myconsumption.android.ui;
/**
 * Created by thibaud on 19.03.15.
 */
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
        ((ArrayAdapter<CharSequence>) mIamSpinner.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mIamSpinner.setSelection(RESID_POS, false);




        final Button button = (Button) findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                //        short privacy = Somtehing.RESID;
                //        if (mIamSpinner.getSelectedItemPosition() == PRO_POS)
                //            privacy = Somtehing.PRO

                // Handle mProfileHouse

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
}