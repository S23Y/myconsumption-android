package org.starfishrespect.myconsumption.android.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Key-Value data pair for the database
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */
@DatabaseTable(tableName = "keyvalue")
public class KeyValueData {
    @DatabaseField(generatedId=true, allowGeneratedIdInsert=true) // Auto increment
    private Integer id;
    @DatabaseField(unique = true, index = true)
    private String key;
    @DatabaseField
    private String value;

    public KeyValueData(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public KeyValueData() {

    }

    public Integer getId() {
        return id;
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
        return "KeyValueData{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }
}
