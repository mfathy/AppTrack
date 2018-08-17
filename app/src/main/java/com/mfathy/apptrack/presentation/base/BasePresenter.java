package com.mfathy.apptrack.presentation.base;

import android.support.annotation.NonNull;

/**
 * Created by Mohammed Fathy on 13/08/2018.
 * dev.mfathy@gmail.com
 * <p>
 * {@link BasePresenter} is a contract interface for every presenter used in the App.
 */
public interface BasePresenter<V extends BaseView> {

    /**
     * Bind or attach a {@link BaseView} to the presenter.
     *
     * @param View to be binded or attached.
     */
    void onBind(@NonNull V View);

    /**
     * Destroy the presenter, also remove any resources attached to it.
     */
    void onDestroy();
}
