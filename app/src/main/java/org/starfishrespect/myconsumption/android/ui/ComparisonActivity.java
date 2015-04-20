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
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.util.PrefUtils;
import org.starfishrespect.myconsumption.android.util.StatUtils;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;

import java.util.List;

public class ComparisonActivity extends BaseActivity {

    private ImageView mImageView;
    private TextView mTxtViewProfile;
    private TextView mTxtViewAvgCons;
    private TextView mTxtViewMyCons;
    private TextView mTxtViewPercent;
    private TextView mTxtViewUnderOver;
    private String mSensorId;

    static final String STATE_SENSOR = "sensorId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comparison);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null)
            mSensorId = extras.getString(STATE_SENSOR);

        Toolbar toolbar = getActionBarToolbar();
        toolbar.setTitle("MyConsumption - Comparison");

        mImageView = (ImageView) findViewById(R.id.imageViewComp);
        mTxtViewProfile = (TextView) findViewById(R.id.txtVwProfileDescription);
        mTxtViewAvgCons = (TextView) findViewById(R.id.txtVwCompAvgCons);
        mTxtViewMyCons = (TextView) findViewById(R.id.txtVwCompMyCons);
        mTxtViewPercent = (TextView) findViewById(R.id.txtVwCompPercent);
        mTxtViewUnderOver = (TextView) findViewById(R.id.txtVwCompUnderOver);

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


        double profileConsumption = PrefUtils.getProfileConsumption(this);
        mTxtViewAvgCons.setText(String.valueOf((int) profileConsumption));


        List<SensorData> sensors = SingleInstance.getUserController().getUser().getSensors();

        if (mSensorId == null)
            mSensorId = sensors.get(0).getSensorId();

        SingleInstance.getStatsController().loadStats(mSensorId);
        StatDTO stat = SingleInstance.getStatsController().getStats().get(Period.YEAR.getValue());

        double myCons = StatUtils.w2kWh(stat.getConsumption());
        mTxtViewMyCons.setText(String.valueOf((int) myCons));

        double percent = (( myCons - profileConsumption) / profileConsumption) * 100.0;

        mTxtViewPercent.setText(String.valueOf((int) percent) + " %");
        if (percent > 0)
            mTxtViewUnderOver.setText(getString(R.string.text_comp_over));
        else
            mTxtViewUnderOver.setText(getString(R.string.text_comp_under));
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