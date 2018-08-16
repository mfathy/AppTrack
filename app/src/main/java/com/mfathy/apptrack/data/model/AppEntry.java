package com.mfathy.apptrack.data.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.mfathy.apptrack.data.loader.AppListLoader;

import java.io.File;

/**
 * Created by Mohammed Fathy on 12/08/2018.
 * dev.mfathy@gmail.com
 * <p>
 * {@link AppEntry} is a model data class for {@link AppListLoader}
 */
public class AppEntry{

    private final PackageManager mPackageManager;
    private final ApplicationInfo mInfo;
    private final AppListLoader mLoader;
    private final File mApkFile;
    private boolean mMounted;
    private Drawable mIcon;
    private String mLabel;

    public AppEntry(AppListLoader loader, PackageManager packageManager, ApplicationInfo info) {
        mApkFile = new File(info.sourceDir);
        mPackageManager = packageManager;
        mLoader = loader;
        mInfo = info;
    }

    public ApplicationInfo getApplicationInfo() {
        return mInfo;
    }

    public String getLabel() {
        return mLabel;
    }

    public Drawable getIcon() {
        if (mIcon == null) {
            if (mApkFile.exists()) {
                mIcon = mInfo.loadIcon(mPackageManager);
                return mIcon;
            } else {
                mMounted = false;
            }
        } else if (!mMounted) {
            // If the app wasn't mounted but is now mounted, reload
            // its icon.
            if (mApkFile.exists()) {
                mMounted = true;
                mIcon = mInfo.loadIcon(mPackageManager);
                return mIcon;
            }
        } else {
            return mIcon;
        }
        return mLoader.getContext().getResources().getDrawable(
                android.R.drawable.sym_def_app_icon);
    }

    @Override
    public String toString() {
        return mLabel;
    }

    public void loadLabel(Context context) {
        if (mLabel == null || !mMounted) {
            if (!mApkFile.exists()) {
                mMounted = false;
                mLabel = mInfo.packageName;
            } else {
                mMounted = true;
                CharSequence label = mInfo.loadLabel(context.getPackageManager());
                mLabel = label != null ? label.toString() : mInfo.packageName;
            }
        }
    }
}

