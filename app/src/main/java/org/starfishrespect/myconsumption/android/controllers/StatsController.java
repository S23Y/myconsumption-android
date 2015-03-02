package org.starfishrespect.myconsumption.android.controllers;

import org.codehaus.jackson.map.ObjectMapper;
import org.starfishrespect.myconsumption.android.dao.DatabaseHelper;
import org.starfishrespect.myconsumption.android.dao.SingleInstance;
import org.starfishrespect.myconsumption.android.data.SensorData;
import biz.manex.sr.myconsumption.api.dto.StatsOverPeriodsDTO;

import java.io.IOException;
import java.util.List;

/**
 * Created by thibaud on 01.03.15.
 */
public class StatsController {
    private static final String TAG = "StatsController";
    private final DatabaseHelper db;
    private StatsOverPeriodsDTO stats = null;

    public StatsController(DatabaseHelper databaseHelper) {
        this.db = databaseHelper;
    }

    /**
     *  Load the stats from the local database.
     */
    public void loadStats() {
        String statsJSON = db.getValueForKey("stats").getValue();
        ObjectMapper mapper = new ObjectMapper();

        try {
            // Read json
            stats = mapper.readValue(statsJSON, StatsOverPeriodsDTO.class);
        } catch (IOException e) {
            SingleInstance.getMainActivity().buildAlert();
        }
    }

    public StatsOverPeriodsDTO getStats() {
        return stats;
    }
}
