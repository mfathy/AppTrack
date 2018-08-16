package com.mfathy.data.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.mfathy.data.model.BlackListedApp;

/**
 * Created by Mohammed Fathy on 14/08/2018.
 * dev.mfathy@gmail.com
 * <p>
 * The Room Database that contains the Applications table.
 */
@Database(entities = {BlackListedApp.class}, version = 1, exportSchema = false)
public abstract class AppsDatabase extends RoomDatabase {

    private static final Object sLock = new Object();
    private static AppsDatabase INSTANCE;

    public static AppsDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppsDatabase.class, "Apps.db").build();
            }
            return INSTANCE;
        }
    }

    public abstract AppsDao appsDao();
}
