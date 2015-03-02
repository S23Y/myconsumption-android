package org.starfishrespect.myconsumption.android.data;

import java.io.Serializable;
import java.util.List;

import biz.manex.sr.myconsumption.api.dto.StatDTO;

/**
 * Created by thibaud on 02.03.15.
 */
public class StatPeriods implements Serializable {
    private String mSensorId;

    private List<StatDTO> mStatDTOs;

    public StatPeriods(String sensorId) {
        mSensorId = sensorId;
    }

    public List<StatDTO> getStatDTOs() {
        return mStatDTOs;
    }

    public void setStatDTOs(List<StatDTO> statDTOs) {
        mStatDTOs = statDTOs;
    }
}
