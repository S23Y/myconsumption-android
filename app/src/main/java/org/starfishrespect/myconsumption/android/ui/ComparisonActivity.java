package org.starfishrespect.myconsumption.android.ui;
/**
 * Created by thibaud on 19.03.15.
 */
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.util.PrefUtils;

public class ComparisonActivity extends BaseActivity {

    private ImageView mImageView;
    private TextView mTxtViewProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        Toolbar toolbar = getActionBarToolbar();
        toolbar.setTitle("MyConsumption - Comparison");

        mImageView = (ImageView) findViewById(R.id.imageViewComp);
        mTxtViewProfile = (TextView) findViewById(R.id.txtVwProfileDescription);

        populateView();

        overridePendingTransition(0, 0);
    }

    private void populateView() {
        String textProfile = PrefUtils.getProfileTextDescription(this);

        if (!textProfile.isEmpty()) mTxtViewProfile.setText(textProfile);

        switch (PrefUtils.getProfileIndex(this)) {
            case 0:
                mImageView.setImageDrawable(getResources().getDrawable(R.drawable.consumption_profile_0));
                break;
            case 1:
                mImageView.setImageDrawable(getResources().getDrawable(R.drawable.consumption_profile_1));
                break;
            case 2:
                mImageView.setImageDrawable(getResources().getDrawable(R.drawable.consumption_profile_2));
                break;
        }
    }

    @Override
    protected int getSelfNavDrawerItem() {
        // set this to have a nav drawer associated with this activity
        return NAVDRAWER_ITEM_COMPARISON;
    }

    public void modifyProfile(View view) {
        // Launch settings activity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        // finish() ?
    }
}