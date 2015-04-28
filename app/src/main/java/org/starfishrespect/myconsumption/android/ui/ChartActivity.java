package org.starfishrespect.myconsumption.android.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.starfishrespect.myconsumption.android.Config;
import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.adapters.SensorListAdapter;
import org.starfishrespect.myconsumption.android.data.SensorData;

import java.util.Date;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGD;
import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGE;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

public class ChartActivity extends BaseActivity
        implements
        SensorListAdapter.SensorChangeCallback, ChartChoiceFragment.GraphOptionChangeCallback {

    private static final String TAG = makeLogTag(ChartActivity.class);
    private boolean mFirstLaunchEver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null) {
            mFirstLaunchEver = getIntent().getExtras().getBoolean(Config.EXTRA_FIRST_LAUNCH, false);
        }
        LOGD(TAG, "first launch " + mFirstLaunchEver);

        // Initialize context, database helper, user and so on...
        SingleInstance.init(this);
        SingleInstance.startNotificationService();

        setContentView(R.layout.activity_chart);

        Toolbar toolbar = getActionBarToolbar();
        getSupportActionBar().setTitle("My Consumption - Chart");

        overridePendingTransition(0, 0);
        init();
    }

    @Override
    protected int getSelfNavDrawerItem() {
        // set this to have a nav drawer associated with this activity
        return NAVDRAWER_ITEM_CHART;
    }


//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View inflatedView = inflater.inflate(R.layout.fragment_chart, container, false);
//
//        // get fragment manager
//        FragmentManager fm = getFragmentManager();
//
//        // todo: remove this
//        System.out.println("graphchoicclasss: " + GraphChoiceFragment.class.toString() + "\n"
//                + "chartviewclass: " + ChartViewFragment.class.toString());
//        // add
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.add(R.id.choice_container, new GraphChoiceFragment(), GraphChoiceFragment.class.toString());
//        ft.add(R.id.chart_container, new ChartViewFragment(), ChartViewFragment.class.toString());
//
//        ft.commit();
//
//        return inflatedView;
//    }

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

    public void init() {
        if (getChartViewFragment() != null && getGraphChoiceFragment() != null) {
            // Reload the user
            SingleInstance.getUserController().reloadUser(isFirstLaunchEver());
            setFirstLaunchEver(false);
        }
    }

    public void reloadUser(boolean refreshData) {
        ChartViewFragment chartView = getChartViewFragment();
        ChartChoiceFragment graphChoice = getGraphChoiceFragment();

        if (chartView == null || graphChoice == null)
            return;

        graphChoice.setUser();
        graphChoice.refreshSpinnerFrequencies();
        graphChoice.refreshSpinnerPrecision();

        if (SingleInstance.getUserController().getUser().getSensors().size() == 0) {
            graphChoice.refreshSpinnerDate();
            chartView.reset();
        } else if (refreshData) {
            this.refreshData();
        } else {
            graphChoice.refreshSpinnerDate();
            chartView.reset();
            dateChanged(graphChoice.getDate(), graphChoice.getDateDelay(), graphChoice.getValueDelay());
        }
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
