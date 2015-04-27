package org.starfishrespect.myconsumption.android.ui;


import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.util.StatUtils;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;

import java.util.ArrayList;

/**
* Created by thibaud on 30.03.15.
*/
public class SlidingStatFragment extends Fragment {

    private static final String STATDTO_KEY = "statdto_key";

    private StatDTO mStat;
    //private int position;
    private PieChart mChart;

    public static SlidingStatFragment newInstance(StatDTO stat) {
        SlidingStatFragment f = new SlidingStatFragment();
        Bundle b = new Bundle();
        //b.putInt(ARG_POSITION, position);
        b.putSerializable(STATDTO_KEY, stat);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sliding_stat,container,false);

        mStat = (StatDTO) getArguments().getSerializable(
                STATDTO_KEY);

        ViewCompat.setElevation(rootView, 50);

        // If no values found for this period, just display info for the user
        if (mStat.getAverageConsumption() == 0) {
            TextView textView = (TextView) rootView.findViewById(R.id.txtVwConsumption);
            textView.setText(getString(R.string.textview_stat_no_value_found));
            return rootView;
        }


        TextView textView = (TextView) rootView.findViewById(R.id.txtVwConsumption);
//        textView.setText(String.valueOf(StatUtils.w2kWh(mStat.getConsumption())));

        textView = (TextView) rootView.findViewById(R.id.txtVwAveragekWh);
        textView.setText(String.valueOf(StatUtils.w2kWh(mStat.getAverageConsumption())));

        textView = (TextView) rootView.findViewById(R.id.txtVwAverageWatts);
        textView.setText(String.valueOf(mStat.getAverageConsumption()));

        textView = (TextView) rootView.findViewById(R.id.txtVwMaximum);
        textView.setText(String.valueOf(mStat.getMaxValue()));

        textView = (TextView) rootView.findViewById(R.id.txtVwMaximumTimestamp);
        textView.setText(StatUtils.timestamp2DateString(mStat.getMaxTimestamp()));

        textView = (TextView) rootView.findViewById(R.id.txtVwMinimum);
        textView.setText(String.valueOf(mStat.getMinValue()));

        textView = (TextView) rootView.findViewById(R.id.txtVwMinimumTimestamp);
        textView.setText(StatUtils.timestamp2DateString(mStat.getMinTimestamp()));

//        textView = (TextView) rootView.findViewById(R.id.txtVwCO2);
//        int co2 = (int) (StatUtils.w2kWh(mStat.getConsumption()) * SingleInstance.getkWhToCO2());
//        textView.setText(String.valueOf(co2));
//
//        textView = (TextView) rootView.findViewById(R.id.txtVwEuroHigh);
//        int dayEuro = (int) (StatUtils.w2kWh(mStat.getConsumptionDay()) * SingleInstance.getkWhDayPrice());
//        textView.setText(String.valueOf(dayEuro));
//
//        textView = (TextView) rootView.findViewById(R.id.txtVwEuroOff);
//        int nightEuro = (int) (StatUtils.w2kWh(mStat.getConsumptionNight()) * SingleInstance.getkWhNightPrice());
//        textView.setText(String.valueOf(nightEuro));

//        textView = (TextView) rootView.findViewById(R.id.txtVwEuro);
//        int totEuro = dayEuro + nightEuro;
//        textView.setText(String.valueOf(totEuro));

        // Adding arrow
        ImageView imgView = (ImageView) rootView.findViewById(R.id.imageView);
        int diff = mStat.getDiffLastTwo();
        String text = "";

        if (diff > 0) {
            imgView.setImageResource(R.drawable.ic_arrow_up);
            text = "+";
        } else if (diff < 0)
            imgView.setImageResource(R.drawable.ic_arrow_down);
        else
            imgView.setImageResource(R.drawable.ic_arrow_stable);

        textView = (TextView) rootView.findViewById(R.id.txtVwComparison);
        textView.setText(text + " " + StatUtils.w2kWh(diff));


//        // Piechart
//        mChart = (PieChart) rootView.findViewById(R.id.pieChart1);
//        mChart.setDescription("");
//
//        // radius of the center hole in percent of maximum radius
//        mChart.setHoleRadius(45f);
//        mChart.setTransparentCircleRadius(50f);
//
//        Legend l = mChart.getLegend();
//        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
//
//        mChart.setData(generatePieData());

        return rootView;
    }

//    protected PieData generatePieData() {
//        ArrayList<Entry> entries1 = new ArrayList<Entry>();
//        ArrayList<String> xVals = new ArrayList<String>();
//
//        xVals.add("PEAK TIME: " + StatUtils.w2kWh(mStat.getConsumptionDay()) + " " + getString(R.string.textview_stat_kWh));
//        xVals.add("OFF-PEAK TIME: " + StatUtils.w2kWh(mStat.getConsumptionNight()) + " " + getString(R.string.textview_stat_kWh));
//
//        //xVals.add("entry" + (1));
//        //xVals.add("entry" + (2));
//        entries1.add(new Entry((float) StatUtils.w2kWh(mStat.getConsumptionDay()), 0));
//        entries1.add(new Entry((float) StatUtils.w2kWh(mStat.getConsumptionNight()), 1));
//
//
//        PieDataSet ds1 = new PieDataSet(entries1, "");
//        ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
//        ds1.setSliceSpace(2f);
//        ds1.setValueTextColor(Color.BLACK);
//        ds1.setValueTextSize(12f);
//
//        PieData d = new PieData(xVals, ds1);
//        d.setValueTypeface(Typeface.DEFAULT);
//
//        return d;
//    }
}