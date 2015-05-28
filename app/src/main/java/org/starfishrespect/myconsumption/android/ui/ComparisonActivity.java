package org.starfishrespect.myconsumption.android.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.adapters.SpinnerSensorAdapter;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.events.ReloadStatEvent;
import org.starfishrespect.myconsumption.android.events.ReloadUserEvent;
import org.starfishrespect.myconsumption.android.util.PrefUtils;
import org.starfishrespect.myconsumption.android.util.StatUtils;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGD;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

/**
 * Activity that displays the comparison.
 *
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class ComparisonActivity extends BaseActivity implements OnChartValueSelectedListener {
    private static final String TAG = makeLogTag(ComparisonActivity.class);

    private ImageView mImageView;
    private TextView mTxtViewProfile, mTxtViewAvgCons, mTxtViewMyCons;
    private TextView mTxtViewPercent, mTxtViewUnderOver;
    private BarChart mChart;

    private Toolbar mToolbar;
    private Spinner mSpinner;
    private SpinnerSensorAdapter mSpinnerAdapter;

    private boolean mFirstStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register to the EventBus
        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_comparison);

        mToolbar = getActionBarToolbar();
        getSupportActionBar().setTitle(getString(R.string.title_comp));

        setUpActionBarSpinner();

        mImageView = (ImageView) findViewById(R.id.imageViewComp);
        mTxtViewProfile = (TextView) findViewById(R.id.txtVwProfileDescription);
        mTxtViewAvgCons = (TextView) findViewById(R.id.txtVwCompAvgCons);
        mTxtViewMyCons = (TextView) findViewById(R.id.txtVwCompMyCons);
        mTxtViewPercent = (TextView) findViewById(R.id.txtVwCompPercent);
        mTxtViewUnderOver = (TextView) findViewById(R.id.txtVwCompUnderOver);
        mChart = (BarChart) findViewById(R.id.chart1);
        initBarChart();

        populateView();

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        // Unregister to the EventBus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initBarChart() {
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDescription("");
        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);
        mChart.setDrawBarShadow(false);
        mChart.setDrawGridBackground(false);
        mChart.getXAxis().setEnabled(false);
        mChart.getAxisLeft().setEnabled(false);
        mChart.getAxisRight().setEnabled(false);
        Legend l = mChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setTextSize(12f);
    }

    private void setData(float value1, float value2) {
        ArrayList<BarEntry> yVals1 = new ArrayList<>();
        ArrayList<BarEntry> yVals2 = new ArrayList<>();
        yVals1.add(new BarEntry(value1, 0));
        yVals2.add(new BarEntry(value2, 0));

        BarDataSet set1 = new BarDataSet(yVals1, "My consumption");
        set1.setBarSpacePercent(55f);
        set1.setColor(Color.rgb(192, 255, 140));

        BarDataSet set2 = new BarDataSet(yVals2, "Average profile");
        set2.setBarSpacePercent(55f);
        set2.setColor(Color.rgb(255, 247, 140));

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);

        String[] xVals = {""};
        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(12f);

        mChart.setData(data);
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

        String sensorId = SingleInstance.getUserController().getUser().getSensors().get(SingleInstance.getSpinnerSensorPosition()).getSensorId();
        SingleInstance.getStatsController().loadStats(sensorId);

        SingleInstance.getStatsController().loadStats(sensorId);
        StatDTO stat = SingleInstance.getStatsController().getStats().get(Period.WEEK.getValue() - 1);

        double myCons = StatUtils.wh2kWh(stat.getConsumption() * 52);
        mTxtViewMyCons.setText(String.valueOf((int) myCons));

        double percent = (( myCons - profileConsumption) / profileConsumption) * 100.0;

        mTxtViewPercent.setText(String.valueOf((int) percent) + " %");
        if (percent > 0)
            mTxtViewUnderOver.setText(getString(R.string.text_comp_over));
        else
            mTxtViewUnderOver.setText(getString(R.string.text_comp_under));

        setData((float) myCons, (float) profileConsumption);
    }

    public void modifyProfile(View view) {
        // Launch settings activity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        // set this to have a nav drawer associated with this activity
        return NAVDRAWER_ITEM_COMPARISON;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        LOGD("Activity", "Selected: " + e.toString() + ", dataSet: " + dataSetIndex);
    }

    @Override
    public void onNothingSelected() {
        LOGD("Activity", "Nothing selected.");
    }

    private void setUpActionBarSpinner() {
        LOGD(TAG, "Configuring Action Bar spinner.");
        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.actionbar_spinner,
                mToolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.RIGHT;
        mToolbar.addView(spinnerContainer, lp);

        List<SensorData> sensors = SingleInstance.getUserController().getUser().getSensors();

        mSpinnerAdapter = new SpinnerSensorAdapter(ComparisonActivity.this, sensors);

        // Populate spinners
        mSpinner = (Spinner) spinnerContainer.findViewById(R.id.actionbar_spinner);

        // Apply the adapter to the spinner
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setSelection(SingleInstance.getSpinnerSensorPosition());

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {

                if (!mFirstStart) {
                    Toast.makeText(ComparisonActivity.this, "Sensor selected " + mSpinnerAdapter.getItem(position), Toast.LENGTH_SHORT).show();
                    //mSensorId = (String) mSpinnerAdapter.getItem(position);
                    SingleInstance.setSpinnerSensorPosition(position);

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }

                mFirstStart = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void openSpinner(View view) {
        mSpinner.performClick();
    }

    /**
     * Triggered when the user wants to reload data.
     * @param event A ReloadUser event
     */
    public void onEvent(ReloadUserEvent event) {
        if (event.refreshDataFromServer())
            this.refreshData();
    }

    /**
     * Triggered when the reload of stats from server is done.
     * @param event A ReloadStat event
     */
    public void onEvent(ReloadStatEvent event) {
        populateView();
    }
}
