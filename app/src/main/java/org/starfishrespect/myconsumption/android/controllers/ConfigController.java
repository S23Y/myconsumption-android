package org.starfishrespect.myconsumption.android.controllers;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.util.AlertUtils;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;

import java.io.IOException;
import java.util.List;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGE;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

/**
 * Created by thibaud on 08.04.15.
 */
public class ConfigController {
    private static final String TAG = makeLogTag(ConfigController.class);

    public ConfigController() {}

    /**
     *  Load config values from the local database.
     */
    public void loadConfig() {
        String stringCo2, stringDay, stringNight;

        try {
            stringCo2 = SingleInstance.getDatabaseHelper().getValueForKey("config_co2").getValue();
            stringDay = SingleInstance.getDatabaseHelper().getValueForKey("config_day").getValue();
            stringNight = SingleInstance.getDatabaseHelper().getValueForKey("config_night").getValue();
        } catch (NullPointerException e) {
            LOGE(TAG, "cannot load config from local db", e);
            return;
        }

        SingleInstance.setkWhToCO2(Double.parseDouble(stringCo2));
        SingleInstance.setkWhDayPrice(Double.parseDouble(stringDay));
        SingleInstance.setkWhNightPrice(Double.parseDouble(stringNight));
    }
}
