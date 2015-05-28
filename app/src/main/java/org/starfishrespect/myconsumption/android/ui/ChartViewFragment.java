package org.starfishrespect.myconsumption.android.ui;


import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.dao.SensorValuesDao;
import org.starfishrespect.myconsumption.android.data.FrequencyData;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.data.SensorValue;
import org.starfishrespect.myconsumption.android.data.SensorValuePreProcessor;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.util.IndexXYMap;
import org.starfishrespect.myconsumption.android.events.ColorChangedEvent;
import org.starfishrespect.myconsumption.android.events.DateChangedEvent;
import org.starfishrespect.myconsumption.android.events.FragmentsReadyEvent;
import org.starfishrespect.myconsumption.android.events.ReloadUserEvent;
import org.starfishrespect.myconsumption.android.events.UpdateMovingAverageEvent;
import org.starfishrespect.myconsumption.android.events.VisibilityChangedEvent;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.*;

import de.greenrobot.event.EventBus;

/**
 * Fragment that displays the chart of ChartActivity
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Adapted from Patrick by Thibaud Ledent
 */
public class ChartViewFragment extends Fragment {

    protected ChartActivity mActivity;

    private static final String TAG = "ChartViewFragment";

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
    private int seekBarPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart_view, container, false);

        // Register to the EventBus
        EventBus.getDefault().register(this);

        textViewNoData = (TextView) view.findViewById(R.id.textViewNoData);
        textViewDataSensorName = (TextView) view.findViewById(R.id.textViewDataSensorName);
        textViewDataDate = (TextView) view.findViewById(R.id.textViewDataDate);
        textViewDataValue = (TextView) view.findViewById(R.id.textViewDataValue);
        refreshingView = view.findViewById(R.id.layoutChartRefresh);
        layoutPointData = (RelativeLayout) view.findViewById(R.id.layoutPointData);
        colorViewSelectedData = view.findViewById(R.id.colorViewSelectedData);
        chartLayout = (LinearLayout) view.findViewById(R.id.chartContainer);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().post(new FragmentsReadyEvent(this.getClass(), true));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (ChartActivity) activity;
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
        // Reset and init the graph
        reset();
    }

    /**
     * Triggered when the date is changed in the spinner of ChartChoiceFragment.
     * @param event A ReloadUser event
     */
    public void onEvent(DateChangedEvent event) {
        if (event.getDate() == null) {
            return;
        }
        long date = event.getDate().getTime() / 1000;
        showAllGraphicsWithPrecision((int) date, event.getDateDelay(), event.getValueDelay());
    }

    /**
     * Triggered when the visibility is changed in ChartChoiceFragment.
     * @param event A VisibilityChangedEvent event
     */
    public void onEvent(VisibilityChangedEvent event) {
        setSensorVisibility(event.getSensor(), event.getSensor().isVisible());
    }

    /**
     * Triggered when the color is changed in ChartChoiceFragment.
     * @param event A ColorChangedEvent event
     */
    public void onEvent(ColorChangedEvent event) {
        setSensorColor(event.getSensor(), event.getSensor().getColor());
    }

    /**
     * Triggered when the moving average seekbar is changed in ChartChoiceFragment.
     * @param event A UpdateMovingAverageEvent event
     */
    public void onEvent(UpdateMovingAverageEvent event) {
        seekBarPosition = event.getSeekBarPosition();
        updateMovingAverage();
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
                    seekBarPosition));

            // Add series to the renderer
            chartRenderer.addSeriesRenderer(serieRenderer);

            container.setRenderer(serieRenderer);
            container.setSerie(serie);

            if (chart == null) {
                chart = ChartFactory.getLineChartView(mActivity, currentChartDataset, chartRenderer);
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
        new loadLocalDataTask().execute();
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
        List<SensorData> sensors = SingleInstance.getUserController().getUser().getSensors();
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
     */
    public void updateMovingAverage() {
        if (seekBarPosition < 0)
            return;

        currentChartDataset.clear();

        // Take into account each sensor and its associated series.
        for (XYSeries series : originalChartDataset.getSeries()) {
            XYSeries moving = movingAverage(series, seekBarPosition);

            currentChartDataset.addSeries(moving);
        }

        if (chart != null) {
            chart.repaint();
        }
        else
            Log.d(TAG, "in method updateMovingAverage: chart is null while it shouldn't");
    }

    // Computes the moving average from a given series
    private XYSeries movingAverage(XYSeries series, int N) {
        if (series == null | N < 0)
            return null;

        if (N == 0)
            return series;

        Log.d(TAG, "method movingAverage called; slider position = " + N);

        XYSeries moving = new XYSeries(series.getTitle());
        IndexXYMap<Double, Double> iXY = (IndexXYMap<Double, Double>) series.getXYMap().clone();

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
        new loadLocalDataTask().execute(sensor);
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
        new loadLocalDataTask().execute(sensor);
    }

    // task to load a data serie from the database, to avoid blocking
    private class loadLocalDataTask extends AsyncTask<SensorData, ChartSerieRendererContainer, Void> {
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

}