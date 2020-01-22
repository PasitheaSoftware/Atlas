package com.software.pasithea.atlas;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "status_table")
public class StatusEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String environment_variable_name;
    private int environment_variable_status;

    public StatusEntity(String environment_variable_name, int environment_variable_status) {
        this.environment_variable_name = environment_variable_name;
        this.environment_variable_status = environment_variable_status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getEnvironment_variable_name() {
        return environment_variable_name;
    }

    public int getEnvironment_variable_status() {
        return environment_variable_status;
    }
}