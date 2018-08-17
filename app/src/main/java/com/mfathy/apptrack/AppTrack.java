package com.mfathy.apptrack;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/**
 * Created by Mohammed Fathy on 16/08/2018.
 * dev.mfathy@gmail.com
 * <p>
 * Custom {@link Application} class.
 */
public class AppTrack extends Application {
    public static final String CHANNEL_ID = ".id.notification.channel.01";

    @Override
    public void onCreate() {
        super.onCreate();

        initNotificationChannel();
    }

    /**
     * Initialize Notification channel used by the application.
     */
    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }
    }
}
