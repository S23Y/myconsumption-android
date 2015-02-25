package org.starfishrespect.myconsumption.android.controllers;

import org.starfishrespect.myconsumption.android.*;
import org.starfishrespect.myconsumption.android.ui.ChartFragment;

/**
 * Created by thibaud on 11.02.15.
 */
public class FragmentController {
    private ChartFragment mChartFragment;
    private StatsFragment mStatsFragment;

/*    private Fragment mCurrentFragment;*/

    public FragmentController() {}

/*    // @todo: FragmentController should keep a list of all fragments instead of one member for each fragm.
    // This list should not be build here.
    public List<Fragment> getAllFragments() {
        List<Fragment> lFrags = new ArrayList<>();

        if (mChartFragment != null)
            lFrags.add(mChartFragment);

*//*        if (mStatsFragment != null)
            lFrags.add(mStatsFragment);*//*

        return lFrags;
    }*/

    /**
     * Notify the fragments that the user has been modified so they can reload it.
     * @param refreshData if the data need to be refreshed.
     */
    public void reloadUser(boolean refreshData) {
        if (mChartFragment != null)
            mChartFragment.reloadUser(refreshData);

        if (mStatsFragment != null)
            mStatsFragment.reloadUser(refreshData);
    }

    public ChartFragment getChartFragment() {
        if (mChartFragment == null)
            mChartFragment = new ChartFragment();
        return mChartFragment;
    }

    public StatsFragment getStatsFragment() {
        if (mStatsFragment == null)
            mStatsFragment = new StatsFragment();
        return mStatsFragment;
    }
/*
    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    public void setCurrentFragment(Fragment currentFragment) {
        mCurrentFragment = currentFragment;
    }*/


/*

    public void replaceFragment(int position) {
        switch (position) {
            // Navigation
            case 1:
                replaceFragmentTransaction(getChartFragment(), position - 1);
                break;
            case 2:
                SingleInstance.getMainActivity().launchStatActivity();
                break;

            // Settings
            case 4: // action_add_sensor
                SingleInstance.getMainActivity().launchAddSensorActivity();
                break;
            case 5: // action_disconnect
                SingleInstance.getMainActivity().disconnect();
                break;

            default:
                break;
        }
    }
*/

/*
    private void replaceFragmentTransaction(Fragment fragment, int position) {
        if (!fragment.equals(getCurrentFragment())) {
            FragmentManager fragmentManager = SingleInstance.getMainActivity().getFragmentManager();
            //@TODO fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            setCurrentFragment(fragment);
        }

        // Update title, then close the drawer
        SingleInstance.getMainActivity().updateTitle(position);
    }
*/

/*    public void addFragment(Bundle extras) {
        // In case the main activity was started with special instructions from an Intent,
        // pass the Intent's extras to the fragment as arguments
        getChartFragment().setArguments(extras);


        // Add the fragment to the 'fragment_container' FrameLayout
        // @TODO SingleInstance.getMainActivity().getFragmentManager().beginTransaction().add(R.id.content_frame, getChartFragment()).commit();
        setCurrentFragment(getChartFragment());

        SingleInstance.getMainActivity().updateTitle(0);
    }*/

/*    public void destroyFragments() {
        for(Fragment f : getAllFragments())
            f.destroyFragment();

        mChartFragment = null;
        mStatsFragment = null;
        mCurrentFragment = null;
    }*/
}
