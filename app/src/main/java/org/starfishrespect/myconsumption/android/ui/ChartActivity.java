package org.starfishrespect.myconsumption.android.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.starfishrespect.myconsumption.android.Config;
import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.events.FragmentsReadyEvent;
import org.starfishrespect.myconsumption.android.events.ReloadUserEvent;
import de.greenrobot.event.EventBus;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGD;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

/**
 * Main Activity.
 * ChartActivity is the first Activity one can see when opening the application. It is composed of
 * two fragments: ChartChoiceFragment and CharViewFragment. The first one displays the option on
 * the right while the second one contains the chart itself.
 *
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Adapted from Patrick by Thibaud Ledent
 */
public class ChartActivity extends BaseActivity {

    private static final String TAG = makeLogTag(ChartActivity.class);
    private boolean mFirstLaunchEver;
    private boolean chartChoiceReady = false;
    private boolean chartViewReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register to the EventBus
        EventBus.getDefault().register(this);

        if (getIntent().getExtras() != null) {
            mFirstLaunchEver = getIntent().getExtras().getBoolean(Config.EXTRA_FIRST_LAUNCH, false);
        }
        LOGD(TAG, "first launch " + mFirstLaunchEver);

        setContentView(R.layout.activity_chart);

        getActionBarToolbar();
        getSupportActionBar().setTitle(getString(R.string.title_chart));

        overridePendingTransition(0, 0);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        // set this to have a nav drawer associated with this activity
        return NAVDRAWER_ITEM_CHART;
    }


    @Override
    protected void onDestroy() {
        // Unregister to the EventBus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * Notify this activity that its child fragments are ready (meaning onStart() has been called).
     * @param event A Fragment Ready event
     */
    public void onEvent(FragmentsReadyEvent event) {
        if (event.getFragmentClass().getName().equals(ChartChoiceFragment.class.getName()))
            this.chartChoiceReady = true;
        if (event.getFragmentClass().getName().equals(ChartViewFragment.class.getName()))
            this.chartViewReady = true;

        if (chartChoiceReady && chartViewReady) {
            init();
            this.chartChoiceReady = false;
            this.chartViewReady = false;
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

    private void init() {
        // Reload the user
        EventBus.getDefault().post(new ReloadUserEvent(isFirstLaunchEver()));
        setFirstLaunchEver(false);
    }

    public void setFirstLaunchEver(boolean mFirstLaunchEver) {
        this.mFirstLaunchEver = mFirstLaunchEver;
    }

    public boolean isFirstLaunchEver() {
        return mFirstLaunchEver;
    }
}
