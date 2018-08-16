package com.mfathy.apptrack.presentation.ui.applist;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.mfathy.data.AppsDataSource;
import com.mfathy.data.loader.AppListLoader;
import com.mfathy.data.model.BlackListedApp;
import com.mfathy.data.model.AppEntry;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.mfathy.mutilites.utils.ValidationUtils.checkNotNull;

/**
 * Created by Mohammed Fathy on 13/08/2018.
 * dev.mfathy@gmail.com
 */
public class AppListPresenter implements AppListContract.Presenter, LoaderManager.LoaderCallbacks<List<AppEntry>> {

    private WeakReference<AppListContract.View> mView;
    private final AppsDataSource mDataSource;
    private final LoaderManager mLoaderManager;
    private Context mContext;

    AppListPresenter(AppsDataSource mDataSource, LoaderManager manager, Context context) {
        this.mDataSource = checkNotNull(mDataSource);
        mContext = context;
        mLoaderManager = manager;
    }

    @Override
    public void onBind(@NonNull AppListContract.View view) {
        checkNotNull(view);
        mView = new WeakReference<>(view);
    }

    @Override
    public void onDestroy() {
        if (mView != null) mView.clear();
    }

    @Override
    public void getDeviceApplicationList() {
        mLoaderManager.initLoader(0, null, this);
    }

    @Override
    public Loader<List<AppEntry>> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.
        return new AppListLoader(mContext);
    }

    @Override
    public void onLoadFinished(Loader<List<AppEntry>> loader, List<AppEntry> data) {
        // Set the new data in the adapter.
        getBlackListedApplications(data);
    }

    @Override
    public void onLoaderReset(Loader<List<AppEntry>> loader) {
        // Clear the data in the adapter.
        if (mView != null && mView.get() != null) mView.get().renderAppList(null, null);
    }

    @Override
    public void addAppEntryToBlackList(AppEntry appEntry) {
        BlackListedApp blackListedApp = new BlackListedApp(appEntry.getApplicationInfo().packageName, appEntry.getLabel());
        mDataSource.saveApplicationEntity(blackListedApp);
    }

    @Override
    public void getBlackListedApplications(final List<AppEntry> data) {
        mDataSource.getApplicationEntities(data, new AppsDataSource.LoadApplicationEntitiesCallback() {
            @Override
            public void onAppsLoaded(List<AppEntry> appEntries, List<BlackListedApp> appEntities) {
                if (mView != null && mView.get() != null) {
                    mView.get().hideLoading();
                    mView.get().hideError();
                    mView.get().renderAppList(data, appEntities);
                }
            }

            @Override
            public void onAppsNotAvailable() {
                if (mView != null && mView.get() != null) {
                    mView.get().hideLoading();
                    mView.get().showError("No Applications!");
                }
            }
        });
    }

    @Override
    public void deleteAppEntryToBlackList(AppEntry appEntry) {
        mDataSource.deleteApplicationEntity(appEntry.getApplicationInfo().packageName);
    }
}
