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

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {AppsEntity.class, StatusEntity.class, SensorEntity.class}, version = 1)
public abstract class AtlasDatabase extends RoomDatabase {
    /** TODO
     * Manage the database versioning before the production code is released
     */

    private static final String TAG = "MensaDatabase";

    private static AtlasDatabase mAtlasDatabase;
    public abstract AtlasDao databaseDao();
    private static Context mContext;

    public static synchronized AtlasDatabase getInstance(Context context){
        if (mAtlasDatabase == null){
            mContext = context;
            /**TODO
             * Remove allowMainThreadQueries before the poduction code is released.
             * This means to replace the direct query in the UserRepository by a background task.
             */
            mAtlasDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    AtlasDatabase.class, "mensa_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .allowMainThreadQueries() // For testing purpose only
                    .build();
        }
        return mAtlasDatabase;
    }

    private static Callback roomCallback = new Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(mAtlasDatabase, mContext).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private AtlasDao mAtlasDao;
        private Context mContext;

        public PopulateDbAsyncTask(AtlasDatabase db, Context context){
            mAtlasDao = db.databaseDao();
            mContext = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Populate the database with default values at in db creation time.
            // We don't manage the version yet so any changes in this part requires a reinstallation of the app.
            mAtlasDao.insertStatus(new StatusEntity("WakeupWordDetector", 2));
            return null;
        }
    }
}