package com.tjhost.autoaod.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.tjhost.autoaod.R;

// ############################################################
//    screen off edge light has worked on 0.9.0, next to do
//    is to just copy a notification info from the source app
//    But I don't need this feature, so I will never implement
//    if. if you are interested in it, pls work on it.
// ############################################################

public class EdgeLightUtil {

    private void showTestNotification(Context context) {
        NotificationCompat.Builder n = new NotificationCompat.Builder(context, "edge_light")
                .setContentTitle("test title")
                .setContentText("content")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(152, n.build());
    }

    private void dismissTestNotification(Context context){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.cancel(152);
    }

    private void createNotificationChannel(String channelId, Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "edge light";
            String description = "edge light description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
