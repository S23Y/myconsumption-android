package org.starfishrespect.myconsumption.android.controllers;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.starfishrespect.myconsumption.android.SingleInstance;
import org.starfishrespect.myconsumption.android.events.BuildAlertEvent;
import org.starfishrespect.myconsumption.server.api.dto.StatDTO;

import java.io.IOException;
import java.util.List;

import de.greenrobot.event.EventBus;

import static org.starfishrespect.myconsumption.android.util.LogUtils.LOGE;
import static org.starfishrespect.myconsumption.android.util.LogUtils.makeLogTag;

/**
 * Created by thibaud on 01.03.15.
 */
public class StatsController {
    private static final String TAG = makeLogTag(StatsController.class);

    private List<StatDTO> stats = null;

    public StatsController() {}

    /**
     *  Load the stats from the local database.
     */
    public void loadStats(String sensorId) {
        String statsJSON;

        try {
            statsJSON = SingleInstance.getDatabaseHelper().getValueForKey("stats_" + sensorId).getValue();
        } catch (NullPointerException e) {
            LOGE(TAG, "cannot load stats from local db", e);
            return;
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            // Read json
            stats = mapper.readValue(statsJSON, new TypeReference<List<StatDTO>>(){});
        } catch (IOException e) {
            EventBus.getDefault().post(new BuildAlertEvent(true));
        }
    }

    public List<StatDTO> getStats() {
        return stats;
    }
}
