package com.mfathy.data;

import android.content.Context;

import com.mfathy.data.local.AppsDatabase;
import com.mfathy.data.local.LocalDataSource;
import com.mfathy.data.utils.AppExecutors;

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
        ValidationUtils.checkNotNull(context);
        AppsDatabase database = AppsDatabase.getInstance(context);
        return AppsDataRepository.getInstance(LocalDataSource.getInstance(database.appsDao(), appExecutor));
    }
}
