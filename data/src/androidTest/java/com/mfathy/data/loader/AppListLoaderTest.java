package com.mfathy.data.loader;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.runner.AndroidJUnit4;
import android.test.LoaderTestCase;

import com.mfathy.data.model.AppEntry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Mohammed Fathy on 17/08/2018.
 * dev.mfathy@gmail.com
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class AppListLoaderTest extends LoaderTestCase {

    private Context mContext;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testLoadInBackgroundReturnListOfApps() {

        AppListLoader appListLoader = new AppListLoader(mContext);

        List<AppEntry> appEntryList = appListLoader.loadInBackground();

        Assert.assertNotNull(appEntryList);
        assertTrue(appEntryList.stream().anyMatch(new Predicate<AppEntry>() {
            @Override
            public boolean test(AppEntry appEntry) {
                return "AppTrack".equals(appEntry.getLabel());
            }
        }));
    }
}