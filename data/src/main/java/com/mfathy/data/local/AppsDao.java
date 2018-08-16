package com.mfathy.data.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.mfathy.data.model.BlackListedApp;

import java.util.List;

/**
 * Created by Mohammed Fathy on 13/08/2018.
 * dev.mfathy@gmail.com
 * Data Access Object for the Black listed applications table.
 */
@Dao
public interface AppsDao {

    /**
     * Select all {@link BlackListedApp} from the app_entities table.
     *
     * @return all app_entities.
     */
    @Query("SELECT * FROM BlackListedApps")
    List<BlackListedApp> getBlackListedApps();

    /**
     * Insert an {@link BlackListedApp} in the database. If the blackListedApp already exists, replace it.
     *
     * @param blackListedApp the blackListedApp to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBlackListedApp(BlackListedApp blackListedApp);

    /**
     * Delete an {@link BlackListedApp} by packageName.
     *
     * @return the number of app_entities deleted. This should always be 1.
     */
    @Query("DELETE FROM BlackListedApps WHERE packageName = :packageName")
    int deleteBlackListedById(String packageName);

}
