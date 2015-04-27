package org.starfishrespect.myconsumption.android.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.starfishrespect.myconsumption.android.Config;
import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.adapters.SensorListAdapter;
import org.starfishrespect.myconsumption.android.asynctasks.GetUserAsyncTask;
import org.starfishrespect.myconsumption.android.dao.SensorValuesDao;
import org.starfishrespect.myconsumption.android.dao.SensorValuesUpdater;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.data.UserData;
import org.starfishrespect.myconsumption.android.util.MiscFunctions;

import java.util.Date;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGD;
import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGE;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

public class ChartActivity extends BaseActivity
        implements
        SensorListAdapter.SensorChangeCallback, SensorValuesUpdater.UpdateFinishedCallback,
        GetUserAsyncTask.GetUserCallback, ChartChoiceFragment.GraphOptionChangeCallback {

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
        toolbar.setTitle("My Consumption - Chart");

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

    /**
     * Refresh data from server (?).
     */
    public void refreshData() {
        if (!MiscFunctions.isOnline(this)) {
            MiscFunctions.makeOfflineDialog(this).show();
            return;
        }

        showReloadLayout(true);
        /*PingTask.ping(Controller.getServerAddress(), new PingTask.PingResultCallback() {
            @Override
            public void pingResult(String url, boolean accessible) {
                if (accessible) {*/
        GetUserAsyncTask getUserAsyncTask = new GetUserAsyncTask(SingleInstance.getUserController().getUser().getName());
        getUserAsyncTask.setGetUserCallback(ChartActivity.this);
        getUserAsyncTask.execute();
                /*}
                else {
                    showReloadLayout(false);
                    Toast.makeText(MainActivity.this, "Cannot ping server", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
    }

    // from callback of GetUserAsyncTask
    @Override
    public void userFound(UserData user) {
        new SensorValuesDao(SingleInstance.getDatabaseHelper()).updateSensorList(user.getSensors());
        SensorValuesUpdater updater = new SensorValuesUpdater();
        updater.setUpdateFinishedCallback(this);
        updater.refreshDB();
    }

    // from callback of GetUserAsyncTask
    @Override
    public void userRetrieveError(Exception e) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_error)
                .setMessage(getString(R.string.dialog_error_update_data_error))
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
        showReloadLayout(false);
    }

    private void showReloadLayout(boolean visible) {
        if (visible) {
            findViewById(R.id.layoutGlobalReloading).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.layoutGlobalReloading).setVisibility(View.GONE);
        }
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

    @Override
    public void onUpdateFinished() {
        showReloadLayout(false);
        //SingleInstance.getUserController().loadUser(false);
        SingleInstance.getUserController().reloadUser(false);
    }
}
