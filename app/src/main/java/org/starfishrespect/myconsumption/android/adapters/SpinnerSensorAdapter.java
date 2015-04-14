package org.starfishrespect.myconsumption.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.data.SensorData;

import java.util.List;

/**
 * Created by thibaud on 09.04.15.
 */
public class SpinnerSensorAdapter extends BaseAdapter {

    private List<SensorData> sensors;
    private LayoutInflater inflater;

    public SpinnerSensorAdapter(Context context, List<SensorData> s) {
        this.sensors = s;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return sensors.size();
    }

    @Override
    public Object getItem(int position) {
        return sensors.get(position).getSensorId();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
        }
        TextView tv = (TextView) view.findViewById(android.R.id.text1);
        tv.setText(sensors.get(position).getName());
        return view;
    }


}