/* ====================================================================
 *
 *  Copyright (C) 2019 François Laforgia - All Rights Reserved
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL FRANCOIS LAFORGIA BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package com.software.pasithea.atlas;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * The repository attached to the Atlas Entities.
 */

public class AtlasRepository {
    private static final String TAG = "AtlasRepository";

    private AtlasDao mAtlasDao;
    private LiveData<List<Integer>> hotwordStatus;

    public AtlasRepository(Application application) {
        AtlasDatabase mAtlasDatabase = AtlasDatabase.getInstance(application);
        mAtlasDao = mAtlasDatabase.databaseDao();
        hotwordStatus = mAtlasDao.selectStatus("WakeupWordDetector");
    }

    public LiveData<List<Integer>> getHotwordStatus() {
        return hotwordStatus;
    }

    public LiveData<List<Integer>> getAppStatus(String appName){
        return mAtlasDao.selectAppStatus(appName);
    }

    public void insertStatus(StatusEntity status){
        new AtlasDbInsertStatusAsyncTask(mAtlasDao).execute(status);
    }

    public void insertApp(AppsEntity app){
        new AtlasDbInsertAppAsyncTask(mAtlasDao).execute(app);
    }

    public void updateHotword(int status){
        new AtlasDbUpdateStatusAsyncTask(mAtlasDao, 0).execute(status);
    }

    public void updateAppStatus(String appName, int status){
        new AtlasDbUpdateAppStatusAsyncTask(mAtlasDao, appName).execute(status);
    }

    private static class AtlasDbInsertStatusAsyncTask extends AsyncTask<StatusEntity, Void, Void>{
        private AtlasDao asyncAtlasDao;

        public AtlasDbInsertStatusAsyncTask(AtlasDao asyncAtlasDao) {
            this.asyncAtlasDao = asyncAtlasDao;
        }

        @Override
        protected Void doInBackground(StatusEntity... Statuses) {
            asyncAtlasDao.insertStatus(Statuses[0]);
            return null;
        }
    }

    private static class AtlasDbInsertAppAsyncTask extends AsyncTask<AppsEntity, Void, Void>{
        private AtlasDao asyncAtlasDao;

        public AtlasDbInsertAppAsyncTask(AtlasDao asyncAtlasDao){
            this.asyncAtlasDao = asyncAtlasDao;
        }

        @Override
        protected Void doInBackground(AppsEntity... appsEntities) {
            asyncAtlasDao.insertApp(appsEntities[0]);
            return null;
        }
    }

    private static class AtlasDbUpdateStatusAsyncTask extends AsyncTask<Integer, Void, Void>{
        private AtlasDao asyncAtlasDao;
        private int operation;

        public AtlasDbUpdateStatusAsyncTask(AtlasDao asyncAtlasDao, int operation) {
            this.asyncAtlasDao = asyncAtlasDao;
            this.operation = operation;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            switch (operation) {
                case 0:
                    asyncAtlasDao.updateHotword(integers[0]);
                    break;
            }
            return null;
        }
    }

    private static class AtlasDbUpdateAppStatusAsyncTask extends AsyncTask<Integer, Void, Void>{
        private AtlasDao asyncAtlasDao;
        private String appName;

        public AtlasDbUpdateAppStatusAsyncTask(AtlasDao asyncAtlasDao, String appName){
            this.asyncAtlasDao = asyncAtlasDao;
            this.appName = appName;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            asyncAtlasDao.updateAppStatus(appName, integers[0]);
            return null;
        }
    }
}