package com.software.pasithea.atlas;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "apps_table")
class AppsEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String appName;
    private int isRunning;

    public AppsEntity(String appName, int isRunning) {
        this.appName = appName;
        this.isRunning = isRunning;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getAppName() {
        return appName;
    }

    public int getIsRunning() {
        return isRunning;
    }
}
