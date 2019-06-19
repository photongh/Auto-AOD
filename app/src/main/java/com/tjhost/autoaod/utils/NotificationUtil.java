package com.tjhost.autoaod.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.tjhost.autoaod.Constants;
import com.tjhost.autoaod.R;

import static android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS;

public class NotificationUtil {

    public static void createNotificationChannel(String channelId, @NonNull String channelLabel, String channelDescription, Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelLabel, importance);
            if (channelDescription != null)
                channel.setDescription(channelDescription);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

    public static void createNotificationChannel(String channelId, @StringRes int channelLabelRes, @StringRes int channelDescriptionRes, Context context) {
        createNotificationChannel(channelId, context.getString(channelLabelRes),
                context.getString(channelDescriptionRes), context);
    }

    public static void showSimpleNotification(Context context, @NonNull String channelid, @NonNull String title,
                @NonNull String text, int smallIconRes, int notificationId, PendingIntent pendingIntent) {
        NotificationCompat.Builder n = new NotificationCompat.Builder(context, channelid)
                .setTimeoutAfter(5 * 60 * 1000L)
                .setContentTitle(title)
                .setContentText(text);
        if (smallIconRes <= 0)
            n.setSmallIcon(R.mipmap.ic_launcher);
        else
            n.setSmallIcon(smallIconRes);

        n.setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        if (pendingIntent != null)
            n.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, n.build());
    }

    public static void showSimpleNotification(Context context, @NonNull String channelid, @StringRes int titleRes,
                @StringRes int textRes, int smallIconRes, int notificationId, PendingIntent pendingIntent) {
        showSimpleNotification(context, channelid, context.getString(titleRes),
                context.getString(textRes), smallIconRes, notificationId, pendingIntent);
    }

    public static void dismissNotification(Context context, int notificationId){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.cancel(notificationId);
    }


    public static void createTipsChannel(Context context) {
        NotificationUtil.createNotificationChannel(Constants.NOTIFICATION_CHANNEL_ID_TIPS,
                R.string.notification_channel_tips_label,
                R.string.notification_channel_tips_description, context);
    }

    private static final int NOTI_ID_ACCESSIBILITY_SERVICE = 19918;
    public static void showAccessibilityServiceNotification(Context context) {
        createTipsChannel(context);

        Intent notifyIntent = new Intent(ACTION_ACCESSIBILITY_SETTINGS);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        showSimpleNotification(context, Constants.NOTIFICATION_CHANNEL_ID_TIPS,
                R.string.notification_accessibility_service_title,
                R.string.notification_accessibility_service_text,
                0, NOTI_ID_ACCESSIBILITY_SERVICE, notifyPendingIntent);
    }
}
