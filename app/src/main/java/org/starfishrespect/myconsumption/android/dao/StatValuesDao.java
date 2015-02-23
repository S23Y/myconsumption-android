package org.starfishrespect.myconsumption.android.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.starfishrespect.myconsumption.android.data.StatValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thibaud on 27.01.15.
 */
public class StatValuesDao {
    private static final String TAG = "StatValuesDao";

    private DatabaseHelper db;

    public StatValuesDao(DatabaseHelper db) {
        this.db = db;
    }

    public void insertStatValue(String sensorId, String key, StatValue value) {
        //todo: sensorId
        ContentValues values = new ContentValues();
        values.put("key", key);
        values.put("value", value.getValue());
        // todo: remove try catch
        try {
            db.getWritableDatabase().beginTransaction();
            db.getWritableDatabase().insertWithOnConflict("stat", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            // droper completement la table avant de la reremplir ? à voir qd sync ok
            db.getWritableDatabase().setTransactionSuccessful();
            db.getWritableDatabase().endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "in method insertStatValue: error while accessing the db");
        }
    }

    public List<Integer> getValues(String sensorId) {
        List<Integer> list = new ArrayList<>();

        //@TODO: remove try catch
        try {
            Cursor cr = read("stat");

            // get the mean
            cr.moveToFirst();
            list.add(getI(cr, "value"));

            while (cr.moveToNext()) {
                //get the max, the comp...
                list.add(getI(cr, "value"));
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return list;
    }

    //@TODO: functions below are duplicated (see functions of sensorvluesdao
    public static int getI(Cursor cr, String col) {
        return cr.getInt(cr.getColumnIndex(col));
    }

    public Cursor read(String table) {
        return db.getReadableDatabase().query(table, null, null, null, null, null, null, null);
    }
}
