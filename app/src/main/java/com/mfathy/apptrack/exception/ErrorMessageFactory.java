package com.mfathy.apptrack.exception;


/**
 * Created by Mohammed Fathy on 17/08/2018.
 * dev.mfathy@gmail.com
 */

import android.content.Context;

import com.mfathy.apptrack.R;
import com.mfathy.data.exception.AppsNotAvailableException;

/**
 * Factory used to create error messages from an Exception as a condition.
 */
public class ErrorMessageFactory {

    private ErrorMessageFactory() {
        //empty
    }

    /**
     * Creates a String representing an error message.
     *
     * @param context   Context needed to retrieve string resources.
     * @param exception An exception used as a condition to retrieve the correct error message.
     * @return {@link String} an error message.
     */
    public static String create(Context context, Exception exception) {
        String message = context.getString(R.string.exception_message_generic);

        if (exception instanceof AppsNotAvailableException) {
            message = context.getString(R.string.exception_message_no_applications);
        }

        return message;
    }
}
