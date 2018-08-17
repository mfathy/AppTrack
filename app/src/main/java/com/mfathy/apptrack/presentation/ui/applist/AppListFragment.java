package com.mfathy.apptrack.presentation.ui.applist;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mfathy.apptrack.R;
import com.mfathy.data.AppsDataRepository;
import com.mfathy.data.Injection;
import com.mfathy.data.model.BlackListedApp;
import com.mfathy.data.model.AppEntry;
import com.mfathy.data.utils.AppExecutors;
import com.mfathy.apptrack.presentation.utils.PermissionUtils;
import com.mfathy.apptrack.presentation.ui.settings.SettingsActivity;
import com.mfathy.apptrack.service.DistractionModeService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohammed Fathy on 12/08/2018.
 * dev.mfathy@gmail.com
 *
 * {@link AppListFragment} is application list view.
 */
public class AppListFragment extends Fragment implements AppListContract.View, AppListAdapterInteractions {

    @VisibleForTesting
    static final int REQUEST_USAGE_CODE = 10101;
    private static final int MENU_ID = 1011;
    private ProgressBar mProgressBarLoading;
    private TextView mTextViewErrorLabel;
    private AppListPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private AppListAdapter mAdapter;

    //region Fragment|Activity methods
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_application_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppExecutors mAppExecutor = new AppExecutors();
        AppsDataRepository appsDataRepository = Injection.provideDataRepository(getActivity(), mAppExecutor);
        mPresenter = new AppListPresenter(appsDataRepository, getLoaderManager(), getActivity());
        mPresenter.onBind(this);

        initViews(view);

        hideAppList();
        showLoading();
        mPresenter.loadDeviceApplicationList();

        startDistractionMode();
    }

    /**
     * Fragment view initialization
     * @param view to be initialized.
     */
    private void initViews(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mProgressBarLoading = view.findViewById(R.id.progressBar_loading);
        mTextViewErrorLabel = view.findViewById(R.id.textView_error_label);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new AppListAdapter(new ArrayList<AppEntry>(), this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem itemSettings = menu.add(MENU_ID, MENU_ID, 0, getString(R.string.label_settings));
        itemSettings.setIcon(R.drawable.ic_action_settings);
        itemSettings.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM
                | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ID:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivityForResult(intent, REQUEST_USAGE_CODE);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_USAGE_CODE:
                startDistractionMode();
                break;
        }
    }

    @Override
    public void onDestroy() {
        mPresenter.onDestroy();
        super.onDestroy();
    }

    //endregion

    //region View methods
    @Override
    public void showLoading() {
        mProgressBarLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mProgressBarLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String message) {
        mTextViewErrorLabel.setVisibility(View.VISIBLE);
        mTextViewErrorLabel.setText(message);
    }

    @Override
    public void hideError() {
        mTextViewErrorLabel.setVisibility(View.INVISIBLE);
    }

    @Override
    public void renderAppList(List<AppEntry> data, List<BlackListedApp> appEntities) {
        mAdapter.setData(data, appEntities);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAppList() {
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public Context context() {
        return getActivity();
    }
    //endregion

    //region AppListAdapterInteractions methods
    @Override
    public void onToggleBlackListButton(AppEntry appEntry, boolean isChecked) {

        if (isChecked) mPresenter.addAppEntryToBlackList(appEntry);
        else mPresenter.deleteAppEntryToBlackList(appEntry);
    }
    //endregion

    //region Helper methods
    /**
     * Helper method to start Distraction mode after checking that user gave us permission.
     */
    private void startDistractionMode() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean switchPref = sharedPref.getBoolean(getString(R.string.key_switch_distraction_mode), false);

        if (switchPref) {
            if (!PermissionUtils.hasPermission(getActivity()))
                PermissionUtils.alertUserToRequestPermission(getActivity());
            else {
                startService();
            }
        }
    }

    /**
     * Helper method to start {@link DistractionModeService} itself.
     */
    private void startService() {
        Intent intent = new Intent(getActivity(), DistractionModeService.class);
        intent.setAction(DistractionModeService.ACTION_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(getActivity(), intent);
        } else {
            getActivity().startService(intent);
        }
    }
    //endregion
}
