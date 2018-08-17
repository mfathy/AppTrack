package com.mfathy.apptrack.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ServiceTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.mfathy.apptrack.presentation.utils.PermissionUtils;
import com.mfathy.data.Injection;
import com.mfathy.data.utils.AppExecutors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

/**
 * Created by Mohammed Fathy on 17/08/2018.
 * dev.mfathy@gmail.com
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class DistractionModeServiceTest {

    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void testDistractionModeServiceStartedSuccessfully() {

        DistractionModeService distractionModeService = new DistractionModeService(Injection.provideDataRepository(context, new AppExecutors()));
        Intent intent = new Intent(context, DistractionModeService.class);
        context.startService(intent);


        assertEquals(Service.START_STICKY, distractionModeService.onStartCommand(new Intent(), 0, 0));

    }

    @Test
    public void testGetUserPreferredIntervalReturnZero() {
        assertEquals(0L, DistractionModeService.getUserPreferredInterval(context));
    }

    @Test
    public void testGetCurrentPackageWillReturnMyPackageName() {
        PermissionUtils.requestPermission(context);
        DistractionModeService distractionModeService = new DistractionModeService(Injection.provideDataRepository(context, new AppExecutors()));
        assertEquals("com.android.settings", distractionModeService.getCurrentPackage(context));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldComplainIfServiceIsDestroyedWithRegisteredBroadcastReceivers() throws Exception {
        DistractionModeService service = new DistractionModeService(Injection.provideDataRepository(context, new AppExecutors()));
        Intent intent = new Intent(context, DistractionModeService.class);
        context.startService(intent);
        service.onDestroy();
    }
}