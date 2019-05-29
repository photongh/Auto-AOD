package com.tjhost.autoaod.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

@Deprecated
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
//        }
    }

    private void startAODService(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(new Intent(context, NotificationMonitorService.class));
//        } else {
//            context.startService(new Intent(context, NotificationMonitorService.class));
//        }
    }
}
