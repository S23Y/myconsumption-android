package org.starfishrespect.myconsumption.android.tasks;

import android.os.AsyncTask;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.dao.DatabaseHelper;
import org.starfishrespect.myconsumption.android.data.KeyValueData;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.starfishrespect.myconsumption.android.util.CryptoUtils;
import org.starfishrespect.myconsumption.server.api.dto.SimpleResponseDTO;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGD;
import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGE;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

/**
 * Get statistics values from the server using an AsyncTask
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class StatValuesUpdater {

    public interface StatUpdateFinishedCallback {
        public void onStatUpdateFinished();
    }

    private StatUpdateFinishedCallback statUpdateFinishedCallback;
    private static final String TAG = makeLogTag(StatValuesUpdater.class);

    public void setUpdateFinishedCallback(StatUpdateFinishedCallback updateFinishedCallback) {
        this.statUpdateFinishedCallback = updateFinishedCallback;
    }

    public void refreshDB() {

        AsyncTask<Void, List, Void> task = new AsyncTask<Void, List, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DatabaseHelper db = SingleInstance.getDatabaseHelper();
                RestTemplate template = new RestTemplate();
                HttpHeaders httpHeaders = CryptoUtils.createHeadersCurrentUser();
                ResponseEntity<StatDTO[]> responseEnt;
                template.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

                try {
                    for (SensorData sensor : db.getSensorDao().queryForAll()) {
                        // Stats
                        String url = String.format(SingleInstance.getServerUrl() + "stats/sensor/%s", sensor.getSensorId());

                        responseEnt = template.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), StatDTO[].class);
                        StatDTO[] statsArray = responseEnt.getBody();
                        List<StatDTO> stats = new ArrayList<>(Arrays.asList(statsArray));

                        ObjectMapper mapper = new ObjectMapper();

                        try {
                            String json = mapper.writeValueAsString(stats);
                            String key = "stats_" + sensor.getSensorId();

                            int id = db.getIdForKey(key);
                            KeyValueData valueData = new KeyValueData(key, json);
                            valueData.setId(id);

                            LOGD(TAG, "writing stat in local db: " + json);
                            db.getKeyValueDao().createOrUpdate(valueData);

                        } catch (IOException e) {
                            LOGD(TAG, "Cannot create stats " + stats.toString(), e);
                        }

                    }
                } catch (SQLException e) {
                    LOGD(TAG, "Cannot create stats ", e);
                } catch (ResourceAccessException | HttpClientErrorException e) {
                    LOGE(TAG, "Cannot access server ", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (statUpdateFinishedCallback != null) {
                    statUpdateFinishedCallback.onStatUpdateFinished();
                }
            }
        };

        task.execute();
    }
}
