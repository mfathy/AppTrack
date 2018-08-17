package com.mfathy.apptrack.presentation.base;

import android.content.Context;

/**
 * Created by Mohammed Fathy on 13/08/2018.
 * dev.mfathy@gmail.com
 * <p>
 * {@link BaseView} is a contract interface for every View used in the App.
 */
public interface BaseView {
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
     * Get a {@link android.content.Context}.
     */
    Context context();
}
