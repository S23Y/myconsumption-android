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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_chart, container, false);

        // get fragment manager
        FragmentManager fm = getFragmentManager();

        // todo: remove this
        System.out.println("graphchoicclasss: " + GraphChoiceFragment.class.toString() + "\n"
                + "chartviewclass: " + ChartViewFragment.class.toString());
        // add
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.choice_container, new GraphChoiceFragment(), GraphChoiceFragment.class.toString());
        ft.add(R.id.chart_container, new ChartViewFragment(), ChartViewFragment.class.toString());

        ft.commit();

        return inflatedView;
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
        SingleInstance.getFragmentController().getChartViewFragment().showAllGraphicsWithPrecision((int) date, dateDelay, valueDelay);
    }

    public void visibilityChanged(SensorData sensor, boolean visible) {
        SingleInstance.getFragmentController().getChartViewFragment().setSensorVisibility(sensor, sensor.isVisible());
    }

    public void colorChanged(SensorData sensor, int color) {
        SingleInstance.getFragmentController().getChartViewFragment().setSensorColor(sensor, sensor.getColor());
    }
    
    public void fragmentsReady(Fragment fragment) {
        if (fragment.getClass().equals(ChartViewFragment.class))
            chartViewFragmentReady = true;
        else if (fragment.getClass().equals(GraphChoiceFragment.class))
            graphChoiceFragmentReady = true;

        if (chartViewFragmentReady && graphChoiceFragmentReady) {
            // Reload the user
            SingleInstance.getFragmentController().reloadUser(SingleInstance.getMainActivity().isFirstLaunchEver());

            // Reset variables
            chartViewFragmentReady = false;
            graphChoiceFragmentReady = false;
            SingleInstance.getMainActivity().setFirstLaunchEver(false);
        }
    }

    public void reloadUser(boolean refreshData) {
        ChartViewFragment chartView = SingleInstance.getFragmentController().getChartViewFragment();
        GraphChoiceFragment graphChoice = SingleInstance.getFragmentController().getGraphChoiceFragment();

        if (chartView == null || graphChoice == null)
            return;

        graphChoice.setUser();
        graphChoice.refreshSpinnerFrequencies();
        graphChoice.refreshSpinnerPrecision();

        if (SingleInstance.getUserController().getUser().getSensors().size() == 0) {
            graphChoice.refreshSpinnerDate();
            chartView.reset();
        } else if (refreshData) {
            SingleInstance.getMainActivity().refreshData();
        } else {
            graphChoice.refreshSpinnerDate();
            chartView.reset();
            dateChanged(graphChoice.getDate(), graphChoice.getDateDelay(), graphChoice.getValueDelay());
        }
    }

    public void updateMovingAverage(int n) {
        if (SingleInstance.getFragmentController().getChartViewFragment() == null)
            return;

        SingleInstance.getFragmentController().getChartViewFragment().updateMovingAverage(n);
    }

    public int getSmoothingValue() {
        return SingleInstance.getFragmentController().getGraphChoiceFragment().getSmoothingValue();
    }
}