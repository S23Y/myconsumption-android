package org.starfishrespect.myconsumption.android.dao;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.codehaus.jackson.map.ObjectMapper;
import org.starfishrespect.myconsumption.android.data.KeyValueData;
import org.starfishrespect.myconsumption.android.data.SensorData;
import biz.manex.sr.myconsumption.api.dto.StatsOverPeriodsDTO;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by thibaud on 27.01.15.
 * Get statistics values from the server using an AsyncTask
 */
public class StatValuesUpdater {

    public interface StatUpdateFinishedCallback {
        public void onStatUpdateFinished();
    }

    private StatUpdateFinishedCallback statUpdateFinishedCallback;
    private static final String TAG = "StatValuesUpdater";

    public void setUpdateFinishedCallback(StatUpdateFinishedCallback updateFinishedCallback) {
        this.statUpdateFinishedCallback = updateFinishedCallback;
    }

    public void refreshDB() {

        AsyncTask<Void, List, Void> dltest = new AsyncTask<Void, List, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DatabaseHelper db = SingleInstance.getDatabaseHelper();
                RestTemplate template = new RestTemplate();
                template.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

                try {
/*                    SQLiteDatabase writeDb = db.getWritableDatabase();
                    if (writeDb == null) {
                        return null;
                    }
                    writeDb.execSQL("CREATE TABLE IF NOT EXISTS stats(" +
                                "key STRING PRIMARY KEY, value STRING)");*/


                    for (SensorData sensor : db.getSensorDao().queryForAll()) {
                        // Stats
                        String url = String.format(SingleInstance.getServerUrl() + "stat/sensor/%s", sensor.getSensorId());
                        StatsOverPeriodsDTO stats = template.getForObject(url, StatsOverPeriodsDTO.class);

                        ObjectMapper mapper = new ObjectMapper();

                        try {
                            String json = mapper.writeValueAsString(stats);
                            Log.d(TAG, "writing stat in local db: " + json);
                            db.getKeyValueDao().createOrUpdate(new KeyValueData("stats", json));
                        } catch (IOException e) {
                            Log.d(TAG, "Cannot create stats " + stats.toString(), e);
                        }

                    }
                } catch (SQLException e) {
                    Log.d(TAG, "Cannot create user ", e);
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

        dltest.execute();
    }
}
