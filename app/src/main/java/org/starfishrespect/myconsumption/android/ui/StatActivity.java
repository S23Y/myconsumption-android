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
import org.starfishrespect.myconsumption.android.dao.ConfigUpdater;
import org.starfishrespect.myconsumption.android.dao.StatValuesUpdater;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;

import java.util.List;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGD;
import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGE;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

public class StatActivity extends BaseActivity
        implements StatValuesUpdater.StatUpdateFinishedCallback,
        ConfigUpdater.ConfigUpdateFinishedCallback {

    private static final String TAG = makeLogTag(StatActivity.class);

    private Toolbar mToolbar;
    private PagerSlidingTabStrip mTabs;
    private ViewPager mPager;

    private Spinner mSpinner;
    private MyPagerAdapter mPageAdapter;
    private SpinnerSensorAdapter mSpinnerAdapter;
    private List<StatDTO> mStats;

    static final String STATE_SENSOR = "sensorId";

    private String mSensorId;
    private boolean mFirstStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null)
            mSensorId = extras.getString(STATE_SENSOR);

        mToolbar = getActionBarToolbar();
        getSupportActionBar().setTitle(getString(R.string.title_stat));

        setUpActionBarSpinner();

        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPager = (ViewPager) findViewById(R.id.pager);

        // TODO only on reload
        // Fetch the data from the server
        StatValuesUpdater statUpdater = new StatValuesUpdater();
        statUpdater.setUpdateFinishedCallback(this);
        statUpdater.refreshDB();
        // TODO only on reload
        ConfigUpdater configUpdater = new ConfigUpdater();
        configUpdater.setUpdateFinishedCallback(this);
        configUpdater.refreshDB();

        mTabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(StatActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        overridePendingTransition(0, 0);
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

        if (mSensorId == null)
            mSensorId = sensors.get(0).getSensorId();

        mSpinnerAdapter = new SpinnerSensorAdapter(StatActivity.this, sensors);

        // Populate spinners
        mSpinner = (Spinner) spinnerContainer.findViewById(R.id.actionbar_spinner);

//        // Create an ArrayAdapter using the string array and a default spinner layout
//        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
//                R.array.iam_array, android.R.layout.simple_spinner_item);
//        // Specify the layout to use when the list of choices appears
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinner.setAdapter(mSpinnerAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {

                if (!mFirstStart) {
                    //onTopLevelTagSelected(mTopLevelSpinnerAdapter.getTag(position));
                    Toast.makeText(StatActivity.this, "spinner selected " + mSpinnerAdapter.getItem(position), Toast.LENGTH_SHORT).show();
                    mSensorId = (String) mSpinnerAdapter.getItem(position);
                    //onStatUpdateFinished();
                    //getWindow().getDecorView().findViewById(android.R.id.mainLayout).invalidate();

                    //Intent intent = new Intent(StatActivity.this, StatActivity.class);
                    Intent intent = getIntent();
                    intent.putExtra(STATE_SENSOR, mSensorId);
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

    public void reloadUser(boolean refreshData) {
        // todo
    }

    @Override
    public void onStatUpdateFinished() {
        SingleInstance.getStatsController().loadStats(mSensorId);
        mStats = SingleInstance.getStatsController().getStats();

        mPageAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPageAdapter);
        mTabs.setViewPager(mPager);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mPager.setPageMargin(pageMargin);
        mPager.setCurrentItem(1);
    }

    @Override
    public void onConfigUpdateFinished() {
       // todo : reload the value co2 € kwH day and € kwh night
    }

    public void openSpinner(View view) {
        mSpinner.performClick();
    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"ALL TIME", "DAY", "WEEK", "MONTH", "YEAR"};

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
            return SlidingStatFragment.newInstance(mStats.get(position));
        }
    }
}