package org.starfishrespect.myconsumption.android.tasks;

import android.os.AsyncTask;

import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.dao.DatabaseHelper;
import org.starfishrespect.myconsumption.android.data.KeyValueData;

import java.sql.SQLException;
import java.util.List;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGD;
import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGE;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

/**
 * Get basic config values from the server using an AsyncTask
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class ConfigUpdater {

    public interface ConfigUpdateFinishedCallback {
        public void onConfigUpdateFinished();
    }

    private ConfigUpdateFinishedCallback configUpdateFinishedCallback;
    private static final String TAG = makeLogTag(StatValuesUpdater.class);

    public void setUpdateFinishedCallback(ConfigUpdateFinishedCallback updateFinishedCallback) {
        this.configUpdateFinishedCallback = updateFinishedCallback;
    }

    public void refreshDB() {

        AsyncTask<Void, List, Void> task = new AsyncTask<Void, List, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DatabaseHelper db = SingleInstance.getDatabaseHelper();
                RestTemplate template = new RestTemplate();
                template.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

                try {
                    String url = SingleInstance.getServerUrl() + "configs/co2";
                    Double co2 = template.getForObject(url, Double.class);

                    int id = db.getIdForKey("config_co2");
                    KeyValueData valueData = new KeyValueData("config_co2", co2.toString());
                    valueData.setId(id);

                    LOGD(TAG, "writing stat in local db: " + co2);
                    db.getKeyValueDao().createOrUpdate(valueData);

                } catch (SQLException e) {
                    LOGD(TAG, "Cannot create config co2", e);
                } catch (ResourceAccessException | HttpClientErrorException e) {
                    LOGE(TAG, "Cannot access server ", e);
                }

                try {
                    String url = SingleInstance.getServerUrl() + "configs/day";
                    Double day = template.getForObject(url, Double.class);

                    int id = db.getIdForKey("config_day");
                    KeyValueData valueData = new KeyValueData("config_day", day.toString());
                    valueData.setId(id);

                    LOGD(TAG, "writing stat in local db: " + day);
                    db.getKeyValueDao().createOrUpdate(valueData);

                } catch (SQLException e) {
                    LOGD(TAG, "Cannot create config day", e);
                } catch (ResourceAccessException | HttpClientErrorException e) {
                    LOGE(TAG, "Cannot access server ", e);
                }

                try {
                    String url = SingleInstance.getServerUrl() + "configs/night";
                    Double night = template.getForObject(url, Double.class);

                    int id = db.getIdForKey("config_night");
                    KeyValueData valueData = new KeyValueData("config_night", night.toString());
                    valueData.setId(id);

                    LOGD(TAG, "writing stat in local db: " + night);
                    db.getKeyValueDao().createOrUpdate(valueData);

                } catch (SQLException e) {
                    LOGD(TAG, "Cannot create config night", e);
                } catch (ResourceAccessException | HttpClientErrorException e) {
                    LOGE(TAG, "Cannot access server ", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (configUpdateFinishedCallback != null) {
                    configUpdateFinishedCallback.onConfigUpdateFinished();
                }
            }
        };

        task.execute();
    }
}
