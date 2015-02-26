package org.starfishrespect.myconsumption.android.controllers;

import org.starfishrespect.myconsumption.android.dao.DatabaseHelper;
import org.starfishrespect.myconsumption.android.dao.SingleInstance;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.data.UserData;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by thibaud on 06.02.15.
 */
public class UserController {
    private static final String TAG = "UserController";
    private final DatabaseHelper db;
    private UserData user = null;

    public UserController(DatabaseHelper databaseHelper) {
        this.db = databaseHelper;
    }

    /**
     *  Load the user from the database and set the sensors associated to him.
     */
    public void loadUser() {
        String userJson = db.getValueForKey("user").getValue();
        ObjectMapper mapper = new ObjectMapper();
        List<SensorData> sensors;

        try {
            // Read json
            user = mapper.readValue(userJson, UserData.class);
            // Get sensors
            sensors = db.getSensorDao().queryForAll();
            user.setSensors(sensors);
            //// Notify the fragments that the user has been modified
            //SingleInstance.getUserController().reloadUser(refreshData);
        } catch (IOException| SQLException e) {
            SingleInstance.getMainActivity().buildAlert();
        }
    }

    public UserData getUser() {
        return user;
    }
}
