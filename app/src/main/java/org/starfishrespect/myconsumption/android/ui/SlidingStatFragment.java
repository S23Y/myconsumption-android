package org.starfishrespect.myconsumption.android.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.starfishrespect.myconsumption.android.R;

/**
* Created by thibaud on 30.03.15.
*/
public class SlidingStatFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    TextView mTextView;

    private int position;

    public static SlidingStatFragment newInstance(int position) {
        SlidingStatFragment f = new SlidingStatFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sliding_stat,container,false);
        mTextView = (TextView) rootView.findViewById(R.id.textView);
        ViewCompat.setElevation(rootView, 50);

//        StatDTO stat = mStats.get(position);
//        String text = "Sensor: " + stat.getSensorId() + "\n\n" + Period.values()[position] + "\n"
//                + "Consumption over this period: " + stat.getConsumption() + " watts (" + w2kWh(stat.getConsumption()) +" kWh).\n"
//                + "Consumption over day(s) on this period: " + stat.getConsumptionDay() + " watts (" + w2kWh(stat.getConsumptionDay()) +" kWh).\n"
//                + "Consumption over night(s) on this period: " + stat.getConsumptionNight() + " watts (" + w2kWh(stat.getConsumptionNight()) +" kWh).\n"
//                + "Average consumption: " + stat.getAverage() + " watts (" + w2kWh(stat.getAverage()) + " kWh).\n"
//                + "Maximum value (" + timestamp2Date(stat.getMaxTimestamp()) + "): "
//                + stat.getMaxValue() + " watts (" + w2kWh(stat.getMaxValue()) + " kWh).\n"
//                + "Minimum value (" + timestamp2Date(stat.getMinTimestamp()) + "): "
//                + stat.getMinValue() + " watts (" + w2kWh(stat.getMinValue()) + " kWh).\n"
//                + "Diff of consumption between last two periods: " + w2kWh(stat.getDiffLastTwo()) + " kWh.";
//
//        textView.setText(text);

        mTextView.setText("CARD " + position);
        return rootView;
    }
}