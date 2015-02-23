package org.starfishrespect.myconsumption.android.data;

import biz.manex.sr.myconsumption.api.dto.UserDTO;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Object used to store user informations on database
 */
public class UserData implements Serializable {

    private String name;
    @JsonIgnore
    private List<SensorData> sensors;

    public UserData() {
        this.sensors = new ArrayList<>();
    }

    public UserData(UserDTO from) {
        this();
        this.name = from.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SensorData> getSensors() {
        return sensors;
    }

    public void addSensor(SensorData sensor) {
        this.sensors.add(sensor);
    }

    public void setSensors(List<SensorData> sensors) {
        this.sensors = sensors;
    }
}
