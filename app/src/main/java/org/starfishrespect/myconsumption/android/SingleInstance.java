package org.starfishrespect.myconsumption.android;

import android.content.Context;
import android.content.Intent;

import org.starfishrespect.myconsumption.android.controllers.StatsController;
//import org.starfishrespect.myconsumption.android.controllers.FragmentController;
import org.starfishrespect.myconsumption.android.controllers.UserController;
import org.starfishrespect.myconsumption.android.dao.DatabaseHelper;
import org.starfishrespect.myconsumption.android.dao.SensorValuesDao;
import org.starfishrespect.myconsumption.android.data.KeyValueData;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.ui.ChartActivity;
import org.starfishrespect.myconsumption.android.ui.LoginActivity;

import java.sql.SQLException;
import java.util.Random;

/**
 * Class used to store unique objects accessible everywhere in the app
 */
public class SingleInstance {

    private static DatabaseHelper databaseHelper;
//    private static FragmentController fragmentController;
    private static UserController userController;
    //private static MainActivity mainActivity;
    protected static Context context;

    // TODO put those config in Config.java or in an xml file
    //private static String serverAddress = "pahe.manex.biz";
    //private static String serverAddress = "172.20.1.75"; // @manex (bridged)
    //private static String serverAddress = "192.168.242.129";  // @ans (nat)
    private static String serverAddress = "192.168.1.9"; // @ans bridged ethernet
    //private static String serverAddress = "192.168.1.32";   // @lw (bridged)
    private static int port = 8080;
    private static String protocol = "http://";
    // TODO serverDir = "myconsumption"
    private static String serverDir = "";

    private static StatsController statsController;

    private static int[] colors = {0xffff0000, 0xff0000ff, 0xff000000,
            0xff000060, 0xff008000, 0xff600000, 0xff661144, 0xff606060, 0xffaa6611};

    private static boolean init = true;

//    // Since it is a Singleton, the constructor is private (we can't instantiate this class) => faux, ceci est une classe qui regroupe des singletons et non un singleton en elle meme
//    private SingleInstance() {
//    }

    public static void init(Context c) {
        if (init) {
            context = c;
        }
        init = false;

        // Init database helper
        SingleInstance.getDatabaseHelper();

        // Load the user
        SingleInstance.getUserController().loadUser();
    }

    public static ChartActivity getChartActivity() {
        return (ChartActivity) context;
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

//    public static FragmentController getFragmentController() {
//        if (fragmentController == null)
//            fragmentController = new FragmentController();
//        return fragmentController;
//    }

    public static StatsController getStatsController() {
        if (statsController == null)
            statsController = new StatsController();
        return statsController;
    }

    public static String getServerUrl() {
        return protocol + serverAddress + ":" + port + "/" + serverDir;
    }

    public static String getServerAddress() {
        return serverAddress;
    }

    public static int getPort() {
        return port;
    }

    public static String getProtocol() {
        return protocol;
    }

    public static void setServerAddress(String serverAddress) {
        SingleInstance.serverAddress = serverAddress;
    }

    public static void setServerPort(int port) {
        SingleInstance.port = port;
    }

    public static void setProtocol(String protocol) {
        SingleInstance.protocol = protocol;
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

    // removes all the data of the current user and go back to the login
    public static void disconnect(ChartActivity chartActivity) {
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

        chartActivity.startActivity(new Intent(chartActivity, LoginActivity.class));
        chartActivity.finish();
    }
}
