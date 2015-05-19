package org.starfishrespect.myconsumption.android.controllers;

import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.data.SensorData;
import org.starfishrespect.myconsumption.android.data.UserData;
import org.codehaus.jackson.map.ObjectMapper;
import org.starfishrespect.myconsumption.android.events.BuildAlertEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by thibaud on 06.02.15.
 */
public class UserController {
    private static final String TAG = "UserController";
    private UserData user = null;

    public UserController() {}

    /**
     *  Load the user from the database and set the sensors associated to him.
     */
    public void loadUser() {
        String userJson = SingleInstance.getDatabaseHelper().getValueForKey("user").getValue();
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
