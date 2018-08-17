package com.mfathy.apptrack.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mfathy.apptrack.R;
import com.mfathy.apptrack.presentation.MainActivity;
import com.mfathy.apptrack.presentation.ui.settings.SettingsActivity;
import com.mfathy.data.AppsDataSource;
import com.mfathy.data.Injection;
import com.mfathy.data.exception.AppsNotAvailableException;
import com.mfathy.data.model.AppEntry;
import com.mfathy.data.model.BlackListedApp;
import com.mfathy.data.utils.AppExecutors;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.mfathy.apptrack.AppTrack.CHANNEL_ID;

/**
 * Created by Mohammed Fathy on 14/08/2018.
 * dev.mfathy@gmail.com
 * <p>
 * Background service to detect the current foreground application a user is using on the device.
 * In case the current foreground application is in a blacklist,
 * the user is notified and the app opens.
 * <p>
 * The user can specify how much minutes he can use blacklisted apps before detection.
 */
public class DistractionModeService extends Service {

    public static final String ACTION_START = ".action.start_distraction_mode_service";
    public static final String ACTION_STOP = ".action.stop_distraction_mode_service";
    public static final int FORE_GROUND_SERVICE_NOTIFICATION_ID = 101;
    private static final int BLACK_LIST_APP_NOTIFICATION_ID = 202;
    private static final long ONE_MINUTE_INTERVAL_MILLIS = 60000L;
    private static final String TAG = "DistractionModeService";
    private static final int REQUEST_CODE = 1001;
    private static PendingIntent distractionModeServiceIntent;
    private static boolean isAlarmStarted = false;
    private ScreenOnOffReceiver screenOnOffReceiver;
    private List<BlackListedApp> mBlackListedApps;
    private AppsDataSource mDataSource;
    private boolean destroy = false;


    public DistractionModeService() {
    }

    public DistractionModeService(AppsDataSource mDataSource) {
        this.mDataSource = mDataSource;
    }

    /**
     * Start a repeating alarm to detect foreground application every 1 minute or any custom duration entered by the user.
     *
     * @param context android context.
     */
    public static void start(Context context) {
        long userInterval = getUserPreferredInterval(context) <= 1 ? ONE_MINUTE_INTERVAL_MILLIS : getUserPreferredInterval(context) * ONE_MINUTE_INTERVAL_MILLIS;
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (am != null) {
            Log.d(TAG, String.format("Setting new alarm on: %s", DateFormat.getDateTimeInstance().format(new Date())));
            am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), userInterval, getRunIntent(context));
            isAlarmStarted = true;
        }
    }

    /**
     * Stop the repeating alarm mentioned in start();
     *
     * @param context android context
     */
    public static void stop(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (am != null) {
            am.cancel(getRunIntent(context));
            isAlarmStarted = false;
        }

    }

    /**
     * Returns a {@link PendingIntent} of {@link DistractionModeService} to run it by the alarm manager.
     *
     * @param context android context
     * @return PendingIntent of {@link DistractionModeService}
     */
    private static PendingIntent getRunIntent(Context context) {
        if (distractionModeServiceIntent == null) {
            Intent intent = new Intent(context, DistractionModeService.class);
            intent.setAction(ACTION_START);
            distractionModeServiceIntent = PendingIntent.getService(context, REQUEST_CODE, intent, 0);
        }
        return distractionModeServiceIntent;
    }

    /**
     * Gets user preferred interval waiting time.
     *
     * @param context android context.
     * @return How much minutes user wants to wait before notifying him.
     */
    @VisibleForTesting
    static long getUserPreferredInterval(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String userIntervalString = sharedPref.getString(context.getString(R.string.key_edit_distraction_mode_interval), "0");
        try {
            return Long.valueOf(userIntervalString);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
    //endregion

    //region Helper methods

    //region Service methods.
    @Override
    public void onCreate() {
        super.onCreate();
        initAppsDataSource();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        loadBlackListedApplications();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = getServiceNotification();
            startForeground(FORE_GROUND_SERVICE_NOTIFICATION_ID, notification);
        }

        if (intent == null || ACTION_START.equalsIgnoreCase(intent.getAction())) {
            if (!isAlarmStarted) {
                start(this);
                screenOnOffReceiver = new ScreenOnOffReceiver();
                registerReceiver(screenOnOffReceiver, ScreenOnOffReceiver.getIntentFilters());
            }
            checkCurrentForegroundApp();
        } else if (ACTION_STOP.equalsIgnoreCase(intent.getAction())) {
            stopAlarmAndStopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called - " + destroy);
        if (!destroy)
            start(this);

        destroy = false;

        try {
            unregisterReceiver(screenOnOffReceiver);
        } catch (IllegalArgumentException e) {
            //  Receiver not registered
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Initializes application local data source.
     */
    private void initAppsDataSource() {
        AppExecutors mAppExecutor = new AppExecutors();
        mDataSource = Injection.provideDataRepository(this, mAppExecutor);
    }

    /**
     * Load blacklisted applications from data source.
     */
    private void loadBlackListedApplications() {
        Log.d(TAG, "Getting blacklisted Apps");
        List<AppEntry> appEntries = new ArrayList<>();
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.sourceDir = "";
        appEntries.add(new AppEntry(null, null, applicationInfo));
        mDataSource.getApplicationEntities(appEntries, new AppsDataSource.LoadApplicationEntitiesCallback() {
            @Override
            public void onAppsLoaded(List<AppEntry> appEntries, List<BlackListedApp> blackListedApps) {
                if (blackListedApps == null)
                    mBlackListedApps = new ArrayList<>();
                else
                    mBlackListedApps = blackListedApps;
            }

            @Override
            public void onAppsNotAvailable(AppsNotAvailableException e) {
                //  nothing to do here
            }
        });
    }

    /**
     * Checks current running foreground application.
     */
    private void checkCurrentForegroundApp() {
        String currentPackage = getCurrentPackage(this);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (mBlackListedApps != null && !mBlackListedApps.isEmpty() && mBlackListedApps.contains(new BlackListedApp(currentPackage))) {
            getBlackListedAppNotification();
            startActivity(intent);
            Log.d(TAG, String.format("%s is black listed!", currentPackage));
        } else {
            Log.d(TAG, String.format("%s is not black listed!", currentPackage));
        }
    }

    /**
     * Returns current foreground application package name.
     *
     * @return package name.
     */
    @VisibleForTesting
    String getCurrentPackage(Context context) {
        UsageStatsManager mUsageStatsManager;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

            long time = System.currentTimeMillis();
            if (mUsageStatsManager != null) {
                List<UsageStats> appList = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
                if (appList != null && appList.size() > 0) {
                    SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
                    for (UsageStats usageStats : appList) {
                        mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                    }
                    if (!mySortedMap.isEmpty()) {
                        return mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                    }
                }
            }
        }

        return "";
    }

    /**
     * Stops {@link DistractionModeService} itself and the repeating Alarm.
     */
    private void stopAlarmAndStopSelf() {
        destroy = true;
        stop(this);
        stopSelf();
    }
    //endregion

    //region Notifications

    /**
     * Builds a foreground service notification object required by Android Oreo 8.0
     *
     * @return notification object.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getServiceNotification() {
        // Create intent that will bring our app to the front, as if it was tapped in the app launcher
        Intent notificationIntent = new Intent(this, SettingsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText(getString(R.string.notify_android_o_notification_content))
                .setContentTitle(getString(R.string.notify_android_o_notification_title))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
    }

    /**
     * Builds a blacklist application notification object to notify the user that he is using a blacklisted App.
     */
    private void getBlackListedAppNotification() {
        NotificationManager mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification appNotification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText(getString(R.string.notify_blacklisted_app_notification_content))
                .setContentTitle(getString(R.string.notify_blacklisted_app_notification_title))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();

        if (mNotifyManager != null) {
            mNotifyManager.notify(BLACK_LIST_APP_NOTIFICATION_ID, appNotification);
        }
    }
    //endregion
}
