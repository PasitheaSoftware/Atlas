package com.software.pasithea.atlas;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;

class AtlasSensorsManager {
    private static final String TAG = "AtlasSensorsManager";

    private SensorManager mSensorManager;

    public AtlasSensorsManager(Application application){
        mSensorManager = (SensorManager) application.getSystemService(Context.SENSOR_SERVICE);
    }

    protected List<Sensor> getSensorList(){
        return mSensorManager.getSensorList(Sensor.TYPE_ALL);
    }
}
