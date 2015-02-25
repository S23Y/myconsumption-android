package org.starfishrespect.myconsumption.android.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import org.starfishrespect.myconsumption.android.AddSensorActivity;
import org.starfishrespect.myconsumption.android.LoginActivity;
import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.adapters.SensorListAdapter;
import org.starfishrespect.myconsumption.android.asynctasks.GetUserAsyncTask;
import org.starfishrespect.myconsumption.android.dao.SensorValuesDao;
import org.starfishrespect.myconsumption.android.dao.SensorValuesUpdater;
import org.starfishrespect.myconsumption.android.dao.SingleInstance;
import org.starfishrespect.myconsumption.android.dao.StatValuesUpdater;
import org.starfishrespect.myconsumption.android.data.KeyValueData;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.data.UserData;
import org.starfishrespect.myconsumption.android.misc.MiscFunctions;

import java.sql.SQLException;
import java.util.Date;


public class MainActivity extends ActionBarActivity
        implements
        SensorListAdapter.SensorChangeCallback, SensorValuesUpdater.UpdateFinishedCallback,
        GetUserAsyncTask.GetUserCallback,
        StatValuesUpdater.StatUpdateFinishedCallback {

    // Static
    public static final String EXTRA_FIRST_LAUNCH = "firstLaunch";
    private static final String TAG = "Main";
    public static final int REQUEST_ADD_SENSOR = 42;

    private boolean mFirstLaunchEver;

    private FragmentNavigationDrawer dlDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SingleInstance.setMainActivity(this);
        SingleInstance.getDatabaseHelper();

        if (getIntent().getExtras() != null) {
            mFirstLaunchEver = getIntent().getExtras().getBoolean(EXTRA_FIRST_LAUNCH, false);
        }
        Log.d(TAG, "first launch " + mFirstLaunchEver);

        // Load the user
        SingleInstance.getUserController().loadUser();

        // Set a Toolbar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        dlDrawer = (FragmentNavigationDrawer) findViewById(R.id.drawer_layout);
        // Setup drawer view
        dlDrawer.setupDrawerConfiguration((ListView) findViewById(R.id.lvDrawer), toolbar,
                R.layout.drawer_nav_item, R.id.flContent);
        // Add nav items
        dlDrawer.addNavItem("Chart", R.drawable.ic_chart, "MyConsumption - Chart", ChartFragment.class);
        dlDrawer.addNavItem("Second", R.drawable.ic_stat, "Second Fragment", SecondFragment.class);
        dlDrawer.addNavItem("Third", R.drawable.ic_add, "Third Fragment", ThirdFragment.class);
        dlDrawer.addNavItem("Fourth", R.drawable.ic_disconnect, "Fourth Fragment", FourthFragment.class);
        // Select default
        if (savedInstanceState == null) {
            dlDrawer.selectDrawerItem(0);
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content
        if (dlDrawer.isDrawerOpen()) {
            // Uncomment to hide menu items
            // menu.findItem(R.id.mi_test).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Uncomment to inflate menu items to Action Bar
        // inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (dlDrawer.getDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        dlDrawer.getDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        dlDrawer.getDrawerToggle().onConfigurationChanged(newConfig);
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
        // todo
/*        if (visible) {
            findViewById(R.id.layoutGlobalReloading).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.layoutGlobalReloading).setVisibility(View.GONE);
        }*/
    }


/*    // from callback of GraphChoiceFragment
    @Override
    public void dateChanged(Date newDate, int dateDelay, int valueDelay) {
        SingleInstance.getFragmentController().getChartFragment().dateChanged(newDate, dateDelay, valueDelay);
    }*/


    // from SensorChangeCallback
    @Override
    public void visibilityChanged(SensorData sensor) {
        SingleInstance.getFragmentController().getChartFragment().visibilityChanged(sensor, sensor.isVisible());
    }

    // from SensorChangeCallback
    @Override
    public void colorChanged(SensorData sensor) {
        SingleInstance.getFragmentController().getChartFragment().colorChanged(sensor, sensor.getColor());
    }

    @Override
    public void onStatUpdateFinished() {
        if (SingleInstance.getFragmentController().getStatsFragment() == null)
            return;
        SingleInstance.getFragmentController().getStatsFragment().updateStat();
    }

    @Override
    public void onUpdateFinished() {
        showReloadLayout(false);
        //SingleInstance.getUserController().loadUser(false);
        SingleInstance.getFragmentController().reloadUser(false);
    }

    public void launchAddSensorActivity() {
        if (!MiscFunctions.isOnline(SingleInstance.getMainActivity())) {
            MiscFunctions.makeOfflineDialog(this).show();
            return;
        }
        startActivityForResult(new Intent(this, AddSensorActivity.class), REQUEST_ADD_SENSOR);
    }

    // removes all the data of the current user and go back to the login
    public void disconnect() {
        KeyValueData userKey = SingleInstance.getDatabaseHelper().getValueForKey("user");
        try {
            if (userKey != null) {
                SingleInstance.getDatabaseHelper().getKeyValueDao().delete(userKey);
            }
            SensorValuesDao sensorValuesDao = new SensorValuesDao(SingleInstance.getDatabaseHelper());
            for (SensorData s : SingleInstance.getDatabaseHelper().getSensorDao().queryForAll()) {
                sensorValuesDao.removeSensor(s.getSensorId());
            }
            SingleInstance.getDatabaseHelper().clearTable("sensors");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void refreshData() {

    }

    public void setFirstLaunchEver(boolean mFirstLaunchEver) {
        this.mFirstLaunchEver = mFirstLaunchEver;
    }

    public boolean isFirstLaunchEver() {
        return mFirstLaunchEver;
    }

    public void buildAlert() {

    }

}