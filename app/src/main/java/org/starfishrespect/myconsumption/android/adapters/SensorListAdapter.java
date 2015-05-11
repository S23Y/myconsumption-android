package org.starfishrespect.myconsumption.android.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.ui.widget.ColorDialog;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.events.ColorChangedEvent;
import org.starfishrespect.myconsumption.android.events.VisibilityChangedEvent;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Adapter that shows the list of sensors
 */
public class SensorListAdapter extends BaseAdapter {

    private List<SensorData> sensors;
    private LayoutInflater inflater;
    private CompoundButton.OnCheckedChangeListener checkedChangeListener;

    public SensorListAdapter(Context context, List<SensorData> sensors) {
        this.sensors = sensors;
        inflater = LayoutInflater.from(context);
        checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int id = (Integer) buttonView.getTag();
                SensorData sensor = SensorListAdapter.this.sensors.get(id);
                sensor.setVisible(isChecked);
                EventBus.getDefault().post(new VisibilityChangedEvent(sensor));
            }
        };
    }

    @Override
    public int getCount() {
        return sensors.size();
    }

    @Override
    public Object getItem(int position) {
        return sensors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            view = inflater.inflate(R.layout.item_sensor_list, null);
        }
        final SensorData sensor = sensors.get(position);
        CheckBox checkBoxSensor = (CheckBox) view.findViewById(R.id.checkBox_sensor_item);

        checkBoxSensor.setOnCheckedChangeListener(null);
        checkBoxSensor.setChecked(sensor.isVisible());
        checkBoxSensor.setTag(position);
        checkBoxSensor.setOnCheckedChangeListener(checkedChangeListener);

        TextView textViewSensor = (TextView) view.findViewById(R.id.textView_sensor_item);
        textViewSensor.setText(sensor.getName());

        View colorView = view.findViewById(R.id.colorView_sensor_item);
        colorView.setBackgroundColor(sensor.getColor());
        colorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View view = v;
                ColorDialog colorDialog = new ColorDialog(v.getContext());
                colorDialog.setColor(sensor.getColor());
                colorDialog.setOnColorSelected(new ColorDialog.OnColorSelected() {
                    @Override
                    public void colorSelected(Dialog dlg, int color) {

                        sensor.setColor(color);
                        view.setBackgroundColor(color);
                        EventBus.getDefault().post(new ColorChangedEvent(sensor));

                        dlg.dismiss();
                    }
                });
                colorDialog.show();
            }
        });
        ImageView imageViewSensorWarning = (ImageView) view.findViewById(R.id.imageViewSensorWarning);
        if (sensor.isDead()) {
            imageViewSensorWarning.setVisibility(View.VISIBLE);
            imageViewSensorWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle(R.string.dialog_title_warning);
                    builder.setMessage(String.format("The sensor %s has not received values since %s. Maybe it is disconnected ?", sensor.getName(), sensor.getLastServerValue().toString()));
                    builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });
        } else {
            imageViewSensorWarning.setVisibility(View.GONE);
        }
        return view;
    }
}
