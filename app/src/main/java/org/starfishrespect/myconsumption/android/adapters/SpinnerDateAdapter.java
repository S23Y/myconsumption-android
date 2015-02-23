package org.starfishrespect.myconsumption.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.data.SpinnerDateData;

import java.util.List;

/**
 * Adapter used to put dates on a spinner
 */
public class SpinnerDateAdapter extends BaseAdapter {

    private List<SpinnerDateData> dates;
    private LayoutInflater inflater;

    public SpinnerDateAdapter(Context context, List<SpinnerDateData> dates) {
        this.dates = dates;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_date_spinner, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.textViewDateItem);
        tv.setText(dates.get(position).getStrValue());
        return view;
    }
}
