package com.mfathy.apptrack.presentation.ui.applist;

import com.mfathy.data.model.BlackListedApp;
import com.mfathy.data.model.AppEntry;
import com.mfathy.apptrack.presentation.base.BasePresenter;
import com.mfathy.apptrack.presentation.base.BaseView;

import java.util.List;

/**
 * Created by Mohammed Fathy on 16/08/2018.
 * dev.mfathy@gmail.com
 *
 * Applications list Contract.
 */
public class AppListContract {

    /**
     * View contract for {@link AppListFragment}
     */
    public interface View extends BaseView {

        /**
         * Render application list in the UI.
         * @param data application list from loader.
         * @param appEntities blacklisted apps.
         */
        void renderAppList(List<AppEntry> data, List<BlackListedApp> appEntities);

        /**
         * Hide application list.
         */
        void hideAppList();

    }

    /**
     * Presenter contract for {@link AppListPresenter}
     */
    public interface Presenter extends BasePresenter<AppListContract.View> {

        /**
         * Loads device [System | Installed] application list.
         */
        void loadDeviceApplicationList();

        /**
         * Add {@link AppEntry} to blacklist.
         * @param appEntry to be added to blacklist.
         */
        void addAppEntryToBlackList(AppEntry appEntry);

        /**
         * Loads blacklisted applications.
         * @param data >> device application list loaded from the {@link com.mfathy.data.loader.AppListLoader}
         */
        void getBlackListedApplications(List<AppEntry> data);

        /**
         * Remove {@link AppEntry} from blacklist.
         * @param appEntry to be removed from blacklist.
         */
        void deleteAppEntryToBlackList(AppEntry appEntry);
    }
}
