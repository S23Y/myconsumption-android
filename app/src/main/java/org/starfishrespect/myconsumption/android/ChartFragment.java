package org.starfishrespect.myconsumption.android;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.starfishrespect.myconsumption.android.dao.SingleInstance;
import org.starfishrespect.myconsumption.android.data.SensorData;

import java.util.Date;

/**
 * Created by thibaud on 24.11.14.
 */
public class ChartFragment extends MainFragment {
    private ChartViewFragment mChartViewFragment = null;
    private GraphChoiceFragment mGraphChoiceFragment = null;
    private boolean chartViewFragmentReady = false;
    private boolean graphChoiceFragmentReady = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_chart, container, false);

        // get fragment manager
        FragmentManager fm = getFragmentManager();

        if (mGraphChoiceFragment == null)
            mGraphChoiceFragment = new GraphChoiceFragment();
        if (mChartViewFragment == null)
            mChartViewFragment = new ChartViewFragment();

        // add
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.choice_container, mGraphChoiceFragment);
        ft.add(R.id.chart_container, mChartViewFragment);

        ft.commit();

        return inflatedView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.destroyFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void dateChanged(Date newDate, int dateDelay, int valueDelay) {
        if (newDate == null) {
            return;
        }
        long date = newDate.getTime() / 1000;
        mChartViewFragment.showAllGraphicsWithPrecision((int) date, dateDelay, valueDelay);
    }

    public void visibilityChanged(SensorData sensor, boolean visible) {
        mChartViewFragment.setSensorVisibility(sensor, sensor.isVisible());
    }

    public void colorChanged(SensorData sensor, int color) {
        mChartViewFragment.setSensorColor(sensor, sensor.getColor());
    }

    public void fragmentsReady(Fragment fragment) {
        if (fragment.getClass().equals(ChartViewFragment.class))
            chartViewFragmentReady = true;
        else if (fragment.getClass().equals(GraphChoiceFragment.class))
            graphChoiceFragmentReady = true;

        if (chartViewFragmentReady && graphChoiceFragmentReady) {
            // @TODO mGraphChoiceFragment & mChartViewFragment ne devraient pas être nuls quand on arrive
            // ici. C'est un bug lié au chgmt d'orientation, à corriger
            if (mGraphChoiceFragment == null || mChartViewFragment == null)
                return;

            // Reload the user
            SingleInstance.getUserController().reloadUser(SingleInstance.getMainActivity().isFirstLaunchEver());

            // Reset variables
            chartViewFragmentReady = false;
            graphChoiceFragmentReady = false;
            SingleInstance.getMainActivity().setFirstLaunchEver(false);
        }
    }

    @Override
    public void destroyFragment() {
        mChartViewFragment = null;
        mGraphChoiceFragment = null;
        chartViewFragmentReady = false;
        graphChoiceFragmentReady = false;
    }

    @Override
    public void reloadUser(boolean refreshData) {
        if (mChartViewFragment == null || mGraphChoiceFragment == null)
            return;

        mGraphChoiceFragment.setUser();
        mGraphChoiceFragment.refreshSpinnerFrequencies();
        mGraphChoiceFragment.refreshSpinnerPrecision();

        if (SingleInstance.getUserController().getUser().getSensors().size() == 0) {
            mGraphChoiceFragment.refreshSpinnerDate();
            mChartViewFragment.reset();
        } else if (refreshData) {
            SingleInstance.getMainActivity().refreshData();
        } else {
            mGraphChoiceFragment.refreshSpinnerDate();
            mChartViewFragment.reset();
            dateChanged(mGraphChoiceFragment.getDate(), mGraphChoiceFragment.getDateDelay(), mGraphChoiceFragment.getValueDelay());
        }
    }

    public void updateMovingAverage(int n) {
        if (mChartViewFragment == null)
            return;

        mChartViewFragment.updateMovingAverage(n);
    }

    public int getSmoothingValue() {
        return mGraphChoiceFragment.getSmoothingValue();
    }
}