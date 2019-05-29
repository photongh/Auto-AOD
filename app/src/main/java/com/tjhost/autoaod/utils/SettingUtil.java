package com.tjhost.autoaod.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Display;

import java.util.Set;

import androidx.core.app.NotificationManagerCompat;

public class SettingUtil {
    public static boolean isWritable (Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.System.canWrite(context);
        }
        return true;
    }

    public static final int MODE_AOD_ALWAYS_ON = 0;
    public static final int MODE_AOD_TAP_ON = 1;

    private static Handler handler = new Handler(Looper.getMainLooper());
    public static boolean changeAodMode(Context context, int mode) {
        boolean r = false;
        switch (mode) {
            case MODE_AOD_ALWAYS_ON:
                r = Settings.System.putInt(context.getContentResolver(),
                        "aod_tap_to_show_mode", 0);
                Settings.System.putInt(context.getContentResolver(),
                        "aod_mode", 0);
                handler.postDelayed(() -> Settings.System.putInt(context.getContentResolver(),
                        "aod_mode", 1), 100);
                break;
            case MODE_AOD_TAP_ON:
                r = Settings.System.putInt(context.getContentResolver(), "aod_tap_to_show_mode", 1);
                break;
            default:break;
        }

        return r;
    }

    public static int getAodMode(Context context) {
        int mode = Settings.System.getInt(context.getContentResolver(),
                "aod_tap_to_show_mode", 1);
        switch (mode) {
            case 0:
                return MODE_AOD_ALWAYS_ON;
            case 1:
            default:
                return MODE_AOD_TAP_ON;
        }
    }

    public static boolean isScreenOn(Context context) {
        DisplayManager powerManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE); //true为打开，false为关闭
        if (powerManager == null) return false;
        Display[] displays = powerManager.getDisplays();
        if (displays == null || displays.length == 0) return false;
        int state = displays[0].getState();
        if (state != Display.STATE_OFF && state != Display.STATE_UNKNOWN)
            return true;
        return false;
    }

    public static boolean isScreenLocked(Context context) {
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (mKeyguardManager == null) return false;
        return  mKeyguardManager.isKeyguardLocked();
    }

    public static boolean isScreenOnAndUnlocked(Context context) {
        return isScreenOn(context) && !isScreenLocked(context);
    }

    public static boolean isNotificationPermissionGranted(Context context) {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(context);
        if (packageNames.contains(context.getPackageName())) {
            return true;
        }
        return false;
    }

    public static void openNotificationPermissionSettings(Context context) {
        try {
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            } else {
                intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            }
            context.startActivity(intent);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
