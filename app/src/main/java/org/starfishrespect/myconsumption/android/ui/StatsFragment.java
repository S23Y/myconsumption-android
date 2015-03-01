package org.starfishrespect.myconsumption.android.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.controllers.StatsController;
import org.starfishrespect.myconsumption.android.dao.*;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.server.api.dto.Period;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;
import org.starfishrespect.myconsumption.server.api.dto.StatsOverPeriodsDTO;

import java.util.ArrayList;
import java.util.Date;
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
        StatsOverPeriodsDTO stats = SingleInstance.getStatsController().getStats();
        String text = "";

        for (Period p : Period.values()) {
            StatDTO stat = stats.getStatDTOs().get(p.getValue());
            text += "\n\n" + p.toString() + "\n"
                    + "Consumption over period: " + stat.getConsumption() + "watts (" + w2kWh(stat.getConsumption()) +"kWh.\n"
                    + "Average consumption: " + stat.getAverage() + " watts (" + w2kWh(stat.getAverage()) + " kWh).\n"
                    + "Maximum value (" + timestamp2Date(stat.getMax().getTimestamp()) + ") : "
                    + stat.getMax().getValue() + " watts (" + w2kWh(stat.getMax().getValue()) + " kWh).\n"
                    + "Minimum value (" + timestamp2Date(stat.getMin().getTimestamp()) + ") : "
                    + stat.getMin().getValue() + " watts (" + w2kWh(stat.getMin().getValue()) + " kWh).\n"
                    + "Diff of consumption between last two periods: " + w2kWh(stat.getDiffLastTwo()) + " kWh.";
        }

        mTextView.setText(text);
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
