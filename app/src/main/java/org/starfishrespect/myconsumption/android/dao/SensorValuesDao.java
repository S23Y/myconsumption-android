package org.starfishrespect.myconsumption.android.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.data.SensorValue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Created by Patrick Herbeuval on 7/04/14.
 */
public class SensorValuesDao {

    private static final String TAG = "SensorValuesDao";

    private DatabaseHelper db;

    public SensorValuesDao(DatabaseHelper db) {
        this.db = db;
    }

    /**
     * Compares the given sensor list and the one saved in database, then
     * creates tables for sensor not present yet, and delete tables if sensors are
     * removed
     *
     * @param sensors the sensor list
     * @return false if any error occurs
     */
    public boolean updateSensorList(List<SensorData> sensors) {
        Log.d(TAG, "updatesensorlist");
        try {
            List<SensorData> oldSensors = db.getSensorDao().queryForAll();
            Collections.sort(sensors);
            Collections.sort(oldSensors);
            int progress = 0, oldProgress = 0;
            List<SensorData> toAdd = new ArrayList<>();
            List<SensorData> toDelete = new ArrayList<>();
            while (progress < sensors.size() && oldProgress < oldSensors.size()) {
                SensorData cur = sensors.get(progress);
                SensorData old = oldSensors.get(oldProgress);
                if (cur.sameId(old)) {
                    System.out.println("Same id\n" + cur.toString() + "\n" + old.toString());
                    progress++;
                    oldProgress++;
                    if (old.updateSettings(cur)) {
                        db.getSensorDao().update(old);
                    }
                } else if (cur.compareTo(old) > 0) {
                    toDelete.add(old);
                    oldProgress++;
                } else {
                    toAdd.add(cur);
                    progress++;
                }
            }
            while (progress < sensors.size()) {
                Log.d(TAG, "ADD LOOP");
                toAdd.add(sensors.get(progress));
                progress++;
            }
            while (oldProgress < oldSensors.size()) {
                Log.d(TAG, "DELETE LOOP");
                toDelete.add(oldSensors.get(oldProgress));
                oldProgress++;
            }

            SQLiteDatabase writeDb = db.getWritableDatabase();
            if (writeDb == null) {
                return false;
            }

            for (SensorData s : toAdd) {
                Log.d(TAG, "will add " + s.getSensorId());
                db.getSensorDao().create(s);
                writeDb.execSQL("CREATE TABLE IF NOT EXISTS sensor_" + s.getSensorId() + "(" +
                        "timestamp INTEGER PRIMARY KEY, value INTEGER)");
                writeDb.execSQL("DELETE FROM sensor_" + s.getSensorId());

            }

            for (SensorData s : toDelete) {
                Log.d(TAG, "will delete " + s.getSensorId());
                db.getSensorDao().delete(s);
                writeDb.execSQL("DROP TABLE IF EXISTS sensor_" + s.getSensorId());
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void removeSensor(String sensorId) {
        db.getWritableDatabase().execSQL("DROP TABLE IF EXISTS sensor_" + sensorId);
    }

    public void insertSensorValue(String sensorId, SensorValue value) {
        ContentValues values = new ContentValues();
        values.put("timestamp", value.getTimestamp());
        values.put("value", value.getValue());
        db.getWritableDatabase().insertWithOnConflict("sensor_" + sensorId, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void insertSensorValues(String sensorId, List<SensorValue> values) {
        db.getWritableDatabase().beginTransaction();
        for (SensorValue value : values) {
            insertSensorValue(sensorId, value);
        }
        db.getWritableDatabase().setTransactionSuccessful();
        db.getWritableDatabase().endTransaction();
    }

    public List<SensorValue> getValues(String sensorId, int start, int end) {
        List<SensorValue> values = new ArrayList<>();
        Cursor cr = read("sensor_" + sensorId, "timestamp >= "
                + start + " AND timestamp <= " + end, null, null);
        while (cr.moveToNext()) {
            values.add(new SensorValue(getI(cr, "timestamp"), getI(cr, "value")));
        }
        return values;
    }

    public static int getI(Cursor cr, String col) {
        return cr.getInt(cr.getColumnIndex(col));
    }

    public Cursor read(String table, String where, String order, String limit) {
        return db.getReadableDatabase().query(table, null, where, null, null, null, order, limit);
    }

}
