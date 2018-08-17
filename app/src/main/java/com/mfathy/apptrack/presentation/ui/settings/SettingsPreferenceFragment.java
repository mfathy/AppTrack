package com.mfathy.apptrack.presentation.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.mfathy.apptrack.R;
import com.mfathy.apptrack.presentation.utils.PermissionUtils;
import com.mfathy.apptrack.service.DistractionModeService;

/**
 * Created by Mohammed Fathy on 15/08/2018.
 * dev.mfathy@gmail.com
 *
 * Settings fragment has 2 settings:
 * 1- Enable/disable distraction mode.
 * 2- User custom waiting interval.
 */
public class SettingsPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_settings, rootKey);
    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        boolean distributionModeFlag = sharedPreferences.getBoolean(getString(R.string.key_switch_distraction_mode), false);
        Intent intent = new Intent(getActivity(), DistractionModeService.class);

        //  if distraction mode value changes >> true > start service | false > stop service
        if (key.equals(getString(R.string.key_switch_distraction_mode))) {
            startDistractionMode(distributionModeFlag, intent);
        }

        //  if user changed waiting interval >> restart service to reset the alarm.
        if (key.equals(getString(R.string.key_edit_distraction_mode_interval))) {
            stopService(intent);
            startDistractionMode(distributionModeFlag, intent);
        }
    }

    /**
     * Helper method to start Distraction mode after checking that user gave us permission.
     */
    private void startDistractionMode(boolean distributionModeFlag, Intent intent) {
        if (distributionModeFlag) {
            if (!PermissionUtils.hasPermission(mContext))
                PermissionUtils.alertUserToRequestPermission(mContext);
            else {
                startService(intent);
            }
        } else {
            stopService(intent);
        }
    }

    /**
     * Helper method to stop {@link DistractionModeService} itself.
     */
    private void stopService(Intent intent) {
        DistractionModeService.stop(mContext);
        mContext.stopService(intent);
    }

    /**
     * Helper method to start {@link DistractionModeService} itself.
     */
    private void startService(Intent intent) {
        intent.setAction(DistractionModeService.ACTION_START);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mContext.startForegroundService(intent);
        } else {
            mContext.startService(intent);
        }
    }

}
