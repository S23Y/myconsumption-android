package org.starfishrespect.myconsumption.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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
import org.starfishrespect.myconsumption.server.api.dto.StatsOverPeriodsDTO;

import java.util.Date;

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

    private StatsOverPeriodsDTO mStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        Toolbar toolbar = getActionBarToolbar();
        toolbar.setTitle("My Consumption - Stats");

        overridePendingTransition(0, 0);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
//        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
//        mPager.setAdapter(mPagerAdapter);

        //mTextView =  (TextView) findViewById(R.id.textViewStat);

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

//            View view = View.inflate(StatActivity.this, R.layout.fragment_sliding_stat, null);
//
//            TextView textView = (TextView) view.findViewById(R.id.textViewStat);
//
//            StatDTO stat = mStats.getStatDTOs().get(position);
//            String text = "\n\n" + Period.values()[position] + "\n"
//                    + "Consumption over period: " + stat.getConsumption() + " watts (" + w2kWh(stat.getConsumption()) +"kWh).\n"
//                    + "Average consumption: " + stat.getAverage() + " watts (" + w2kWh(stat.getAverage()) + " kWh).\n"
//                    + "Maximum value (" + timestamp2Date(stat.getMax().getTimestamp()) + ") : "
//                    + stat.getMax().getValue() + " watts (" + w2kWh(stat.getMax().getValue()) + " kWh).\n"
//                    + "Minimum value (" + timestamp2Date(stat.getMin().getTimestamp()) + ") : "
//                    + stat.getMin().getValue() + " watts (" + w2kWh(stat.getMin().getValue()) + " kWh).\n"
//                    + "Diff of consumption between last two periods: " + w2kWh(stat.getDiffLastTwo()) + " kWh.";
//
//            textView.setText(text);
//
//            container.addView(view);
//
//            return view;


            TextView textView = new TextView(StatActivity.this);
            textView.setTextSize(30);
            StatDTO stat = mStats.getStatDTOs().get(position);
            String text = "Sensor: " + mStats.getSensorId() + "\n\n" + Period.values()[position] + "\n"
                    + "Consumption over period: " + stat.getConsumption() + " watts (" + w2kWh(stat.getConsumption()) +" kWh).\n"
                    + "Average consumption: " + stat.getAverage() + " watts (" + w2kWh(stat.getAverage()) + " kWh).\n"
                    + "Maximum value (" + timestamp2Date(stat.getMax().getTimestamp()) + "): "
                    + stat.getMax().getValue() + " watts (" + w2kWh(stat.getMax().getValue()) + " kWh).\n"
                    + "Minimum value (" + timestamp2Date(stat.getMin().getTimestamp()) + "): "
                    + stat.getMin().getValue() + " watts (" + w2kWh(stat.getMin().getValue()) + " kWh).\n"
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

//        @Override
//        public Fragment getItem(int position) {
//            return new SlidingStatFragment();
//        }

        @Override
        public int getCount() {
            return mStats.getStatDTOs().size();
        }
    }

    @Override
    public void onStatUpdateFinished() {
        SingleInstance.getStatsController().loadStats();
        mStats = SingleInstance.getStatsController().getStats();

//        String text = "";
//
//        for (Period p : Period.values()) {
//            StatDTO stat = stats.getStatDTOs().get(p.getValue());
//            text += "\n\n" + p.toString() + "\n"
//                    + "Consumption over period: " + stat.getConsumption() + " watts (" + w2kWh(stat.getConsumption()) +"kWh).\n"
//                    + "Average consumption: " + stat.getAverage() + " watts (" + w2kWh(stat.getAverage()) + " kWh).\n"
//                    + "Maximum value (" + timestamp2Date(stat.getMax().getTimestamp()) + ") : "
//                    + stat.getMax().getValue() + " watts (" + w2kWh(stat.getMax().getValue()) + " kWh).\n"
//                    + "Minimum value (" + timestamp2Date(stat.getMin().getTimestamp()) + ") : "
//                    + stat.getMin().getValue() + " watts (" + w2kWh(stat.getMin().getValue()) + " kWh).\n"
//                    + "Diff of consumption between last two periods: " + w2kWh(stat.getDiffLastTwo()) + " kWh.";
//        }

        mPagerAdapter = new ViewPagerAdapter();
        mPager.setAdapter(mPagerAdapter);

//        mTextView.setText(text);
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