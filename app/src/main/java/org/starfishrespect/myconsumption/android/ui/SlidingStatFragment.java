package org.starfishrespect.myconsumption.android.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;

import java.util.Date;

/**
* Created by thibaud on 30.03.15.
*/
public class SlidingStatFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    TextView mTextView;

    private StatDTO mStat;
    private int position;

    public SlidingStatFragment(StatDTO stat) {
        mStat = stat;
    }

    public static SlidingStatFragment newInstance(StatDTO stat, int position) {
        SlidingStatFragment f = new SlidingStatFragment(stat);
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

        String text = "Sensor: " + mStat.getSensorId() + "\n\n" + Period.values()[position] + "\n"
                + "Consumption over this period: " + mStat.getConsumption() + " watts (" + w2kWh(mStat.getConsumption()) +" kWh).\n"
                + "Consumption over day(s) on this period: " + mStat.getConsumptionDay() + " watts (" + w2kWh(mStat.getConsumptionDay()) +" kWh).\n"
                + "Consumption over night(s) on this period: " + mStat.getConsumptionNight() + " watts (" + w2kWh(mStat.getConsumptionNight()) +" kWh).\n"
                + "Average consumption: " + mStat.getAverage() + " watts (" + w2kWh(mStat.getAverage()) + " kWh).\n"
                + "Maximum value (" + timestamp2Date(mStat.getMaxTimestamp()) + "): "
                + mStat.getMaxValue() + " watts (" + w2kWh(mStat.getMaxValue()) + " kWh).\n"
                + "Minimum value (" + timestamp2Date(mStat.getMinTimestamp()) + "): "
                + mStat.getMinValue() + " watts (" + w2kWh(mStat.getMinValue()) + " kWh).\n"
                + "Diff of consumption between last two periods: " + w2kWh(mStat.getDiffLastTwo()) + " kWh.";

        mTextView.setText(text);

        //mTextView.setText("CARD " + position);
        return rootView;
    }

    /**
     * Convert watt to kWh and round it up with two decimals.
     * @param watt the value you want to convert
     */
    private double w2kWh(int watt) {
        double converted = (double)watt/(60*1000);
        return Math.round(converted * 100.0) / 100.0;
    }

    /**
     * Convert a linux timestamp to a Date time.
     * @param timestamp
     * @return a Java Date.
     */
    private Date timestamp2Date(int timestamp) {
        return new java.util.Date((long) timestamp * 1000);
    }
}