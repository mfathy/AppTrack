package com.mfathy.apptrack.presentation.base;

import android.support.annotation.NonNull;

/**
 * Created by Mohammed Fathy on 13/08/2018.
 * dev.mfathy@gmail.com
 */
public interface BasePresenter<V extends BaseView> {

    void onBind(@NonNull V View);

    void onDestroy();
}
