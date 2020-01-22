package com.software.pasithea.atlas;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.List;

class AtlasAppsManager {
    private static final String TAG = "AtlasAppsManager";

    private PackageManager mPackageManager = null;

    List<Apps> AppsList = null;

    public AtlasAppsManager(PackageManager packageManager){
        mPackageManager = packageManager;
    }

    protected List<Apps> getApps() {
        AppsList = new ArrayList<>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableApps = mPackageManager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableApps) {
            Apps app = new Apps();

            //get App package name
            app.label = ri.activityInfo.packageName;
            //get App name
            app.name = ri.loadLabel(mPackageManager);
            //get App icon
            app.icon = ri.loadIcon(mPackageManager);

            AppsList.add(app);
        }
        return AppsList;
    }
}