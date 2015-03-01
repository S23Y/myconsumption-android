package org.starfishrespect.myconsumption.android.data;

import org.starfishrespect.myconsumption.android.misc.GraphBaseColors;
import org.starfishrespect.myconsumption.server.api.dto.SensorDTO;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Object used to store sensor information on database
 */
@DatabaseTable(tableName = "sensors")
public class SensorData implements Serializable, Comparable<SensorData> {
    @DatabaseField(id = true, unique = true)
    private String sensorId;
    @DatabaseField
    private String name;
    @DatabaseField
    private String type;
    @DatabaseField
    private Date firstLocalValue = new Date(0);
    @DatabaseField
    private Date lastLocalValue = new Date(0);
    @DatabaseField
    private Date firstServerValue = new Date(0);
    @DatabaseField
    private Date lastServerValue = new Date(0);
    @DatabaseField
    private boolean visible = true;
    @DatabaseField
    private int color = 0xFFAA00;
    @DatabaseField
    private boolean dead = false;

    public SensorData() {
    }

    public SensorData(SensorDTO from) {
        this.sensorId = from.getId();
        this.name = from.getName();
        this.type = from.getType();
        this.firstServerValue = from.getFirstValue();
        this.lastServerValue = from.getLastValue();
        this.dead = from.isDead();
        color = GraphBaseColors.getRandomColor();
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getFirstLocalValue() {
        return firstLocalValue;
    }

    public void setFirstLocalValue(Date firstLocalValue) {
        this.firstLocalValue = firstLocalValue;
    }

    public Date getLastLocalValue() {
        return lastLocalValue;
    }

    public void setLastLocalValue(Date lastLocalValue) {
        this.lastLocalValue = lastLocalValue;
    }

    public Date getFirstServerValue() {
        return firstServerValue;
    }

    public void setFirstServerValue(Date firstServerValue) {
        this.firstServerValue = firstServerValue;
    }

    public Date getLastServerValue() {
        return lastServerValue;
    }

    public void setLastServerValue(Date lastServerValue) {
        this.lastServerValue = lastServerValue;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int compareTo(SensorData another) {
        return sensorId.compareTo(another.sensorId);
    }

    public boolean sameId(SensorData another) {
        if (another == null) {
            return false;
        }
        return sensorId.equals(another.sensorId);
    }

    public boolean updateSettings(SensorData from) {
        boolean edited = false;
        if (!this.getName().equals(from.getName())) {
            this.setName(from.getName());
            edited = true;
        }
        if (this.isDead() != from.isDead()) {
            this.setDead(from.isDead());
            edited = true;
        }
        if (!this.getFirstServerValue().equals(from.getFirstServerValue())) {
            this.setFirstServerValue(from.getFirstServerValue());
            edited = true;
        }
        if (!this.getLastServerValue().equals(from.getLastServerValue())) {
            this.setLastServerValue(from.getLastServerValue());
            edited = true;
        }
        return edited;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "sensorId='" + sensorId + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", firstLocalValue=" + firstLocalValue +
                ", lastLocalValue=" + lastLocalValue +
                ", firstServerValue=" + firstServerValue +
                ", lastServerValue=" + lastServerValue +
                ", visible=" + visible +
                ", color=" + color +
                ", dead=" + dead +
                '}';
    }
}
