package com.mfathy.apptrack.data.local;

import android.support.annotation.NonNull;

import com.mfathy.apptrack.data.AppsDataSource;
import com.mfathy.apptrack.data.model.BlackListedApp;
import com.mfathy.apptrack.data.model.AppEntry;
import com.mfathy.apptrack.data.utils.AppExecutors;

import java.util.List;

import static com.mfathy.mutilites.utils.ValidationUtils.checkNotNull;


/**
 * Created by Mohammed Fathy on 14/08/2018.
 * dev.mfathy@gmail.com
 */
public class LocalDataSource implements AppsDataSource {

    private static volatile AppsDataSource INSTANCE;

    private AppsDao mAppsDao;

    private AppExecutors mAppExecutors;

    // Prevent direct instantiation.
    private LocalDataSource(@NonNull AppExecutors appExecutors,
                            @NonNull AppsDao appsDao) {
        mAppExecutors = appExecutors;
        mAppsDao = appsDao;
    }

    public static AppsDataSource getInstance(@NonNull AppsDao appsDao, @NonNull AppExecutors appExecutors) {
        if (INSTANCE == null) {
            synchronized (LocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalDataSource(appExecutors, appsDao);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getApplicationEntities(final List<AppEntry> data, @NonNull final LoadApplicationEntitiesCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<BlackListedApp> appEntities = mAppsDao.getBlackListedApps();
                mAppExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (appEntities.isEmpty()) {
                            // This will be called if the table is new or just empty.
                            callback.onAppsLoaded(data, null);
                        } else {
                            callback.onAppsLoaded(data, appEntities);
                        }
                    }
                });
            }
        };

        mAppExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteApplicationEntity(final String packageName) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                mAppsDao.deleteBlackListedById(packageName);
            }
        };

        mAppExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void saveApplicationEntity(final BlackListedApp blackListedApp) {
        checkNotNull(blackListedApp);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                mAppsDao.insertBlackListedApp(blackListedApp);
            }
        };
        mAppExecutors.diskIO().execute(saveRunnable);
    }

}
