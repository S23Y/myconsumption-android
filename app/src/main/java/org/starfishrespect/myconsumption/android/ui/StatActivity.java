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
import org.starfishrespect.myconsumption.android.dao.ConfigUpdater;
import org.starfishrespect.myconsumption.android.dao.StatValuesUpdater;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;

import java.util.List;

public class StatActivity extends BaseActivity
        implements StatValuesUpdater.StatUpdateFinishedCallback,
        ConfigUpdater.ConfigUpdateFinishedCallback {

    Toolbar mToolbar;
    PagerSlidingTabStrip mTabs;
    ViewPager mPager;

    private MyPagerAdapter mPageAdapter;
    private List<StatDTO> mStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);
        mToolbar = getActionBarToolbar();
        mToolbar.setTitle("MyConsumption - Statistics");
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
        SingleInstance.getStatsController().loadStats();
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
        SingleInstance.getConfigController().loadConfig();
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