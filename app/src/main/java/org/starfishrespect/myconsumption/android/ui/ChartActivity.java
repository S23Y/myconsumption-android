package org.starfishrespect.myconsumption.android.ui;

import android.os.Bundle;
import android.widget.Toast;

import org.starfishrespect.myconsumption.android.Config;
import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.adapters.SensorListAdapter;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.events.FragmentsReadyEvent;
import org.starfishrespect.myconsumption.android.events.ReloadUserEvent;

import java.util.Date;

import de.greenrobot.event.EventBus;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGD;
import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGE;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

public class ChartActivity extends BaseActivity
        implements
        SensorListAdapter.SensorChangeCallback, ChartChoiceFragment.GraphOptionChangeCallback {

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

        // Initialize context, database helper, user and so on...
        SingleInstance.init(this);
        SingleInstance.startNotificationService();

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
            Toast.makeText(this, "Both are ready", Toast.LENGTH_SHORT).show();
            init();
        }
    }

    /**
     * Triggered when the user wants to reload data.
     * @param event A ReloadUser event
     */
    public void onEvent(ReloadUserEvent event) {
        if (event.refreshData())
            this.refreshData();
    }

    private ChartViewFragment getChartViewFragment() {
        // Get the fragment
        ChartViewFragment chartViewFragment = (ChartViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.chart_viewer);

        if (chartViewFragment == null) {
            LOGE(TAG, "ChartViewFragment not found");
            return null;
        } else {
            return chartViewFragment;
        }
    }

    private ChartChoiceFragment getGraphChoiceFragment() {
        // Get the fragment
        ChartChoiceFragment chartChoiceFragment = (ChartChoiceFragment)
                getSupportFragmentManager().findFragmentById(R.id.graph_choice);

        if (chartChoiceFragment == null) {
            LOGE(TAG, "GraphChoiceFragment not found");
            return null;
        } else {
            return chartChoiceFragment;
        }
    }

    private void init() {
            // Reload the user
            SingleInstance.getUserController().reloadUser(isFirstLaunchEver());
            setFirstLaunchEver(false);
    }



    public void updateMovingAverage(int n) {
        if (getChartViewFragment() == null)
            return;

        getChartViewFragment().updateMovingAverage(n);
    }

    public int getSmoothingValue() {
        if (getGraphChoiceFragment() == null)
            return -1;
        else
            return getGraphChoiceFragment().getSmoothingValue();
    }

    public void setFirstLaunchEver(boolean mFirstLaunchEver) {
        this.mFirstLaunchEver = mFirstLaunchEver;
    }

    public boolean isFirstLaunchEver() {
        return mFirstLaunchEver;
    }


    // from callback of GraphChoiceFragment
    @Override
    public void dateChanged(Date newDate, int dateDelay, int valueDelay) {
        if (newDate == null) {
            return;
        }
        long date = newDate.getTime() / 1000;

        ChartViewFragment chartViewFragment = getChartViewFragment();
        if (chartViewFragment != null) {
            chartViewFragment.showAllGraphicsWithPrecision((int) date, dateDelay, valueDelay);
        }
    }


    // from SensorChangeCallback
    @Override
    public void visibilityChanged(SensorData sensor) {
        ChartViewFragment chartViewFragment = getChartViewFragment();
        if (chartViewFragment != null) {
            chartViewFragment.setSensorVisibility(sensor, sensor.isVisible());
        }
    }

    // from SensorChangeCallback
    @Override
    public void colorChanged(SensorData sensor) {
        ChartViewFragment chartViewFragment = getChartViewFragment();
        if (chartViewFragment != null) {
            chartViewFragment.setSensorColor(sensor, sensor.getColor());
        }
    }
}
