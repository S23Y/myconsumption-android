package org.starfishrespect.myconsumption.android.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.starfishrespect.myconsumption.android.data.KeyValueData;
import org.starfishrespect.myconsumption.android.data.SensorData;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;


/**
 * Created by Patrick Herbeuval on 3/04/14.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "DBHelper";

    private static final String DATABASE_NAME = "starfishr_myconsumption";
    private static final int DATABASE_VERSION = 8;

    private Dao<KeyValueData, String> keyValueDao;
    private Dao<SensorData, String> sensorDao;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, SensorData.class);
            TableUtils.createTable(connectionSource, KeyValueData.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to create databases", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, SensorData.class, true);
            TableUtils.dropTable(connectionSource, KeyValueData.class, true);
            database.execSQL("DROP TABLE IF EXISTS sensor_values");
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVersion + " to new "
                    + newVersion, e);
        }
    }

    public void clearTable(String table) throws SQLException {
        getSensorDao().executeRawNoArgs("DELETE FROM " + table);
    }

    public Dao<SensorData, String> getSensorDao() throws SQLException {
        if (sensorDao == null) {
            sensorDao = getDao(SensorData.class);
        }
        return sensorDao;
    }

    public Dao<KeyValueData, String> getKeyValueDao() throws SQLException {
        if (keyValueDao == null) {
            keyValueDao = getDao(KeyValueData.class);
        }
        return keyValueDao;
    }

    /**
     * Convenient way to get a value from the KeyValueDao
     *
     * @param key the key we want to retrieve
     * @return KeyValueData object, or null if there is no value associated
     */
    public KeyValueData getValueForKey(String key) {
        try {
            List<KeyValueData> data = getKeyValueDao().queryForEq("key", key);
            if (data != null && data.size() > 0) {
                return data.get(0);
            }
        } catch (SQLException e) {
            return null;
        }
        return null;
    }
}
