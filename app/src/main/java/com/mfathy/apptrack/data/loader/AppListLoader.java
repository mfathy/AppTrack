package com.mfathy.apptrack.data.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.mfathy.apptrack.data.helper.InterestingConfigChanges;
import com.mfathy.apptrack.data.helper.PackageIntentReceiver;
import com.mfathy.apptrack.data.model.AppEntry;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Mohammed Fathy on 12/08/2018.
 * dev.mfathy@gmail.com
 * A custom Loader that loads all of the installed applications.
 */
public class AppListLoader extends AsyncTaskLoader<List<AppEntry>> {

    private final InterestingConfigChanges mLastConfig = new InterestingConfigChanges();
    private PackageIntentReceiver mPackageObserver;
    private final PackageManager mPackageManager;
    private List<AppEntry> mApps;

    public AppListLoader(Context context) {
        super(context);
        // Retrieve the package manager for later use.
        mPackageManager = getContext().getPackageManager();
    }

    @Override
    public List<AppEntry> loadInBackground() {
        // Retrieve all known applications.
        final Context context = getContext();
        List<ApplicationInfo> apps = null;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            apps = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA | PackageManager.GET_UNINSTALLED_PACKAGES);
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apps = mPackageManager.getInstalledApplications(
                    PackageManager.MATCH_UNINSTALLED_PACKAGES);
        }

        if (apps == null) {
            apps = new ArrayList<>();
        }

        // Create corresponding array of entries and load their labels.
        List<AppEntry> entries = new ArrayList<>(apps.size());
        for (ApplicationInfo appInfo : apps) {

            //  Don't add my app to the list.
            if (appInfo.packageName.equals(context.getPackageName())) continue;

            //  We need only System apps and installed apps, so add them to the list.
            if (mPackageManager.getLaunchIntentForPackage(appInfo.packageName) != null) {
                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    //  System Apps
                    AppEntry entry = new AppEntry(this, mPackageManager, appInfo);
                    entry.loadLabel(context);
                    entries.add(entry);

                } else {
                    //  Installed Apps
                    AppEntry entry = new AppEntry(this, mPackageManager, appInfo);
                    entry.loadLabel(context);
                    entries.add(entry);
                }
            }
        }

        // Sort the list.
        Collections.sort(entries, ALPHA_COMPARATOR);

        // Done!
        return entries;
    }

    @Override
    public void deliverResult(List<AppEntry> apps) {
        mApps = apps;
        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(apps);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mApps != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mApps);
        }
        // Start watching for changes in the app data.
        if (mPackageObserver == null) {
            mPackageObserver = new PackageIntentReceiver(this);
        }
        // Has something interesting in the configuration changed since we
        // last built the app list?
        boolean configChange = mLastConfig.applyNewConfig(getContext().getResources());
        if (takeContentChanged() || mApps == null || configChange) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(List<AppEntry> apps) {
        super.onCanceled(apps);
    }

    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mApps != null) {
            mApps = null;
        }
        // Stop monitoring for changes.
        if (mPackageObserver != null) {
            getContext().unregisterReceiver(mPackageObserver);
            mPackageObserver = null;
        }
    }

    /**
     * Perform alphabetical comparison of application entry objects.
     */
    private static final Comparator<AppEntry> ALPHA_COMPARATOR = new Comparator<AppEntry>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(AppEntry object1, AppEntry object2) {
            return sCollator.compare(object1.getLabel(), object2.getLabel());
        }
    };
}
