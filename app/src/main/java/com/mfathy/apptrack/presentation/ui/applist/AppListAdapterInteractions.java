package com.mfathy.apptrack.presentation.ui.applist;

import com.mfathy.data.model.AppEntry;

/**
 * Created by Mohammed Fathy on 14/08/2018.
 * dev.mfathy@gmail.com
 *
 * {@link AppListAdapter} interactions interface listener to pass actions to attached View [Activity | Fragment].
 */
public interface AppListAdapterInteractions {
    /**
     * Blacklist | un-Blacklist this application.
     * @param appEntry to be added to blacklist.
     * @param isChecked >> true to add appEntry to blacklist, false to remove it.
     */
    void onToggleBlackListButton(AppEntry appEntry, boolean isChecked);
}
