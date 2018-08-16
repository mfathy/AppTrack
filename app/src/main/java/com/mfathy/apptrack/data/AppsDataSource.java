package com.mfathy.apptrack.data;

import android.support.annotation.NonNull;

import com.mfathy.apptrack.data.model.BlackListedApp;
import com.mfathy.apptrack.data.model.AppEntry;

import java.util.List;

/**
 * Created by Mohammed Fathy on 14/08/2018.
 * dev.mfathy@gmail.com
 */
public interface AppsDataSource {

    void getApplicationEntities(List<AppEntry> data, @NonNull LoadApplicationEntitiesCallback callback);
    void deleteApplicationEntity(String packageName);
    void saveApplicationEntity(BlackListedApp blackListedApp);

    interface LoadApplicationEntitiesCallback {
        void onAppsLoaded(List<AppEntry> appEntries, List<BlackListedApp> appEntities);
        void onAppsNotAvailable();
    }
}
