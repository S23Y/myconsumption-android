package org.starfishrespect.myconsumption.android.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.util.IndexXYMap;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.starfishrespect.myconsumption.android.AddSensorActivity;
import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.adapters.SensorListAdapter;
import org.starfishrespect.myconsumption.android.adapters.SpinnerDateAdapter;
import org.starfishrespect.myconsumption.android.adapters.SpinnerFrequencyAdapter;
import org.starfishrespect.myconsumption.android.dao.SensorValuesDao;
import org.starfishrespect.myconsumption.android.dao.SingleInstance;
import org.starfishrespect.myconsumption.android.data.FrequencyData;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.data.SensorValue;
import org.starfishrespect.myconsumption.android.data.SensorValuePreProcessor;
import org.starfishrespect.myconsumption.android.data.SpinnerDateData;
import org.starfishrespect.myconsumption.android.misc.MiscFunctions;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thibaud on 25.02.15.
 */
public class ChartFragment extends Fragment {
    private static final String TAG = "ChartFragment";

    //private UserData user; // not needed anymore, accessed through the controller
    private ListView listViewSensor;
    private LinearLayout mLinearLayout = null;
    private TextView mTextView = null;
    private SensorListAdapter sensorListAdapter;
    private SensorListAdapter.SensorChangeCallback sensorChangeCallback;
    private GraphOptionChangeCallback graphOptionChangeCallback;
    private Spinner spinnerDate, spinnerFrequency, spinnerPrecision;
    private List<FrequencyData> frequencies;
    private List<SensorData> sensors = null;
    private SeekBar seekBar;

    private int lastLongClickItem = -1;
    public static final int REQUEST_EDIT_SENSOR = 43;
    private int seekBarPosition;

    private GraphicalView chart = null;

    /* ------------------------------------------
     * currentChartDataset & originalChartDataset
     * ------------------------------------------
     * Why keeping both of them? Because of the smoothing option given to the user, we need
     * to keep the original dataset in order to recompute the moving average if the slider is
     * modified. The currentChartDataset contains the values modified while the originalDataSet
     * contains the one that aren't.
     */
    // Dataset containing the series of point that is really displayed
    private XYMultipleSeriesDataset currentChartDataset = new XYMultipleSeriesDataset();
    // Dataset containing the series of point that is present in the db
    private XYMultipleSeriesDataset originalChartDataset = new XYMultipleSeriesDataset();


    private XYMultipleSeriesRenderer chartRenderer = new XYMultipleSeriesRenderer();
    private double minimalX = Integer.MAX_VALUE, maximalX = 0, maximalY = 0;
    private HashMap<String, ChartSerieRendererContainer> data;
    private float touchPosX = 0, touchPosY = 0;

    private int currentStart, currentDateDelay, currentValueDelay;

    private TextView textViewNoData, textViewDataSensorName, textViewDataDate, textViewDataValue;
    private View colorViewSelectedData;
    private RelativeLayout layoutPointData;
    private View refreshingView;
    private LinearLayout chartLayout;
    private boolean refreshing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart_view, container, false);
        textViewNoData = (TextView) view.findViewById(R.id.textViewNoData);
        textViewDataSensorName = (TextView) view.findViewById(R.id.textViewDataSensorName);
        textViewDataDate = (TextView) view.findViewById(R.id.textViewDataDate);
        textViewDataValue = (TextView) view.findViewById(R.id.textViewDataValue);
        refreshingView = view.findViewById(R.id.layoutChartRefresh);
        layoutPointData = (RelativeLayout) view.findViewById(R.id.layoutPointData);
        colorViewSelectedData = view.findViewById(R.id.colorViewSelectedData);
        chartLayout = (LinearLayout) view.findViewById(R.id.chartContainer);

        listViewSensor = (ListView) view.findViewById(R.id.listViewSensors);
        mLinearLayout = (LinearLayout) view.findViewById(R.id.linearLayoutDateSelectionItems);
        mTextView = (TextView) view.findViewById(R.id.textViewUsername);

        spinnerDate = (Spinner) view.findViewById(R.id.spinnerDate);
        spinnerFrequency = (Spinner) view.findViewById(R.id.spinnerFrequency);
        spinnerPrecision = (Spinner) view.findViewById(R.id.spinnerPrecision);

        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBarPosition = 0;

        // todo remove try catch
        try {
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                // Called when the slider moves to another value
                @Override
                public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                    seekBarPosition = progresValue;
                }

                // Called when you start moving the slider
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                // Called when it seems that you are done moving the slider
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    updateMovingAverage(seekBarPosition);
                }
            });

        }
        catch (Exception e) {
            System.out.println(e.toString());
        }

        listViewSensor.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                lastLongClickItem = position;
                PopupMenu popup = new PopupMenu(SingleInstance.getMainActivity(), view);
                popup.inflate(R.menu.menu_sensor_dropdown);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_delete_sensor:
                                if (!MiscFunctions.isOnline(SingleInstance.getMainActivity())) {
                                    MiscFunctions.makeOfflineDialog(SingleInstance.getMainActivity()).show();
                                    return false;
                                }
                                deleteSensor(lastLongClickItem);
                                break;
                            case R.id.action_edit_sensor:
                                if (!MiscFunctions.isOnline(SingleInstance.getMainActivity())) {
                                    MiscFunctions.makeOfflineDialog(SingleInstance.getMainActivity()).show();
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

        reset();

        return view;
    }


    // load data from the local database to a sensor
    private List<SensorValue> loadData(SensorData sensor) {
        Log.d(TAG, "loading data");
        List<SensorValue> values;
        if (currentDateDelay == -1)
            values = new SensorValuesDao(SingleInstance.getDatabaseHelper()).getValues(sensor.getSensorId(), currentStart, Integer.MAX_VALUE);
        else
            values = new SensorValuesDao(SingleInstance.getDatabaseHelper()).getValues(sensor.getSensorId(), currentStart, currentStart + currentDateDelay);

        Log.d(TAG, "processing data");
        values = SensorValuePreProcessor.fitToPrecision(values, currentValueDelay);
        return values;
    }

    // refresh the chart for the given sensor
    private void updateChartForSensor(ChartSerieRendererContainer container) {
        if (container == null)
            return;

        XYSeriesRenderer serieRenderer = null;

        // Remove former serie form dataset and renderer if present
        if (container.getSerie() != null) {
            currentChartDataset.removeSeries(container.getSerie());
            originalChartDataset.removeSeries(container.getSerie());
        }
        if (container.getRenderer() != null) {
            chartRenderer.removeSeriesRenderer(container.getRenderer());
            serieRenderer = container.getRenderer();
        }

        // If the sensor is visible and the container has values
        if (container.getSensor().isVisible() && container.values != null && container.values.size() > 0) {
            if (serieRenderer == null) {
                serieRenderer = new XYSeriesRenderer();
                serieRenderer.setPointStyle(PointStyle.CIRCLE);
            }
            serieRenderer.setColor(container.getSensor().getColor());
            final XYSeries serie = new XYSeries(container.getSensor().getSensorId());
            for (SensorValue point : container.values) {
                int x = point.getTimestamp();
                int y = point.getValue();
                if (y > maximalY) maximalY = y;
                if (x > maximalX) maximalX = x;
                if (x < minimalX) minimalX = x;
                serie.add(x, y);
            }
            double lateralDelta = maximalX - minimalX;
            lateralDelta /= 20;
            double[] limits = {minimalX - lateralDelta, maximalX + lateralDelta, 0.0, maximalY};
            chartRenderer.setPanLimits(limits);

            // Add series to the datasets
            originalChartDataset.addSeries(serie);
            currentChartDataset.addSeries(movingAverage(
                    serie,
                    SingleInstance.getFragmentController().getChartFragment().getSmoothingValue()));

            // Add series to the renderer
            chartRenderer.addSeriesRenderer(serieRenderer);

            container.setRenderer(serieRenderer);
            container.setSerie(serie);

            if (chart == null) {
                chart = ChartFactory.getLineChartView(SingleInstance.getMainActivity(), currentChartDataset, chartRenderer);
                chartLayout.addView(chart, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT));
                chart.setClickable(true);

                chart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SeriesSelection seriesSelection = chart.getCurrentSeriesAndPoint();

                        if (seriesSelection != null) {
                            for (ChartSerieRendererContainer container : data.values()) {
                                if (container.getSerie() == null) {
                                    continue;
                                }
                                if (container.values == null || container.values.size() == 0) {
                                    continue;
                                }

                                XYSeries xyserie = currentChartDataset.getSeriesAt(seriesSelection.getSeriesIndex());

                                if (container.getSensor().getSensorId().equals(xyserie.getTitle())) {
                                    int point = seriesSelection.getPointIndex();
                                    double px = seriesSelection.getXValue();
                                    long ts = (long) px;
                                    int nearest = -1;
                                    for (int i = 0; i < container.values.size(); i++) {
                                        if (ts - container.values.get(i).getTimestamp() >= 0) {
                                            nearest = i;
                                        } else {
                                            point = nearest;
                                            break;
                                        }
                                    }
                                    // correcting point, if some values on the left are not visible

                                    layoutPointData.setVisibility(View.INVISIBLE);
                                    textViewDataSensorName.setText(container.sensor.getName());
                                    textViewDataDate.setText(formatDate(new Date(((long) container.values.get(point).getTimestamp()) * 1000)));
                                    textViewDataValue.setText(container.values.get(point).getValue() + " W");
                                    colorViewSelectedData.setBackgroundColor(container.getSensor().getColor());
                                    float posX = touchPosX;
                                    float posY = touchPosY;
                                    if (posX > v.getWidth() / 2) {
                                        posX = posX - layoutPointData.getWidth() - 10;
                                    } else {
                                        posX++;
                                    }
                                    if (posY + layoutPointData.getHeight() + 10 > v.getHeight()) {
                                        posY = posY - layoutPointData.getHeight() - 10;
                                    } else {
                                        posY += 10;
                                    }
                                    layoutPointData.setX(posX);
                                    layoutPointData.setY(posY);
                                    layoutPointData.setVisibility(View.VISIBLE);
                                    break;
                                }
                            }
                        } else {
                            layoutPointData.setVisibility(View.INVISIBLE);
                        }

                    }
                });

                chart.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            touchPosX = event.getX();
                            touchPosY = event.getY();
                        }
                        return false;
                    }
                });
            }
        }
        if (chart != null) {
            chart.repaint();
        }
        if (currentChartDataset.getSeriesCount() == 0) {
            textViewNoData.setVisibility(View.VISIBLE);
        } else {
            textViewNoData.setVisibility(View.GONE);
        }
    }

    /**
     * Formats the date to a pretty display for information popup,
     * adapted with the current precision
     *
     * @param date the date
     * @return the formatted date
     */
    private String formatDate(Date date) {
        String format = "";
        switch (currentValueDelay) {
            case FrequencyData.DELAY_DAY:
                format = "EEE dd MMMM yyyy";
                break;
            case FrequencyData.DELAY_15MIN:
            case FrequencyData.DELAY_MINUTE:
            case FrequencyData.DELAY_FIVE_MINUTES:
            case FrequencyData.DELAY_HOUR:
                format = "EEE dd MMMM yyyy HH:mm";
                break;
            case FrequencyData.DELAY_MONTH:
                format = "MMMM yyyy";
                break;
            case FrequencyData.DELAY_YEAR:
                format = "yyyy";
                break;
            case FrequencyData.DELAY_WEEK:
                Date nextWeek = new Date(date.getTime() + 6000 * 86400);
                format = "EEE dd MMMM yyyy";
                SimpleDateFormat formatter = new SimpleDateFormat(format);
                return "Week from " + formatter.format(date) + " to " + formatter.format(nextWeek);

        }
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * Show all data that is marked as visible on the graph with the given interval
     *
     * @param start      start of the range to display (timestamp)
     * @param dateDelay  length of the range to display
     * @param valueDelay minimal time between two successive points. If time if longer,
     *                   multiple points will be aggregated
     */
    public void showAllGraphicsWithPrecision(int start, int dateDelay, int valueDelay) {
        if (refreshing) {
            return;
        }

        if (start == currentStart && dateDelay == currentDateDelay && valueDelay == currentValueDelay && dateDelay != -1) {
            return;
        }
        try {
            layoutPointData.setVisibility(View.GONE);
        } catch (NullPointerException e) {
            return;
        }
        init();
        currentStart = start;
        currentDateDelay = dateDelay;
        currentValueDelay = valueDelay;
        refreshingView.setVisibility(View.VISIBLE);
        textViewNoData.setVisibility(View.GONE);
        new loadDataTask().execute();
    }

    // clears the graph
    public void reset() {
        Log.d(TAG, "reset");
        currentStart = -1;
        currentDateDelay = -1;
        currentValueDelay = -1;
        init();
    }

    // initialises the graph
    private void init() {
        Log.d(TAG, "init");
        layoutPointData.setVisibility(View.GONE);
        chartLayout.removeAllViews();
        chart = null;
        data = new HashMap<>();
        try {
            List<SensorData> sensors = SingleInstance.getDatabaseHelper().getSensorDao().queryForAll();
            if (sensors.size() == 0) {
                textViewNoData.setVisibility(View.VISIBLE);
                textViewNoData.setText(R.string.chart_text_no_sensor);
                refreshingView.setVisibility(View.GONE);
            } else {
                textViewNoData.setText(R.string.chart_text_no_data);
            }
            for (SensorData sensor : sensors) {
                data.put(sensor.getSensorId(), new ChartSerieRendererContainer(sensor));
            }
        } catch (SQLException e) {

        }
        textViewNoData.setVisibility(View.VISIBLE);

        originalChartDataset = new XYMultipleSeriesDataset();
        currentChartDataset = new XYMultipleSeriesDataset();
        chartRenderer = new XYMultipleSeriesRenderer();

        chartRenderer.setApplyBackgroundColor(true);
        chartRenderer.setBackgroundColor(Color.WHITE);
        chartRenderer.setAxesColor(Color.DKGRAY);
        chartRenderer.setMarginsColor(Color.WHITE);
        chartRenderer.setGridColor(Color.LTGRAY);
        chartRenderer.setXLabelsColor(Color.DKGRAY);
        chartRenderer.setYLabelsColor(0, Color.DKGRAY);

        //chartRenderer.setZoomEnabled(true);
        //chartRenderer.setPanEnabled(true);
        chartRenderer.setZoomEnabled(true, false);
        chartRenderer.setPanEnabled(true, false);
        chartRenderer.setClickEnabled(true);

        chartRenderer.setShowGrid(true);
        chartRenderer.setXLabelFormat(new DecimalFormat() {
            @Override
            public StringBuffer format(double value, StringBuffer buffer, FieldPosition position) {
                Date dateFormat = new Date(((long) value) * 1000);
                buffer.append(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(dateFormat));
                return buffer;
            }
        });

        chartRenderer.setShowLegend(false);

        minimalX = Integer.MAX_VALUE;
        maximalX = 0;
        maximalY = 0;
    }

    /**
     * Apply a moving average on the dataset based on the smoothing option chosen by the user.
     * It updates the current data set displayed on screen based on the values given in the
     * original data set.
     * @param n period of the moving average
     */
    public void updateMovingAverage(int n) {
        if (n < 0)
            return;

        int index = 0;

        // Take into account each sensor and its associated series.
        for (XYSeries series : originalChartDataset.getSeries()) {
            XYSeries moving = movingAverage(series, n);

            currentChartDataset.removeSeries(
                    currentChartDataset.getSeriesAt(index++));
            currentChartDataset.addSeries(moving);

            if (chart != null) {
                chart.repaint();
            }
            else
                Log.d(TAG, "in method updateMovingAverage: chart is null while it shouldn't");
        }
    }

    // Computes the moving average from a given series
    private XYSeries movingAverage(XYSeries series, int N) {
        if (series == null | N < 0)
            return null;

        if (N == 0)
            return series;

        Log.d(TAG, "method movingAverage called; slider position = " + N);

        XYSeries moving = new XYSeries(series.getTitle());
        IndexXYMap<Double, Double> iXY = series.getXYMap();

        Double y_n_1 = 0.0;

        for (int i = 1; i < iXY.size(); i++) {
            Double av;

            if ((i-N) < 0)
                continue;
            else
                av = y_n_1 + ((iXY.getYByIndex(i) - iXY.getYByIndex(i - N)) / N);

            moving.add(iXY.getXByIndex(i), av);
            y_n_1 = av;
        }

        return moving;
    }

    // simple container for data series
    private class ChartSerieRendererContainer {
        XYSeries serie;
        XYSeriesRenderer renderer;
        SensorData sensor;
        List<SensorValue> values;

        private ChartSerieRendererContainer(SensorData sensor) {
            this.sensor = sensor;
        }

        public void setSerie(XYSeries serie) {
            this.serie = serie;
        }

        public void setRenderer(XYSeriesRenderer renderer) {
            this.renderer = renderer;
        }

        public XYSeries getSerie() {
            return serie;
        }

        public XYSeriesRenderer getRenderer() {
            return renderer;
        }

        public SensorData getSensor() {
            return sensor;
        }

        public void setSensor(SensorData sensor) {
            this.sensor = sensor;
        }
    }

    // shows or hide a sensor from the graph
    public void setSensorVisibility(SensorData sensor, boolean visible) {
        layoutPointData.setVisibility(View.GONE);
        ChartSerieRendererContainer container = data.get(sensor.getSensorId());
        if (container == null) {
            return;
        }
        container.getSensor().setVisible(visible);
        try {
            SingleInstance.getDatabaseHelper().getSensorDao().update(container.getSensor());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        new loadDataTask().execute(sensor);
    }

    // change the color of a sensor of the graph
    public void setSensorColor(SensorData sensor, int color) {
        layoutPointData.setVisibility(View.GONE);
        ChartSerieRendererContainer container = data.get(sensor.getSensorId());
        if (container == null) {
            return;
        }
        container.getSensor().setColor(color);
        try {
            SingleInstance.getDatabaseHelper().getSensorDao().update(container.getSensor());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        new loadDataTask().execute(sensor);
    }

    // task to load a data serie from the database, to avoid blocking
    private class loadDataTask extends AsyncTask<SensorData, ChartSerieRendererContainer, Void> {
        @Override
        protected void onPreExecute() {
            refreshing = true;
        }

        @Override
        protected Void doInBackground(SensorData... sensors) {

            if (sensors.length == 0) {
                for (ChartSerieRendererContainer container : data.values()) {
                    if (!container.getSensor().isVisible()) {
                        Log.d(TAG, "INVISIBLE");
                        publishProgress(container);
                        continue;
                    }
                    if (container.values == null) {
                        container.values = loadData(container.sensor);
                    }
                    publishProgress(container);
                }
            } else {
                for (SensorData sensor : sensors) {
                    ChartSerieRendererContainer container = data.get(sensor.getSensorId());
                    if (container == null) {
                        continue;
                    }
                    if (!container.getSensor().isVisible()) {
                        Log.d(TAG, "INVISIBLE");
                        publishProgress(container);
                        continue;
                    }
                    if (container.values == null) {
                        container.values = loadData(container.sensor);
                    }
                    publishProgress(container);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(ChartSerieRendererContainer... values) {
            for (ChartSerieRendererContainer container : values) {
                updateChartForSensor(container);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            refreshingView.setVisibility(View.GONE);
            refreshing = false;
        }
    }


    private void editSensor(int index) {
        SensorData sensor = sensors.get(index);
        Intent intent = new Intent(SingleInstance.getMainActivity(), AddSensorActivity.class);
        intent.putExtra("edit", sensor.getSensorId());
        startActivityForResult(intent, REQUEST_EDIT_SENSOR);
    }

    private void deleteSensor(int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleInstance.getMainActivity());
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
                        template.delete(SingleInstance.getServerUrl() + "user/" +
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
                                new AlertDialog.Builder(SingleInstance.getMainActivity()).setTitle(R.string.dialog_title_information)
                                        .setMessage(R.string.dialog_message_information_sensor_deleted)
                                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //SingleInstance.getUserController().loadUser(false);
                                                SingleInstance.getFragmentController().reloadUser(false);
                                                dialog.dismiss();
                                            }
                                        }).show();
                            } else {
                                new AlertDialog.Builder(SingleInstance.getMainActivity()).setTitle(R.string.dialog_title_information)
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
        try {
            sensorChangeCallback = (SensorListAdapter.SensorChangeCallback) activity;
            //graphOptionChangeCallback = (GraphOptionChangeCallback) activity;
            if (sensorListAdapter != null) {
                sensorListAdapter.setSensorChangeCallback(sensorChangeCallback);
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SensorSelectedCallback");
        }
    }

    public void setUser() {
        mTextView.setText(SingleInstance.getUserController().getUser().getName());
        sensors = SingleInstance.getUserController().getUser().getSensors();
        sensorListAdapter = new SensorListAdapter(SingleInstance.getMainActivity(), sensors);
        listViewSensor.setAdapter(sensorListAdapter);
        sensorListAdapter.setSensorChangeCallback(sensorChangeCallback);
        if (sensors.size() == 0) {
            mLinearLayout.setVisibility(View.GONE);
        }
    }

    public void refreshSpinnerFrequencies() {
        frequencies = new ArrayList<>();
        frequencies.add(new FrequencyData("Day", FrequencyData.DELAY_DAY));
        frequencies.add(new FrequencyData("Week", FrequencyData.DELAY_WEEK));
        frequencies.add(new FrequencyData("Month", FrequencyData.DELAY_MONTH));
        frequencies.add(new FrequencyData("Year", FrequencyData.DELAY_YEAR));
        frequencies.add(new FrequencyData("Everything", FrequencyData.DELAY_EVERYTHING));
        spinnerFrequency.setAdapter(new SpinnerFrequencyAdapter(SingleInstance.getMainActivity(), frequencies));
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
        spinnerPrecision.setAdapter(new SpinnerFrequencyAdapter(SingleInstance.getMainActivity(), precisionData));
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
                    graphOptionChangeCallback.dateChanged(((SpinnerDateData) spinnerDate.getSelectedItem()).getDate(), f.getDelay(), precision.getDelay());
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
        try {
            long start = Long.MAX_VALUE;
            long end = 0;
            for (SensorData sensor : SingleInstance.getDatabaseHelper().getSensorDao().queryForAll()) {
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
            spinnerDate.setAdapter(new SpinnerDateAdapter(SingleInstance.getMainActivity(), dates));
            spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    FrequencyData f = frequencies.get(spinnerFrequency.getSelectedItemPosition());
                    FrequencyData precision = (FrequencyData) spinnerPrecision.getSelectedItem();
                    if (precision != null)
                        graphOptionChangeCallback.dateChanged(dates.get(position).getDate(), f.getDelay(), precision.getDelay());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spinnerDate.setSelection(dates.size() - 1);
            mLinearLayout.setVisibility(View.VISIBLE);

        } catch (SQLException e) {

        }
    }

    public int getSmoothingValue() {
        return seekBarPosition;
    }

    public interface GraphOptionChangeCallback {
        public void dateChanged(Date newDate, int dateDelay, int valueDelay);
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
            SingleInstance.getMainActivity().refreshData();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void dateChanged(Date newDate, int dateDelay, int valueDelay) {
        if (newDate == null) {
            return;
        }
        long date = newDate.getTime() / 1000;
        showAllGraphicsWithPrecision((int) date, dateDelay, valueDelay);
    }

    public void visibilityChanged(SensorData sensor, boolean visible) {
        setSensorVisibility(sensor, sensor.isVisible());
    }

    public void colorChanged(SensorData sensor, int color) {
        setSensorColor(sensor, sensor.getColor());

    }

    public void reloadUser(boolean refreshData) {
        setUser();
        refreshSpinnerFrequencies();
        refreshSpinnerPrecision();

        if (SingleInstance.getUserController().getUser().getSensors().size() == 0) {
            refreshSpinnerDate();
            reset();
        } else if (refreshData) {
            SingleInstance.getMainActivity().refreshData();
        } else {
            refreshSpinnerDate();
            reset();
            dateChanged(getDate(), getDateDelay(), getValueDelay());
        }
    }
}