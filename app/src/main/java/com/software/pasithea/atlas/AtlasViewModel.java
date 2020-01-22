package com.software.pasithea.atlas;

import android.app.Application;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * Environment Manager class
 */
public class AtlasViewModel extends AndroidViewModel {
    private static final String TAG = "AtlasViewModel";

    private Application application;
    private AtlasAudioManager mAtlasAudioManager;
    private AtlasRepository mAtlasRepository;

    public AtlasViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        mAtlasAudioManager = new AtlasAudioManager(application);
        mAtlasRepository = new AtlasRepository(application);
    }

    public List<Apps> getAppsList() {
        try {
            PackageManager mPackageManager = application.getPackageManager();
            AtlasAppsManager mAtlasAppsManager = new AtlasAppsManager(mPackageManager);
            return mAtlasAppsManager.getApps();
        } catch (Exception e) {
            Log.e(TAG, "getAppsList: Exception occurred, returning null value", e);
            return null;
        }
    }

    public void requestAudioFocus(){
        try {
            mAtlasAudioManager.requestAudioFocus();
            Log.i(TAG, "requestAudioFocus: Audio focus granted");
        } catch (Exception e){
            Log.e(TAG, "requestAudioFocus: Exception occurred", e);
        }
    }

    public void abandonAudioFocus(){
        try {
            mAtlasAudioManager.abandonFocus();
            Log.i(TAG, "abandonAudioFocus: Audio focus released");
        } catch (Exception e) {
            Log.e(TAG, "abandonAudioFocus: Exception occurred", e);
        }
    }

    public List<Sensor> getSensorList(){
        try {
            AtlasSensorsManager mAtlasSensorManager = new AtlasSensorsManager(application);
            return mAtlasSensorManager.getSensorList();
        } catch (Exception e) {
            Log.e(TAG, "getSensorList: Exception occurred, returning null value", e);
            return null;
        }
    }

    public void setAudioVolume(){
        try {
            mAtlasAudioManager.setAudioVolume();
            Log.i(TAG, "setAudioVolume: Audio volume automatically set");
        } catch (Exception e){
            Log.e(TAG, "setAudioVolume: Exception occurred", e);
        }
    }

    public void setAudioVolume(int volume){
        try {
            mAtlasAudioManager.setAudioVolume(volume);
            Log.i(TAG, "setAudioVolume: Audio volume manually set to " + volume + " level");
        } catch (Exception e){
            Log.e(TAG, "setAudioVolume: Exception occurred", e);
        }
    }

    public void restoreVolume(){
        try {
            mAtlasAudioManager.restoreVolume();
            Log.i(TAG, "restoreVolume: Audio volume restored to its previous level");
        } catch (Exception e){
            Log.e(TAG, "restoreVolume: Exception occurred", e);
        }
    }

    /*
    DataBase operations
     */

    public void updateHotword(int status){
        try {
            mAtlasRepository.updateHotword(status);
            Log.i(TAG, "updateHotword: Hotword status updated");
        } catch (Exception e){
            Log.e(TAG, "updateHotword: Exception occurred", e);
        }
    }

    public void updateAppStatus(String appName, int status){
        try {
            mAtlasRepository.updateAppStatus(appName, status);
            Log.i(TAG, "updateAppStatus: App status updated");
        } catch (Exception e){
            Log.e(TAG, "updateAppStatus: Exception occurred", e);
        }
    }

    public LiveData<List<Integer>> getHotwordStatus(){
        try {
            LiveData<List<Integer>> hotwordStatus = mAtlasRepository.getHotwordStatus();
            Log.i(TAG, "getHotwordStatus: Status returned");
            return hotwordStatus;
        } catch (Exception e) {
            Log.e(TAG, "getHotwordStatus: Exception occurred, returning null value", e);
            return null;
        }
    }

    public LiveData<List<Integer>> getAppStatus(String appName){
        try {
            LiveData<List<Integer>> appStatus = mAtlasRepository.getAppStatus(appName);
            Log.i(TAG, "getAppStatus: Status returned");
            return appStatus;
        } catch (Exception e){
            Log.e(TAG, "getAppStatus: Exception occurred, returning null value", e);
            return null;
        }
    }
}
