package org.starfishrespect.myconsumption.android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.adapters.SensorListAdapter;
import org.starfishrespect.myconsumption.android.adapters.SpinnerDateAdapter;
import org.starfishrespect.myconsumption.android.adapters.SpinnerFrequencyAdapter;
import org.starfishrespect.myconsumption.android.dao.SensorValuesDao;
import org.starfishrespect.myconsumption.android.data.FrequencyData;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.data.SpinnerDateData;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.starfishrespect.myconsumption.android.events.DateChangedEvent;
import org.starfishrespect.myconsumption.android.events.FragmentsReadyEvent;
import org.starfishrespect.myconsumption.android.events.ReloadUserEvent;
import org.starfishrespect.myconsumption.android.events.UpdateMovingAverageEvent;
import org.starfishrespect.myconsumption.android.util.MiscFunctions;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import de.greenrobot.event.EventBus;

/**
 * Fragment that contains settings of the graph (which dataset to show, which range,
 * colors) and allows to edit them (color, sensor settings, delete)
 */
public class ChartChoiceFragment extends Fragment {

    //private UserData user; // not needed anymore, accessed through the controller
    private ListView listViewSensor;
    private LinearLayout mLinearLayout = null;
    private TextView mTextView = null;
    private SensorListAdapter sensorListAdapter;
    private SensorListAdapter.SensorChangeCallback sensorChangeCallback;
    private Spinner spinnerDate, spinnerFrequency, spinnerPrecision;
    private List<FrequencyData> frequencies;
    private List<SensorData> sensors = null;
    private SeekBar seekBar;

    private int lastLongClickItem = -1;
    public static final int REQUEST_EDIT_SENSOR = 43;
    private int seekBarPosition;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart_choice, container, false);

        // Register to the EventBus
        EventBus.getDefault().register(this);

        listViewSensor = (ListView) view.findViewById(R.id.listViewSensors);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.linearLayoutDateSelectionItems);
        mTextView = (TextView) view.findViewById(R.id.textViewUsername);

        spinnerDate = (Spinner) view.findViewById(R.id.spinnerDate);
        spinnerFrequency = (Spinner) view.findViewById(R.id.spinnerFrequency);
        spinnerPrecision = (Spinner) view.findViewById(R.id.spinnerPrecision);

        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBarPosition = 0;

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // Called when the slider moves to another value
            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                seekBarPosition = progresValue;
            }

            // Called when you start moving the slider
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            // Called when it seems that you are done moving the slider
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Tell ChartViewFragment to update the graph
                EventBus.getDefault().post(new UpdateMovingAverageEvent(seekBarPosition));
            }
        });

        listViewSensor.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                lastLongClickItem = position;
                PopupMenu popup = new PopupMenu(SingleInstance.getChartActivity(), view);
                popup.inflate(R.menu.menu_sensor_dropdown);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete_sensor:
                                if (!MiscFunctions.isOnline(SingleInstance.getChartActivity())) {
                                    MiscFunctions.makeOfflineDialog(SingleInstance.getChartActivity()).show();
                                    return false;
                                }
                                deleteSensor(lastLongClickItem);
                                break;
                            case R.id.action_edit_sensor:
                                if (!MiscFunctions.isOnline(SingleInstance.getChartActivity())) {
                                    MiscFunctions.makeOfflineDialog(SingleInstance.getChartActivity()).show();
                                    return false;
                                }
                                editSensor(lastLongClickItem);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
                return false;
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().post(new FragmentsReadyEvent(this.getClass(), true));
    }

    @Override
    public void onDestroy() {
        // Unregister to the EventBus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * Triggered when the user wants to reload data.
     * @param event A ReloadUser event
     */
    public void onEvent(ReloadUserEvent event) {
        setUser();
        refreshSpinnerFrequencies();
        refreshSpinnerPrecision();

        if ((SingleInstance.getUserController().getUser().getSensors().size() == 0) || event.refreshDataFromServer()) {
            refreshSpinnerDate();
        } else {
            refreshSpinnerDate();
            EventBus.getDefault().post(new DateChangedEvent(getDate(), getDateDelay(), getValueDelay()));
        }
    }

    private void editSensor(int index) {
        SensorData sensor = sensors.get(index);
        Intent intent = new Intent(SingleInstance.getChartActivity(), AddSensorActivity.class);
        intent.putExtra("edit", sensor.getSensorId());
        startActivityForResult(intent, REQUEST_EDIT_SENSOR);
    }

    private void deleteSensor(int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleInstance.getChartActivity());
        builder.setTitle(R.string.dialog_title_confirmation);
        builder.setMessage(String.format(getResources().getString(R.string.dialog_message_confirmation_delete_sensor), sensors.get(lastLongClickItem).getName()));
        builder.setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new AsyncTask<Void, Boolean, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        RestTemplate template = new RestTemplate();
                        template.getMessageConverters().add(new FormHttpMessageConverter());
                        template.getMessageConverters().add(new StringHttpMessageConverter());
                        template.delete(SingleInstance.getServerUrl() + "users/" +
                                SingleInstance.getUserController().getUser().getName() +
                                "/sensor/" +
                                sensors.get(lastLongClickItem).getSensorId());
                        try {
                            SingleInstance.getDatabaseHelper().getSensorDao().delete(sensors.get(lastLongClickItem));
                            new SensorValuesDao(SingleInstance.getDatabaseHelper()).removeSensor(sensors.get(lastLongClickItem).getSensorId());
                        } catch (SQLException e) {
                            publishProgress(false);
                        }
                        publishProgress(true);
                        return null;
                    }

                    @Override
                    protected void onProgressUpdate(Boolean... values) {
                        for (boolean b : values) {
                            if (b) {
                                new AlertDialog.Builder(SingleInstance.getChartActivity()).setTitle(R.string.dialog_title_information)
                                        .setMessage(R.string.dialog_message_information_sensor_deleted)
                                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //SingleInstance.getUserController().loadUser(false);
                                                //SingleInstance.getUserController().reloadUser(false);
                                                EventBus.getDefault().post(new ReloadUserEvent(false));
                                                dialog.dismiss();
                                            }
                                        }).show();
                            } else {
                                new AlertDialog.Builder(SingleInstance.getChartActivity()).setTitle(R.string.dialog_title_information)
                                        .setMessage("unknown error while deleting from DB")
                                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                        }
                    }
                }.execute();
                dialog.dismiss();
            }

        });
        builder.show();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        setUser();

        try {
            sensorChangeCallback = (SensorListAdapter.SensorChangeCallback) activity;
            if (sensorListAdapter != null) {
                sensorListAdapter.setSensorChangeCallback(sensorChangeCallback);
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SensorSelectedCallback");
        }
    }

    public void setUser() {
        if (mTextView != null)
            mTextView.setText(SingleInstance.getUserController().getUser().getName());

        sensors = SingleInstance.getUserController().getUser().getSensors();
        sensorListAdapter = new SensorListAdapter(SingleInstance.getChartActivity(), sensors);

        if (listViewSensor != null)
            listViewSensor.setAdapter(sensorListAdapter);

        sensorListAdapter.setSensorChangeCallback(sensorChangeCallback);
        if (sensors.size() == 0) {
            if (mLinearLayout != null)
                mLinearLayout.setVisibility(View.GONE);
        }
    }

/*    public void setUser(TextView txtView, LinearLayout lnLayout) {
        //this.user = user;
        // ((TextView) getView().findViewById(R.id.textViewUsername)).setText(user.getName());
        txtView.setText(user.getName());
        //sensors = user.getSensors();
        try {
            sensors = Controller.getDatabaseHelper().getSensorDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sensorListAdapter = new SensorListAdapter(getActivity(), sensors);
        listViewSensor.setAdapter(sensorListAdapter);
        sensorListAdapter.setSensorChangeCallback(sensorChangeCallback);
        if (sensors.size() == 0) {
            //getView().findViewById(R.id.linearLayoutDateSelectionItems).setVisibility(View.GONE);
            lnLayout.setVisibility(View.GONE);
        }
    }*/

    public void refreshSpinnerFrequencies() {
        frequencies = new ArrayList<>();
        frequencies.add(new FrequencyData("Day", FrequencyData.DELAY_DAY));
        frequencies.add(new FrequencyData("Week", FrequencyData.DELAY_WEEK));
        frequencies.add(new FrequencyData("Month", FrequencyData.DELAY_MONTH));
        frequencies.add(new FrequencyData("Year", FrequencyData.DELAY_YEAR));
        frequencies.add(new FrequencyData("Everything", FrequencyData.DELAY_EVERYTHING));
        spinnerFrequency.setAdapter(new SpinnerFrequencyAdapter(SingleInstance.getChartActivity(), frequencies));
        spinnerFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshSpinnerPrecision();
                refreshSpinnerDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void refreshSpinnerPrecision() {
        int precision = frequencies.get(spinnerFrequency.getSelectedItemPosition()).getDelay();
        ArrayList<FrequencyData> precisionData = new ArrayList<>();
        switch (precision) {
            case FrequencyData.DELAY_DAY:
                precisionData.add(new FrequencyData("Minute", FrequencyData.DELAY_MINUTE));
                precisionData.add(new FrequencyData("5 minutes", FrequencyData.DELAY_FIVE_MINUTES));
                precisionData.add(new FrequencyData("15 minutes", FrequencyData.DELAY_15MIN));
                precisionData.add(new FrequencyData("Hour", FrequencyData.DELAY_HOUR));
                break;
            case FrequencyData.DELAY_WEEK:
                precisionData.add(new FrequencyData("15 minutes", FrequencyData.DELAY_15MIN));
                precisionData.add(new FrequencyData("Hour", FrequencyData.DELAY_HOUR));
                precisionData.add(new FrequencyData("Day", FrequencyData.DELAY_DAY));
                break;
            case FrequencyData.DELAY_MONTH:
                precisionData.add(new FrequencyData("Hour", FrequencyData.DELAY_HOUR));
                precisionData.add(new FrequencyData("Day", FrequencyData.DELAY_DAY));
                break;
            case FrequencyData.DELAY_YEAR:
                precisionData.add(new FrequencyData("Day", FrequencyData.DELAY_DAY));
                precisionData.add(new FrequencyData("Week", FrequencyData.DELAY_WEEK));
                precisionData.add(new FrequencyData("Month", FrequencyData.DELAY_MONTH));
                break;
            case FrequencyData.DELAY_EVERYTHING:
                precisionData.add(new FrequencyData("Minute", FrequencyData.DELAY_MINUTE));
                precisionData.add(new FrequencyData("5 minutes", FrequencyData.DELAY_FIVE_MINUTES));
                precisionData.add(new FrequencyData("15 minutes", FrequencyData.DELAY_15MIN));
                precisionData.add(new FrequencyData("Hour", FrequencyData.DELAY_HOUR));
                precisionData.add(new FrequencyData("Day", FrequencyData.DELAY_DAY));
                precisionData.add(new FrequencyData("Week", FrequencyData.DELAY_WEEK));
                precisionData.add(new FrequencyData("Month", FrequencyData.DELAY_MONTH));
                precisionData.add(new FrequencyData("Year", FrequencyData.DELAY_YEAR));
                break;
            default:
                precisionData.add(new FrequencyData("Minute", FrequencyData.DELAY_MINUTE));
                break;
        }
        spinnerPrecision.setOnItemSelectedListener(null);
        spinnerPrecision.setAdapter(new SpinnerFrequencyAdapter(SingleInstance.getChartActivity(), precisionData));
        if (precision == FrequencyData.DELAY_EVERYTHING) {
            spinnerPrecision.setSelection(precisionData.size() - 2);
        }
        spinnerPrecision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spinnerDate.getCount() == 0) {
                    return;
                }
                FrequencyData f = frequencies.get(spinnerFrequency.getSelectedItemPosition());
                FrequencyData precision = (FrequencyData) spinnerPrecision.getSelectedItem();
                if (precision != null)
                    EventBus.getDefault().post(new DateChangedEvent(((SpinnerDateData) spinnerDate.getSelectedItem()).getDate(), f.getDelay(), precision.getDelay()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void refreshSpinnerDate() {
        if (SingleInstance.getUserController().getUser() != null
                && SingleInstance.getUserController().getUser().getSensors().size() == 0)
        {
            mLinearLayout.setVisibility(View.GONE);
            return;
        }
        long start = Long.MAX_VALUE;
        long end = 0;
        for (SensorData sensor : SingleInstance.getUserController().getUser().getSensors()) {
            if (start > sensor.getFirstLocalValue().getTime()) {
                start = sensor.getFirstLocalValue().getTime();
            }
            if (end < sensor.getLastLocalValue().getTime()) {
                end = sensor.getLastLocalValue().getTime();
            }
            start = start - start % 86400000L;
            end = end - end % 86400000L;
        }

        final List<SpinnerDateData> dates = new ArrayList<>();
        int precision = frequencies.get(spinnerFrequency.getSelectedItemPosition()).getDelay();
        SimpleDateFormat formatter = null;
        if (precision == FrequencyData.DELAY_EVERYTHING) {
            dates.add(new SpinnerDateData(new Date(0), "Everything"));
        } else if (precision == FrequencyData.DELAY_MONTH) {
            formatter = new SimpleDateFormat("MMMM yyyy");
            Calendar next = new GregorianCalendar();
            next.setTimeInMillis(start);
            next.set(Calendar.DAY_OF_MONTH, 1);
            start = next.getTimeInMillis();
        } else if (precision == FrequencyData.DELAY_YEAR) {
            formatter = new SimpleDateFormat("yyyy");
            Calendar next = new GregorianCalendar();
            next.setTimeInMillis(start);
            next.set(Calendar.DAY_OF_MONTH, 1);
            next.set(Calendar.MONTH, Calendar.JANUARY);
            start = next.getTimeInMillis();
        } else {
            formatter = new SimpleDateFormat("dd MMMM yyyy");
        }
        if (start == 0) {
            mLinearLayout.setVisibility(View.GONE);
            return;
        }
        long cur = start;
        while (cur <= end && precision != FrequencyData.DELAY_EVERYTHING) {
            Date d = new Date(cur);
            dates.add(new SpinnerDateData(d, formatter.format(d)));
            switch (precision) {
                case FrequencyData.DELAY_MONTH: {
                    Calendar next = new GregorianCalendar();
                    next.setTimeInMillis(cur);
                    next.add(Calendar.MONTH, 1);
                    cur = next.getTimeInMillis();
                    break;
                }
                case FrequencyData.DELAY_YEAR: {
                    Calendar next = new GregorianCalendar();
                    next.setTimeInMillis(cur);
                    next.add(Calendar.YEAR, 1);
                    cur = next.getTimeInMillis();
                    break;
                }
                case FrequencyData.DELAY_MINUTE:
                case FrequencyData.DELAY_DAY:
                case FrequencyData.DELAY_HOUR:
                case FrequencyData.DELAY_WEEK:
                default:
                    cur += (long) precision * 1000;
            }
        }
        spinnerDate.setOnItemSelectedListener(null);
        //spinnerDate.setAdapter(new SpinnerDateAdapter(getActivity(), dates));
        spinnerDate.setAdapter(new SpinnerDateAdapter(SingleInstance.getChartActivity(), dates));
        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FrequencyData f = frequencies.get(spinnerFrequency.getSelectedItemPosition());
                FrequencyData precision = (FrequencyData) spinnerPrecision.getSelectedItem();
                if (precision != null)
                    EventBus.getDefault().post(new DateChangedEvent(dates.get(position).getDate(), f.getDelay(), precision.getDelay()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerDate.setSelection(dates.size() - 1);
        mLinearLayout.setVisibility(View.VISIBLE);
    }

    public int getSmoothingValue() {
        return seekBarPosition;
    }

    public Date getDate() {
        if (spinnerDate.getSelectedItemPosition() < 0) {
            return null;
        }
        return ((SpinnerDateData) spinnerDate.getSelectedItem()).getDate();
    }

    public int getDateDelay() {
        FrequencyData f = frequencies.get(spinnerFrequency.getSelectedItemPosition());
        return f.getDelay();
    }

    public int getValueDelay() {
        FrequencyData f = (FrequencyData) spinnerPrecision.getSelectedItem();
        if (f != null) {
            return f.getDelay();
        } else {
            return FrequencyData.DELAY_MINUTE;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_SENSOR && resultCode == Activity.RESULT_OK) {
            SingleInstance.getChartActivity().refreshData();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}