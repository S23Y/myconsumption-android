package org.starfishrespect.myconsumption.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.adapters.SpinnerSensorAdapter;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.events.ReloadConfigEvent;
import org.starfishrespect.myconsumption.android.events.ReloadStatEvent;
import org.starfishrespect.myconsumption.android.events.ReloadUserEvent;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;

import java.util.List;

import de.greenrobot.event.EventBus;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGD;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

/**
 * StatActivity provides an analysis of consumption based on statistics.
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class StatActivity extends BaseActivity {
    private static final String TAG = makeLogTag(StatActivity.class);

    private Toolbar mToolbar;
    private PagerSlidingTabStrip mTabs;
    private ViewPager mPager;

    private Spinner mSpinner;
    private MyPagerAdapter mPageAdapter;
    private SpinnerSensorAdapter mSpinnerAdapter;
    private List<StatDTO> mStats;

    private boolean mFirstStart = true;
    private boolean statReloaded = false;
    private boolean configReloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register to the EventBus
        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_stat);

        mToolbar = getActionBarToolbar();
        getSupportActionBar().setTitle(getString(R.string.title_stat));

        setUpActionBarSpinner();

        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPager = (ViewPager) findViewById(R.id.pager);

        mTabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(StatActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        reloadPager();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        // Unregister to the EventBus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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

        mSpinnerAdapter = new SpinnerSensorAdapter(StatActivity.this, sensors);

        // Populate spinners
        mSpinner = (Spinner) spinnerContainer.findViewById(R.id.actionbar_spinner);

        // Apply the adapter to the spinner
        mSpinner.setAdapter(mSpinnerAdapter);
        mSpinner.setSelection(SingleInstance.getSpinnerSensorPosition());

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {

                if (!mFirstStart) {
                    Toast.makeText(StatActivity.this, "Sensor selected " + mSpinnerAdapter.getItem(position), Toast.LENGTH_SHORT).show();
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

    @Override
    protected int getSelfNavDrawerItem() {
        // set this to have a nav drawer associated with this activity
        return NAVDRAWER_ITEM_STATS;
    }

    private void reloadPager() {
        String sensorId = SingleInstance.getUserController().getUser().getSensors().get(SingleInstance.getSpinnerSensorPosition()).getSensorId();
        SingleInstance.getStatsController().loadStats(sensorId);
        mStats = SingleInstance.getStatsController().getStats();

        mPageAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPageAdapter);
        mTabs.setViewPager(mPager);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mPager.setPageMargin(pageMargin);
        mPager.setCurrentItem(0);
    }

    /**
     * Triggered when the reload of stats from server is done.
     * @param event A ReloadStat event
     */
    public void onEvent(ReloadStatEvent event) {
        statReloaded = true;

        if (configReloaded && statReloaded) {
            configReloaded = statReloaded = false;
            reloadPager();
        }
    }

    /**
     * Triggered when the reload of configs from server is done.
     * @param event A ReloadConfig event
     */
    public void onEvent(ReloadConfigEvent event) {
        configReloaded = true;

        if (configReloaded && statReloaded) {
            configReloaded = statReloaded = false;
            reloadPager();
        }
    }

    /**
     * Triggered when the user wants to reload data.
     * @param event A ReloadUser event
     */
    public void onEvent(ReloadUserEvent event) {
        if (event.refreshDataFromServer())
            this.refreshData();
    }

    public void openSpinner(View view) {
        mSpinner.performClick();
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"DAY", "WEEK", "MONTH", "YEAR", "ALL TIME"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            StatDTO stat = null;
            try {
                stat = mStats.get(position);
            } catch (Exception e) {
                LOGD(TAG, "Stat not found while trying to populate SlidingStatFragment " + e.toString());
            }

            return SlidingStatFragment.newInstance(stat);
        }
    }
}