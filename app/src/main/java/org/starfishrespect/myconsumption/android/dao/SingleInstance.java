package org.starfishrespect.myconsumption.android.dao;

import android.app.Activity;
import android.content.Context;

import org.starfishrespect.myconsumption.android.controllers.StatsController;
import org.starfishrespect.myconsumption.android.ui.MainActivity;
import org.starfishrespect.myconsumption.android.controllers.FragmentController;
import org.starfishrespect.myconsumption.android.controllers.UserController;
import org.starfishrespect.myconsumption.android.data.UserData;
import org.starfishrespect.myconsumption.server.api.dto.StatsOverPeriodsDTO;

/**
 * Class used to store unique objects accessible everywhere in the app
 */
public class SingleInstance {

    private static DatabaseHelper databaseHelper;
    private static FragmentController fragmentController;
    private static UserController userController;
    //private static MainActivity mainActivity; // @TODO do not keep this, it should not be done this way
    protected static Context context;

    //private static String serverAddress = "pahe.manex.biz";
    private static String serverAddress = "172.20.1.66"; // @manex (bridged)
    //private static String serverAddress  = "192.168.154.133";  // @ans (nat)
    private static int port = 8080;
    private static String protocol = "http://";
    // TODO serverDir = "myconsumption"
    private static String serverDir = "";
    private static StatsController statsController;

    public static void init(Context c) {
        context = c;

        // Init database helper
        SingleInstance.getDatabaseHelper();

        // Load the user
        SingleInstance.getUserController().loadUser();
    }

    public static MainActivity getMainActivity() {
        return (MainActivity) context;
    }

    public static DatabaseHelper getDatabaseHelper() {
        if (databaseHelper == null)
            databaseHelper = new DatabaseHelper(getMainActivity());
        return databaseHelper;
    }

    public static UserController getUserController() {
        if (userController == null)
            userController = new UserController(databaseHelper);
        return userController;
    }

    public static FragmentController getFragmentController() {
        if (fragmentController == null)
            fragmentController = new FragmentController();
        return fragmentController;
    }

    public static StatsController getStatsController() {
        if (statsController == null)
            statsController = new StatsController(databaseHelper);
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
}
