package org.starfishrespect.myconsumption.android.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.dao.DatabaseHelper;
import org.starfishrespect.myconsumption.android.dao.SensorValuesDao;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.data.SensorValue;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.starfishrespect.myconsumption.android.util.CryptoUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Update sensor values.
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */
public class SensorValuesUpdater {

    public interface UpdateFinishedCallback {
        public void onUpdateFinished();
    }

    private UpdateFinishedCallback updateFinishedCallback;
    private static final String TAG = "SensorValuesUpdater";

    public void setUpdateFinishedCallback(UpdateFinishedCallback updateFinishedCallback) {
        this.updateFinishedCallback = updateFinishedCallback;
    }

    public void refreshDB() {

        AsyncTask<Void, List, Void> task = new AsyncTask<Void, List, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DatabaseHelper db = SingleInstance.getDatabaseHelper();
                RestTemplate template = new RestTemplate();
                HttpHeaders httpHeaders = CryptoUtils.createHeadersCurrentUser();
                ResponseEntity<List> responseEnt;
                template.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

                try {
                    SensorValuesDao valuesDao = new SensorValuesDao(db);
                    for (SensorData sensor : db.getSensorDao().queryForAll()) {
                        int startTime = (int) (sensor.getLastLocalValue().getTime() / 1000);
                        String url = String.format(SingleInstance.getServerUrl() + "sensors/%s/data?start=%d", sensor.getSensorId(), startTime);
                        Log.d(TAG, url);
                        responseEnt = template.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), List.class);
                        List<List<Integer>> sensorData = responseEnt.getBody();
                        List<SensorValue> values = new ArrayList<>();
                        long last = 0;
                        long first = Long.MAX_VALUE;
                        for (List<Integer> value : sensorData) {
                            values.add(new SensorValue(value.get(0), value.get(1)));
                            if (value.get(0) > last) {
                                last = value.get(0);
                            }
                            if (value.get(0) < first) {
                                first = value.get(0);
                            }
                        }
                        valuesDao.insertSensorValues(sensor.getSensorId(), values);
                        sensor.setLastLocalValue(new Date(last * 1000));
                        long formerFirst = sensor.getFirstLocalValue().getTime() / 1000;
                        if (formerFirst > first || formerFirst == 0) {
                            sensor.setFirstLocalValue(new Date(first * 1000));
                        }
                        db.getSensorDao().update(sensor);
                        Log.d(TAG, "Inserted values to " + last);
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "Error:" + e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (updateFinishedCallback != null) {
                    updateFinishedCallback.onUpdateFinished();
                }
            }
        };

        task.execute();
    }
}
