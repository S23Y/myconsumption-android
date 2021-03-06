package org.starfishrespect.myconsumption.android.data;

import org.starfishrespect.myconsumption.server.api.dto.UserDTO;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Object used to store user information in the database
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */
public class UserData implements Serializable {

    private String name;
    private String password;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
