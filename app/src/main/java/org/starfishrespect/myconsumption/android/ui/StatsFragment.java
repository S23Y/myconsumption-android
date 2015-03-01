package org.starfishrespect.myconsumption.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.dao.*;
import org.starfishrespect.myconsumption.android.data.SensorData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thibaud on 24.11.14.
 */
public class StatsFragment extends Fragment {
    private static final String TAG = "StatFragment";
    private TextView mTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        mTextView =  (TextView) view.findViewById(R.id.textView);

        return view;
    }

    public void reloadUser(boolean refreshData) {
        // todo
    }


    public void updateStat() {
        List<Integer> values = new ArrayList<>();
        for (SensorData sensor : SingleInstance.getUserController().getUser().getSensors()) {
            values = new StatValuesDao(SingleInstance.getDatabaseHelper()).getValues(sensor.getSensorId());
        }

        String text = "Average consumption (all time) : " + values.get(0) + " watts (" + w2kWh(values.get(0)) + " kWh).\n"
            + "Maximum value (all time): " + values.get(1) + " watts (" + w2kWh(values.get(1)) + " kWh).\n"
            + "Diff of consumption between yesterday and today: " + w2kWh(values.get(2)) + " kWh.";

        mTextView.setText(text);
    }

    /**
     * Convert watt to kWh and round it up with two decimals.
     * @param watt the value you want to convert
     */
    public double w2kWh(int watt) {
        double converted = (double)watt/(60*1000);
        return Math.round(converted * 100.0) / 100.0;
    }

}
