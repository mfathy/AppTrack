package com.mfathy.data;

import android.support.annotation.NonNull;

import com.mfathy.data.model.BlackListedApp;
import com.mfathy.data.model.AppEntry;

import java.util.List;

import static com.mfathy.mutilites.utils.ValidationUtils.checkNotNull;

/**
 * Created by Mohammed Fathy on 14/08/2018.
 * dev.mfathy@gmail.com
 */
public class AppsDataRepository implements AppsDataSource {

    private static AppsDataRepository INSTANCE = null;

    private final AppsDataSource mLocalDataSource;


    // Prevent direct instantiation.
    private AppsDataRepository(@NonNull AppsDataSource mLocalDataSource) {
        this.mLocalDataSource = ValidationUtils.checkNotNull(mLocalDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param mLocalDataSource the device storage data source
     * @return the {@link AppsDataRepository} instance
     */
    static AppsDataRepository getInstance(AppsDataSource mLocalDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new AppsDataRepository(mLocalDataSource);
        }
        return INSTANCE;
    }

    @Override
    public void getApplicationEntities(final List<AppEntry> data, @NonNull final LoadApplicationEntitiesCallback callback) {
        mLocalDataSource.getApplicationEntities(data, new LoadApplicationEntitiesCallback() {
            @Override
            public void onAppsLoaded(List<AppEntry> appEntries, List<BlackListedApp> appEntities) {
                if (appEntities != null && !appEntities.isEmpty())  // no applications blacklisted in database.
                    callback.onAppsLoaded(data, appEntities);
                else
                    callback.onAppsLoaded(data, null);
            }

            @Override
            public void onAppsNotAvailable() {
                callback.onAppsNotAvailable();
            }
        });
    }

    @Override
    public void saveApplicationEntity(BlackListedApp blackListedApp) {
        mLocalDataSource.saveApplicationEntity(blackListedApp);
    }

    @Override
    public void deleteApplicationEntity(String packageName) {
        mLocalDataSource.deleteApplicationEntity(packageName);
    }
}
