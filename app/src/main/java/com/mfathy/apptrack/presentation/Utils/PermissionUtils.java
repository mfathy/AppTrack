package com.mfathy.apptrack.presentation.Utils;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import com.mfathy.apptrack.R;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Mohammed Fathy on 15/08/2018.
 * dev.mfathy@gmail.com
 */
public class PermissionUtils {
    public static void requestPermission(Context context) {
        Intent intent = new Intent(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static boolean hasPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        if (appOps != null) {
            int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), context.getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        }
        return false;
    }

    public static void alertUserToRequestPermission(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.message_request_usage_access).setTitle(R.string.app_name);

        builder.setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                PermissionUtils.requestPermission(context);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
