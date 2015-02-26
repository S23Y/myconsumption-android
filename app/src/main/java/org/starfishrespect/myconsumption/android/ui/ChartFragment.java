package org.starfishrespect.myconsumption.android.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.dao.SingleInstance;
import org.starfishrespect.myconsumption.android.data.SensorData;

import java.util.Date;

/**
 * Created by thibaud on 24.11.14.
 */
public class ChartFragment extends Fragment {
    private boolean chartViewFragmentReady = false;
    private boolean graphChoiceFragmentReady = false;

    public static final String TAG_CHOICE = "GRAPH_CHOICE_FRAGMENT";
    public static final String TAG_CHART = "CHART_VIEW_FRAGMENT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_chart, container, false);

        // get fragment manager
        FragmentManager fm = getFragmentManager();

        // add
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.choice_container, new GraphChoiceFragment(), TAG_CHOICE);
        ft.add(R.id.chart_container, new ChartViewFragment(), TAG_CHART);

        ft.commit();

        return inflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private ChartViewFragment getChartViewFragment() {
        FragmentManager manager = getChildFragmentManager();
        Fragment f = manager.findFragmentByTag(TAG_CHART);
        if (f instanceof ChartViewFragment)
            return (ChartViewFragment) f;
        else
            return null;
    }

    private GraphChoiceFragment getGraphChoiceFragment() {
        FragmentManager manager = getChildFragmentManager();
        Fragment f = manager.findFragmentByTag(TAG_CHOICE);
        if (f instanceof GraphChoiceFragment)
            return (GraphChoiceFragment) f;
        else
            return null;
    }

    public void dateChanged(Date newDate, int dateDelay, int valueDelay) {
        if (newDate == null) {
            return;
        }
        long date = newDate.getTime() / 1000;
        getChartViewFragment().showAllGraphicsWithPrecision((int) date, dateDelay, valueDelay);
    }

    public void visibilityChanged(SensorData sensor, boolean visible) {
        getChartViewFragment().setSensorVisibility(sensor, sensor.isVisible());
    }

    public void colorChanged(SensorData sensor, int color) {
        getChartViewFragment().setSensorColor(sensor, sensor.getColor());
    }
    
    public void fragmentsReady(Fragment fragment) {
        if (fragment.getClass().equals(ChartViewFragment.class))
            chartViewFragmentReady = true;
        else if (fragment.getClass().equals(GraphChoiceFragment.class))
            graphChoiceFragmentReady = true;

        if (chartViewFragmentReady && graphChoiceFragmentReady) {
            SingleInstance.getMainActivity().test();
            // @TODO getGraphChoiceFragment() & getChartViewFragment() ne devraient pas être nuls quand on arrive
            // ici. C'est un bug lié au chgmt d'orientation, à corriger. Corrigé ?
            if (getGraphChoiceFragment() == null || getChartViewFragment() == null)
                return;

            // Reload the user
            SingleInstance.getFragmentController().reloadUser(SingleInstance.getMainActivity().isFirstLaunchEver());

            // Reset variables
            chartViewFragmentReady = false;
            graphChoiceFragmentReady = false;
            SingleInstance.getMainActivity().setFirstLaunchEver(false);
        }
    }

    public void reloadUser(boolean refreshData) {
        if (getChartViewFragment() == null || getGraphChoiceFragment() == null)
            return;

        getGraphChoiceFragment().setUser();
        getGraphChoiceFragment().refreshSpinnerFrequencies();
        getGraphChoiceFragment().refreshSpinnerPrecision();

        if (SingleInstance.getUserController().getUser().getSensors().size() == 0) {
            getGraphChoiceFragment().refreshSpinnerDate();
            getChartViewFragment().reset();
        } else if (refreshData) {
            SingleInstance.getMainActivity().refreshData();
        } else {
            getGraphChoiceFragment().refreshSpinnerDate();
            getChartViewFragment().reset();
            dateChanged(getGraphChoiceFragment().getDate(), getGraphChoiceFragment().getDateDelay(), getGraphChoiceFragment().getValueDelay());
        }
    }

    public void updateMovingAverage(int n) {
        if (getChartViewFragment() == null)
            return;

        getChartViewFragment().updateMovingAverage(n);
    }

    public int getSmoothingValue() {
        return getGraphChoiceFragment().getSmoothingValue();
    }
}