package org.starfishrespect.myconsumption.android.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Key-Value data pair to store stats into the local Android database.
 * Created by thibaud on 31.03.15.
 */
@DatabaseTable(tableName = "stats")
public class StatData {
    @DatabaseField(id = true)
    private int id;
    @DatabaseField(unique = true, index = true)
    private String key;
    @DatabaseField
    private String value;

    public StatData(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public StatData() {

    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "StatData{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
