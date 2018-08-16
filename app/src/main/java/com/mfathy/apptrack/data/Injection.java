package com.mfathy.apptrack.data;

import android.content.Context;

import com.mfathy.apptrack.data.local.AppsDatabase;
import com.mfathy.apptrack.data.local.LocalDataSource;
import com.mfathy.apptrack.data.utils.AppExecutors;

import static com.mfathy.mutilites.utils.ValidationUtils.checkNotNull;

/**
 * Created by Mohammed Fathy on 12/07/2018.
 * dev.mfathy@gmail.com
 * <p>
 * <p>
 * Simple dependency injection class.
 */
public class Injection {
    public static AppsDataRepository provideDataRepository(Context context, AppExecutors appExecutor) {
        checkNotNull(context);
        AppsDatabase database = AppsDatabase.getInstance(context);
        return AppsDataRepository.getInstance(LocalDataSource.getInstance(database.appsDao(), appExecutor));
    }
}
