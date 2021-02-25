package com.tjhost.autoaod.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.tjhost.autoaod.Constants;
import com.tjhost.autoaod.R;
import com.tjhost.autoaod.utils.NotificationUtil;

public class ReInstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // install
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString();
            if (Constants.DEBUG) Log.d("ReInstallReceiver", "android.intent.action.PACKAGE_ADDED, " +
                    "package name = " + packageName);
        }
        // replace
        else if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
            String rawPackageName = intent.getDataString();
            if (Constants.DEBUG) Log.d("ReInstallReceiver", "android.intent.action.PACKAGE_REPLACED, " +
                    "raw package name name = " + rawPackageName);
            String packageName = "";
            try {
                packageName = rawPackageName.substring(8);
            } catch (Exception ignored) {}
            if (packageName.equals(context.getPackageName())) {
                if (Build.VERSION.SDK_INT == 30 && "samsung".equals(Build.BRAND)) {
                    if (Constants.DEBUG) Log.d("ReInstallReceiver", "prepare to show notification");
                    NotificationUtil.createTipsChannel(context);
                    NotificationUtil.showSimpleNotification(context, Constants.NOTIFICATION_CHANNEL_ID_TIPS,
                            R.string.tips_reboot_title, R.string.tips_reboot, -1, Constants.NOTI_ID_TIPS_REBOOT,
                            null);
                }
            }
        }
        // remove
        else if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            String packageName = intent.getDataString();
            if (Constants.DEBUG) Log.d("ReInstallReceiver", "android.intent.action.PACKAGE_REMOVED, " +
                    "package name = " + packageName);
        }
    }
}
