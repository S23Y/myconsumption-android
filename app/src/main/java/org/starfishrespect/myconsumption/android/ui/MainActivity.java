package org.starfishrespect.myconsumption.android.ui;

import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import org.starfishrespect.myconsumption.android.GraphChoiceFragment;
import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.adapters.SensorListAdapter;
import org.starfishrespect.myconsumption.android.asynctasks.GetUserAsyncTask;
import org.starfishrespect.myconsumption.android.dao.SensorValuesUpdater;
import org.starfishrespect.myconsumption.android.dao.StatValuesUpdater;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.data.UserData;

import java.util.Date;


public class MainActivity extends ActionBarActivity
        implements
        SensorListAdapter.SensorChangeCallback, SensorValuesUpdater.UpdateFinishedCallback,
        GetUserAsyncTask.GetUserCallback, GraphChoiceFragment.GraphOptionChangeCallback,
        StatValuesUpdater.StatUpdateFinishedCallback {

    // Static
    public static final String EXTRA_FIRST_LAUNCH = "firstLaunch";
    private boolean firstLaunchEver;

    private FragmentNavigationDrawer dlDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set a ToolBar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        dlDrawer = (FragmentNavigationDrawer) findViewById(R.id.drawer_layout);
        // Setup drawer view
        dlDrawer.setupDrawerConfiguration((ListView) findViewById(R.id.lvDrawer), toolbar,
                R.layout.drawer_nav_item, R.id.flContent);
        // Add nav items
        dlDrawer.addNavItem("First", "First Fragment", FirstFragment.class);
        dlDrawer.addNavItem("Second", "Second Fragment", SecondFragment.class);
        dlDrawer.addNavItem("Third", "Third Fragment", ThirdFragment.class);
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




    @Override
    public void userFound(UserData user) {

    }

    @Override
    public void userRetrieveError(Exception e) {

    }

    @Override
    public void dateChanged(Date newDate, int dateDelay, int valueDelay) {

    }

    @Override
    public void visibilityChanged(SensorData sensor) {

    }

    @Override
    public void colorChanged(SensorData sensor) {

    }

    @Override
    public void onStatUpdateFinished() {

    }

    @Override
    public void onUpdateFinished() {

    }

    public void updateTitle(int position) {

    }

    public void launchStatActivity() {

    }

    public void launchAddSensorActivity() {

    }

    public void disconnect() {

    }

    public void refreshData() {

    }

    public void setFirstLaunchEver(boolean firstLaunchEver) {
        this.firstLaunchEver = firstLaunchEver;
    }

    public boolean isFirstLaunchEver() {
        return firstLaunchEver;
    }

    public void buildAlert() {

    }

}
