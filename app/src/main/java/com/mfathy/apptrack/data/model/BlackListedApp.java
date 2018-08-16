package com.mfathy.apptrack.data.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * Created by Mohammed Fathy on 14/08/2018.
 * dev.mfathy@gmail.com
 * <p>
 * {@link BlackListedApp} is a model data class for room local database.
 */

@Entity(tableName = "BlackListedApps")
public class BlackListedApp {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "packageName")
    private String mPackageName;

    @ColumnInfo(name = "label")
    private String mLabel;

    /**
     * Use this constructor to create a new {@link BlackListedApp}.
     *
     * @param mPackageName AppTrack package name.
     * @param mLabel       AppTrack name.
     */
    public BlackListedApp(@NonNull String mPackageName, String mLabel) {
        this.mPackageName = mPackageName;
        this.mLabel = mLabel;
    }

    public BlackListedApp(@NonNull String currentPackage) {
        this.mPackageName = currentPackage;
    }

    @NonNull
    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(@NonNull String mPackageName) {
        this.mPackageName = mPackageName;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String mLabel) {
        this.mLabel = mLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlackListedApp blackListedApp = (BlackListedApp) o;
        return Objects.equals(mPackageName, blackListedApp.mPackageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mPackageName, mLabel);
    }
}
