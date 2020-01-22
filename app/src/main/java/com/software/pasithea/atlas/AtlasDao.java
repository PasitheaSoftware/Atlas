package com.software.pasithea.atlas;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * Atlas DAO for all the tables
 */
@Dao
public interface AtlasDao {

    /*
    SQL requests for the status
     */

    @Insert
    void insertStatus(StatusEntity status);

    @Query("UPDATE status_table SET environment_variable_status=(:status) WHERE environment_variable_name='WakeupWordDetector'")
    void updateHotword(int status);

    @Query("SELECT environment_variable_status FROM status_table WHERE environment_variable_name LIKE (:name)")
    LiveData<List<Integer>> selectStatus(String name);

    /*
    SQL requests for the apps
     */

    @Insert
    void insertApp(AppsEntity apps);

    @Query("UPDATE apps_table SET isRunning=(:status) WHERE appName LIKE (:name)")
    void updateAppStatus(String name, int status);

    @Query("SELECT isRunning FROM apps_table WHERE appName LIKE (:name)")
    LiveData<List<Integer>> selectAppStatus(String name);
}
