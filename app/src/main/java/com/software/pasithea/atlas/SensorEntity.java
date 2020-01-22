package com.software.pasithea.atlas;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sensors_table")
public class SensorEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String sensorName;

    public SensorEntity(String sensorName) {
        this.sensorName = sensorName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getSensorName() {
        return sensorName;
    }
}
