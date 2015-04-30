//package org.starfishrespect.myconsumption.android.controllers;
//
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//
//import org.starfishrespect.myconsumption.android.SingleInstance;
//import org.starfishrespect.myconsumption.android.ui.ChartFragment;
//import org.starfishrespect.myconsumption.android.ui.ChartViewFragment;
//import org.starfishrespect.myconsumption.android.ui.GraphChoiceFragment;
//
///**
//* Created by thibaud on 11.02.15.
//*/
//public class FragmentController {
///*    // @TODO remove those instances
//    private ChartFragment mChartFragment;
//    private StatsFragment mStatsFragment;*/
//
///*    private Fragment mCurrentFragment;*/
//
//    public FragmentController() {}
//
///*    // @todo: FragmentController should keep a list of all fragments instead of one member for each fragm.
//    // This list should not be build here.
//    public List<Fragment> getAllFragments() {
//        List<Fragment> lFrags = new ArrayList<>();
//
//        if (mChartFragment != null)
//            lFrags.add(mChartFragment);
//
//*//*        if (mStatsFragment != null)
//            lFrags.add(mStatsFragment);*//*
//
//        return lFrags;
//    }*/
//
//    /**
//     * Notify the fragments that the user has been modified so they can reload it.
//     * @param refreshData if the data need to be refreshed.
//     */
//    public void reloadUser(boolean refreshDataFromServer) {
//        if (SingleInstance.getChartActivity() != null)
//            SingleInstance.getChartActivity().reloadUser(refreshDataFromServer);
//
//        // TODO
////        if (getStatsFragment() != null)
////            getStatsFragment().reloadUser(refreshDataFromServer);
//    }
//
//    private Fragment getFragment(Class<?> fragmentClass) {
//        FragmentManager manager = SingleInstance.getChartActivity().getSupportFragmentManager();
//        Fragment f = manager.findFragmentByTag(fragmentClass.toString());
//        return f;
//    }
//
////    public ChartFragment getChartFragment() {
////        Fragment f = getFragment(ChartFragment.class);
////        if (f instanceof ChartFragment)
////            return (ChartFragment) f;
////        else
////            return null;
////    }
//
////    public StatsFragment getStatsFragment() {
////        Fragment f = getFragment(StatsFragment.class);
////        if (f instanceof StatsFragment)
////            return (StatsFragment) f;
////        else
////            return null;
////    }
//
//    public ChartViewFragment getChartViewFragment() {
//        Fragment f = getFragment(ChartViewFragment.class);
//        if (f instanceof ChartViewFragment)
//            return (ChartViewFragment) f;
//        else
//            return null;
//    }
//
//    public GraphChoiceFragment getGraphChoiceFragment() {
//        Fragment f = getFragment(GraphChoiceFragment.class);
//        if (f instanceof GraphChoiceFragment)
//            return (GraphChoiceFragment) f;
//        else
//            return null;
//    }
//
///*
//    public Fragment getCurrentFragment() {
//        return mCurrentFragment;
//    }
//
//    public void setCurrentFragment(Fragment currentFragment) {
//        mCurrentFragment = currentFragment;
//    }*/
//
//
///*
//
//    public void replaceFragment(int position) {
//        switch (position) {
//            // Navigation
//            case 1:
//                replaceFragmentTransaction(getChartFragment(), position - 1);
//                break;
//            case 2:
//                SingleInstance.getChartActivity().launchStatActivity();
//                break;
//
//            // Settings
//            case 4: // action_add_sensor
//                SingleInstance.getChartActivity().launchAddSensorActivity();
//                break;
//            case 5: // action_disconnect
//                SingleInstance.getChartActivity().disconnect();
//                break;
//
//            default:
//                break;
//        }
//    }
//*/
//
///*
//    private void replaceFragmentTransaction(Fragment fragment, int position) {
//        if (!fragment.equals(getCurrentFragment())) {
//            FragmentManager fragmentManager = SingleInstance.getChartActivity().getFragmentManager();
//            //@TODO fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//            setCurrentFragment(fragment);
//        }
//
//        // Update title, then close the drawer
//        SingleInstance.getChartActivity().updateTitle(position);
//    }
//*/
//
///*    public void addFragment(Bundle extras) {
//        // In case the main activity was started with special instructions from an Intent,
//        // pass the Intent's extras to the fragment as arguments
//        getChartFragment().setArguments(extras);
//
//
//        // Add the fragment to the 'fragment_container' FrameLayout
//        // @TODO SingleInstance.getChartActivity().getFragmentManager().beginTransaction().add(R.id.content_frame, getChartFragment()).commit();
//        setCurrentFragment(getChartFragment());
//
//        SingleInstance.getChartActivity().updateTitle(0);
//    }*/
//
///*    public void destroyFragments() {
//        for(Fragment f : getAllFragments())
//            f.destroyFragment();
//
//        mChartFragment = null;
//        mStatsFragment = null;
//        mCurrentFragment = null;
//    }*/
//}
