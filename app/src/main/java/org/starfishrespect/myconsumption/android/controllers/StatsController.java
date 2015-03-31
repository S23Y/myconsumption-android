package org.starfishrespect.myconsumption.android.controllers;

import android.app.AlertDialog;
import android.content.DialogInterface;

import org.codehaus.jackson.map.ObjectMapper;
import org.starfishrespect.myconsumption.android.R;
import org.starfishrespect.myconsumption.android.dao.DatabaseHelper;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.util.AlertUtils;
import org.starfishrespect.myconsumption.server.api.dto.StatsOverPeriodsDTO;

import java.io.IOException;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGE;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

/**
 * Created by thibaud on 01.03.15.
 */
public class StatsController {
    private static final String TAG = makeLogTag(StatsController.class);

    private StatsOverPeriodsDTO stats = null;

    public StatsController() {}

    /**
     *  Load the stats from the local database.
     */
    public void loadStats() {
        String statsJSON;

        try {
            statsJSON = SingleInstance.getDatabaseHelper().getStatForKey("stats").getValue();
        } catch (NullPointerException e) {
            LOGE(TAG, "cannot load stats from local db", e);
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            // Read json
            stats = mapper.readValue(statsJSON, StatsOverPeriodsDTO.class);
        } catch (IOException e) {
            AlertUtils.buildAlert();
        }
    }

    public StatsOverPeriodsDTO getStats() {
        return stats;
    }
}
