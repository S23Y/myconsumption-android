package org.starfishrespect.myconsumption.android.controllers;

import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.data.KeyValueData;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.data.UserData;
import org.codehaus.jackson.map.ObjectMapper;
import org.starfishrespect.myconsumption.android.events.BuildAlertEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Handle local database access for user
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 * Author: Thibaud Ledent
 */
public class UserController {
    private static final String TAG = "UserController";
    private UserData user = null;

    public UserController() {}

    /**
     *  Load the user from the database and set the sensors associated to him.
     */
    public void loadUser() {
        KeyValueData keyData = SingleInstance.getDatabaseHelper().getValueForKey("user");

        if (keyData == null)
            return;

        String userJson = keyData.getValue();
        ObjectMapper mapper = new ObjectMapper();
        List<SensorData> sensors;

        try {
            // Read json
            user = mapper.readValue(userJson, UserData.class);
            // Get sensors
            sensors = SingleInstance.getDatabaseHelper().getSensorDao().queryForAll();
            user.setSensors(sensors);
        } catch (IOException| SQLException e) {
            EventBus.getDefault().post(new BuildAlertEvent(true));
        }
    }

    public UserData getUser() {
        return user;
    }
}
