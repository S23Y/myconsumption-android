package org.starfishrespect.myconsumption.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.starfishrespect.myconsumption.android.controllers.StatsController;
import org.starfishrespect.myconsumption.android.controllers.UserController;
import org.starfishrespect.myconsumption.android.dao.DatabaseHelper;
import org.starfishrespect.myconsumption.android.dao.SensorValuesDao;
import org.starfishrespect.myconsumption.android.data.KeyValueData;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.ui.LoginActivity;

import java.sql.SQLException;
import java.util.Random;

/**
 * Class used to store unique objects accessible everywhere in the app
 */
public class SingleInstance {

    private static DatabaseHelper databaseHelper;
    private static UserController userController;
    private static int spinnerSensorPosition = 0;
    protected static Context context;
    private static StatsController statsController;
    private static boolean init = true;

    private static int[] colors = {0xffff0000, 0xff0000ff, 0xff000000,
            0xff000060, 0xff008000, 0xff600000, 0xff661144, 0xff606060, 0xffaa6611};

    public static void init(Context c) {
        if (init)
            context = c;
        init = false;

        // Init database helper
        getDatabaseHelper();

        // Load the user
        getUserController().loadUser();
    }

    // removes all the data of the current user and go back to the login
    public static void disconnect() {
        KeyValueData userKey = getDatabaseHelper().getValueForKey("user");
        try {
            if (userKey != null) {
                getDatabaseHelper().getKeyValueDao().delete(userKey);
            }
            SensorValuesDao sensorValuesDao = new SensorValuesDao(getDatabaseHelper());
            for (SensorData s : getDatabaseHelper().getSensorDao().queryForAll()) {
                sensorValuesDao.removeSensor(s.getSensorId());
            }
            getDatabaseHelper().clearTable("sensors");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        context.startActivity(new Intent(context, LoginActivity.class));
        ((Activity) context).finish();
    }

    public static DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null)
            databaseHelper = new DatabaseHelper(context);
        return databaseHelper;
    }

    public static UserController getUserController() {
        if (userController == null)
            userController = new UserController();
        return userController;
    }

    public static StatsController getStatsController() {
        if (statsController == null)
            statsController = new StatsController();
        return statsController;
    }

    public static String getServerUrl() {
        return Config.protocol + Config.serverAddress + ":" + Config.port + "/" + Config.serverDir;
    }

    public static void destroyDatabaseHelper() {
        databaseHelper = null;
    }

    public static int getRandomColor() {
        return colors[new Random().nextInt(colors.length)];
    }

    public static double getkWhToCO2() {
        KeyValueData valueData = getDatabaseHelper().getValueForKey("config_co2");

        if (valueData == null)
            return 0.0;
        else
            return Double.parseDouble(valueData.getValue());
    }

    public static double getkWhDayPrice() {
        KeyValueData valueData = getDatabaseHelper().getValueForKey("config_day");

        if (valueData == null)
            return 0.0;
        else
            return Double.parseDouble(valueData.getValue());
    }

    public static double getkWhNightPrice() {
        KeyValueData valueData = getDatabaseHelper().getValueForKey("config_night");

        if (valueData == null)
            return 0.0;
        else
            return Double.parseDouble(valueData.getValue());
    }

    public static int getSpinnerSensorPosition() {
        return spinnerSensorPosition;
    }

    public static void setSpinnerSensorPosition(int spinnerSensorPosition) {
        SingleInstance.spinnerSensorPosition = spinnerSensorPosition;
    }
}
