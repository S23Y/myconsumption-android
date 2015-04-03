package org.starfishrespect.myconsumption.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.dao.StatValuesUpdater;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;

import java.util.Date;
import java.util.List;

public class StatActivity extends BaseActivity
        implements StatValuesUpdater.StatUpdateFinishedCallback {

    private List<StatDTO> mStats;
    private Toolbar toolbar;
    private PagerSlidingTabStrip mTabs;
    private ViewPager mPager;

    private MyPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        toolbar = getActionBarToolbar();
        toolbar.setTitle("My Consumption - Stats");

        overridePendingTransition(0, 0);

        mPager = (ViewPager) findViewById(R.id.pager);
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

        // Fetch the data from the server
        StatValuesUpdater updater = new StatValuesUpdater();
        updater.setUpdateFinishedCallback(this);
        updater.refreshDB();


        adapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
        mTabs.setViewPager(mPager);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mPager.setPageMargin(pageMargin);
        mPager.setCurrentItem(1);


        mTabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(StatActivity.this, "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_contact:
//                QuickContactFragment.newInstance().show(getSupportFragmentManager(), "QuickContactFragment");
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"Categories", "Home", "Top Paid", "Top Free", "Top Grossing", "Top New Paid",
                "Top New Free", "Trending"};

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
            return SlidingStatFragment.newInstance(position);
        }
    }




    @Override
    public void onStatUpdateFinished() {
        SingleInstance.getStatsController().loadStats();
        mStats = SingleInstance.getStatsController().getStats();

//        mPagerAdapter = new ViewPagerAdapter();
//        mPager.setAdapter(mPagerAdapter);
    }

    public void reloadUser(boolean refreshData) {
        // todo
    }

    /**
     * Convert watt to kWh and round it up with two decimals.
     * @param watt the value you want to convert
     */
    private double w2kWh(int watt) {
        double converted = (double)watt/(60*1000);
        return Math.round(converted * 100.0) / 100.0;
    }

    /**
     * Convert a linux timestamp to a Date time.
     * @param timestamp
     * @return a Java Date.
     */
    private Date timestamp2Date(int timestamp) {
        return new java.util.Date((long) timestamp * 1000);
    }
}