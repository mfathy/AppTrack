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


    public interface View extends BaseView {
        /**
         * Show a view with a progress bar indicating a loading process.
         */
        void showLoading();

        /**
         * Hide a loading view.
         */
        void hideLoading();

        /**
         * Show an error message
         *
         * @param message A string representing an error.
         */
        void showError(String message);

        /**
         * Hide an error message
         */
        void hideError();

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

    public interface Presenter extends BasePresenter<AppListContract.View> {

        void getDeviceApplicationList();

        void addAppEntryToBlackList(AppEntry appEntry);

        void getBlackListedApplications(List<AppEntry> data);

        void deleteAppEntryToBlackList(AppEntry appEntry);
    }
}
