package com.mfathy.apptrack.exception;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.mfathy.apptrack.R;
import com.mfathy.data.exception.AppsNotAvailableException;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getContext;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Mohammed Fathy on 17/08/2018.
 * dev.mfathy@gmail.com
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ErrorMessageFactoryTest {


    @Test
    public void testAppsNotAvailableErrorMessage() {
        String expectedMessage = InstrumentationRegistry.getTargetContext().getString(R.string.exception_message_no_applications);
        String actualMessage = ErrorMessageFactory.create(InstrumentationRegistry.getTargetContext(),
                new AppsNotAvailableException());

        assertThat(actualMessage, is(equalTo(expectedMessage)));
    }

    @Test
    public void testGenericErrorMessage() {
        String expectedMessage = InstrumentationRegistry.getTargetContext().getString(R.string.exception_message_generic);
        String actualMessage = ErrorMessageFactory.create(InstrumentationRegistry.getTargetContext(), new Exception());

        assertThat(actualMessage, is(equalTo(expectedMessage)));
    }
}