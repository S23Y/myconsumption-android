package org.starfishrespect.myconsumption.android.ui;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.dao.StatValuesUpdater;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;

import java.util.Date;
import java.util.List;

public class StatActivity extends BaseActivity
        implements StatValuesUpdater.StatUpdateFinishedCallback {

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    private List<StatDTO> mStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        Toolbar toolbar = getActionBarToolbar();
        toolbar.setTitle("My Consumption - Stats");

        overridePendingTransition(0, 0);

        mPager = (ViewPager) findViewById(R.id.pager);

        // Fetch the data from the server
        StatValuesUpdater updater = new StatValuesUpdater();
        updater.setUpdateFinishedCallback(this);
        updater.refreshDB();
    }

    @Override
    protected int getSelfNavDrawerItem() {
        // set this to have a nav drawer associated with this activity
        return NAVDRAWER_ITEM_STATS;
    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TextView textView = new TextView(StatActivity.this);
            textView.setTextSize(30);
            StatDTO stat = mStats.get(position);
            String text = "Sensor: " + stat.getSensorId() + "\n\n" + Period.values()[position] + "\n"
                    + "Consumption over this period: " + stat.getConsumption() + " watts (" + w2kWh(stat.getConsumption()) +" kWh).\n"
                    + "Consumption over day(s) on this period: " + stat.getConsumptionDay() + " watts (" + w2kWh(stat.getConsumptionDay()) +" kWh).\n"
                    + "Consumption over night(s) on this period: " + stat.getConsumptionNight() + " watts (" + w2kWh(stat.getConsumptionNight()) +" kWh).\n"
                    + "Average consumption: " + stat.getAverage() + " watts (" + w2kWh(stat.getAverage()) + " kWh).\n"
                    + "Maximum value (" + timestamp2Date(stat.getMaxTimestamp()) + "): "
                    + stat.getMaxValue() + " watts (" + w2kWh(stat.getMaxValue()) + " kWh).\n"
                    + "Minimum value (" + timestamp2Date(stat.getMinTimestamp()) + "): "
                    + stat.getMinValue() + " watts (" + w2kWh(stat.getMinValue()) + " kWh).\n"
                    + "Diff of consumption between last two periods: " + w2kWh(stat.getDiffLastTwo()) + " kWh.";

            textView.setText(text);

            LinearLayout layout = new LinearLayout(StatActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            LayoutParams layoutParams = new LayoutParams(
                    LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
            layout.setLayoutParams(layoutParams);
            layout.addView(textView);

            container.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mStats.size();
        }
    }

    @Override
    public void onStatUpdateFinished() {
        SingleInstance.getStatsController().loadStats();
        mStats = SingleInstance.getStatsController().getStats();

        mPagerAdapter = new ViewPagerAdapter();
        mPager.setAdapter(mPagerAdapter);
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