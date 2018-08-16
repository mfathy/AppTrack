package com.mfathy.apptrack.presentation.ui.applist;

import com.mfathy.data.model.AppEntry;

/**
 * Created by Mohammed Fathy on 14/08/2018.
 * dev.mfathy@gmail.com
 *
 * {@link AppListAdapter} interactions interface.
 */
public interface AppListAdapterInteractions {
    void onToggleBlackListButton(AppEntry appEntry, boolean isChecked);
}
