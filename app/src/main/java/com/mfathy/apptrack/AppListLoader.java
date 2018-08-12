package com.mfathy.apptrack;

/**
 * Created by Mohammed Fathy on 12/08/2018.
 * dev.mfathy@gmail.com
 */

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A custom Loader that loads all of the installed applications.
 */
public class AppListLoader extends AsyncTaskLoader<List<AppEntry>> {
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
    final InterestingConfigChanges mLastConfig = new InterestingConfigChanges();
    final PackageManager mPm;
    List<AppEntry> mApps;
    PackageIntentReceiver mPackageObserver;

    public AppListLoader(Context context) {
        super(context);
        // Retrieve the package manager for later use; note we don't
        // use 'context' directly but instead the save global application
        // context returned by getContext().
        mPm = getContext().getPackageManager();
    }

    /**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public List<AppEntry> loadInBackground() {
        // Retrieve all known applications.
        List<ApplicationInfo> apps = null;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            apps = mPm.getInstalledApplications(PackageManager.GET_META_DATA);
        }
//        else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            apps = mPm.getInstalledApplications(
//                    PackageManager.MATCH_UNINSTALLED_PACKAGES);
//        }
        if (apps == null) {
            apps = new ArrayList<>();
        }
        final Context context = getContext();
        // Create corresponding array of entries and load their labels.
        List<AppEntry> entries = new ArrayList<>(apps.size());
        for (ApplicationInfo appInfo : apps) {
            if (mPm.getLaunchIntentForPackage(appInfo.packageName) != null) {
                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                    // system apps

                    AppEntry entry = new AppEntry(this, appInfo);
                    entry.loadLabel(context);
                    entries.add(entry);

                } else {
                    AppEntry entry = new AppEntry(this, appInfo);
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

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(List<AppEntry> apps) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (apps != null) {
                onReleaseResources(apps);
            }
        }
        List<AppEntry> oldApps = mApps;
        mApps = apps;
        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(apps);
        }
        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null) {
            onReleaseResources(oldApps);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
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

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(List<AppEntry> apps) {
        super.onCanceled(apps);
        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        super.onReset();
        // Ensure the loader is stopped
        onStopLoading();
        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mApps != null) {
            onReleaseResources(mApps);
            mApps = null;
        }
        // Stop monitoring for changes.
        if (mPackageObserver != null) {
            getContext().unregisterReceiver(mPackageObserver);
            mPackageObserver = null;
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<AppEntry> apps) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}
