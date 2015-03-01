package org.starfishrespect.myconsumption.android.dao;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.data.StatValue;
import org.starfishrespect.myconsumption.server.api.dto.StatsOverPeriodsDTO;
import org.starfishrespect.myconsumption.server.api.dto.UserDTO;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
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
                    SQLiteDatabase writeDb = db.getWritableDatabase();
                    if (writeDb == null) {
                        return null;
                    }
                    writeDb.execSQL("CREATE TABLE IF NOT EXISTS stat(" +
                                "key STRING PRIMARY KEY, value INTEGER)");

                    StatValuesDao valueDao = new StatValuesDao(db);
                    for (SensorData sensor : db.getSensorDao().queryForAll()) {
                        // Stats
                        String url = String.format(SingleInstance.getServerUrl() + "stat/sensor/%s", sensor.getSensorId());
                        StatsOverPeriodsDTO stats = template.getForObject(url, StatsOverPeriodsDTO.class);

                        //valueDao.insertStatValue(sensor.getSensorId(), "stats", stats);
                        //db.getSensorDao().update(sensor);
                        //Log.d(TAG, "Inserted stat value (mean): " + stats.getValue());
                    }
                } catch (SQLException e) {

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
