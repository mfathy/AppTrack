package com.mfathy.data;


import android.content.Context;

import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;

/**
 * Created by Mohammed Fathy on 17/08/2018.
 * dev.mfathy@gmail.com
 * Base class for Robolectric data layer tests.
 * Inherit from this class to create a test.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = ApplicationStub.class, sdk = 21)
public abstract class ApplicationTestCase {

    @Rule
    public TestRule injectMocksRule = new TestRule() {
        @Override
        public Statement apply(Statement base, Description description) {
            MockitoAnnotations.initMocks(ApplicationTestCase.this);
            return base;
        }
    };


    public static Context context() {
        return RuntimeEnvironment.application;
    }

    public static File cacheDir() {
        return RuntimeEnvironment.application.getCacheDir();
    }
}
