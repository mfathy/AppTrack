package com.mfathy.apptrack.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Created by Mohammed Fathy on 14/08/2018.
 * dev.mfathy@gmail.com
 *
 * Broadcast Receiver to listen to Screen ON | OFF changes.
 */
public class ScreenOnOffReceiver extends BroadcastReceiver {

    private static final String TAG = "DistractionModeService";

    public static IntentFilter getIntentFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        return filter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            Log.d(TAG, "ACTION_SCREEN_ON");
            DistractionModeService.start(context);
        } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            Log.d(TAG, "ACTION_SCREEN_OFF");
            DistractionModeService.stop(context);
        }
    }
}
