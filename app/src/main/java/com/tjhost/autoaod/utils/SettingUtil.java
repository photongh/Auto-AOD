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

import com.tjhost.autoaod.Constants;
import com.tjhost.autoaod.services.KeyMonitorService;
import com.tjhost.autoaod.services.NotificationMonitorService;

public class SettingUtil {
    public static boolean isWritable (Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.System.canWrite(context);
        }
        return true;
    }

    public static final int MODE_AOD_ALWAYS_ON = 0;
    public static final int MODE_AOD_TAP_ON = 1;
    public static final int MODE_AOD_SCHEDULE = 2;

    private static Handler handler = new Handler(Looper.getMainLooper());
    public static boolean changeAodMode(Context context, int mode, int aodSwitch, int startTime, int endTime) {
        boolean r = false;
        switch (mode) {
            case MODE_AOD_ALWAYS_ON:
                // ignore aodSwitch, startTime, endTime
                r = Settings.System.putInt(context.getContentResolver(),
                        "aod_tap_to_show_mode", 0);
                r = Settings.System.putInt(context.getContentResolver(),
                        "aod_mode_start_time", 0);
                r = Settings.System.putInt(context.getContentResolver(),
                        "aod_mode_end_time", 0);
                Settings.System.putInt(context.getContentResolver(),
                        "aod_mode", 0);
                handler.postDelayed(() -> Settings.System.putInt(context.getContentResolver(),
                        "aod_mode", 1), 100);
                return r;
            case MODE_AOD_TAP_ON:
                // ignore startTime, endTime
                r = Settings.System.putInt(context.getContentResolver(), "aod_tap_to_show_mode", 1);
                r = Settings.System.putInt(context.getContentResolver(),
                        "aod_mode_start_time", 0);
                r = Settings.System.putInt(context.getContentResolver(),
                        "aod_mode_end_time", 0);
                break;
            case MODE_AOD_SCHEDULE:
                r = Settings.System.putInt(context.getContentResolver(), "aod_tap_to_show_mode", 0);
                r = Settings.System.putInt(context.getContentResolver(),
                        "aod_mode_start_time", startTime);
                r = Settings.System.putInt(context.getContentResolver(),
                        "aod_mode_end_time", endTime);
                break;
            default:break;
        }

        if (getCurrentAodState(context) == 1) {
            r = Settings.System.putInt(context.getContentResolver(),
                    "aod_mode", 0);
            if (aodSwitch == 1)
                handler.postDelayed(() -> Settings.System.putInt(context.getContentResolver(),
                    "aod_mode", aodSwitch), 100);
        } else {
            r = Settings.System.putInt(context.getContentResolver(),
                    "aod_mode", aodSwitch);
        }

        return r;
    }

    public static int getAodMode(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                "aod_mode", 0);
    }

    public static int getCurrentAodState(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                "aod_show_state", 0);
    }

    public static int getAodStartTime(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                "aod_mode_start_time", 0);
    }

    public static int getAodEndTime(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                "aod_mode_end_time", 0);
    }

    public static int getAodTapMode(Context context) {
        int mode = Settings.System.getInt(context.getContentResolver(),
                "aod_tap_to_show_mode", 1);
        int startTime = getAodStartTime(context);
        int endTime = getAodEndTime(context);
        switch (mode) {
            case 0:
                if (startTime == 0 && endTime == 0)
                    return MODE_AOD_ALWAYS_ON;
                return MODE_AOD_SCHEDULE;
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
        if (state == Display.STATE_ON || state == Display.STATE_ON_SUSPEND)
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

    public static boolean isScreenOnAndLocked(Context context) {
        return isScreenOn(context) && isScreenLocked(context);
    }

    // return 0 means failed
    public static int getScreenTimeoutTime(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, 0);
    }

    public static void refreshDebugState(boolean debug) {
        Constants.DEBUG = debug;
        NotificationMonitorService.DEBUG = debug;
        KeyMonitorService.DEBUG = debug;
    }

    public static boolean getDebugState() {
        return Constants.DEBUG;
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

    public static void lightScreenOn(Context context) {
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |  PowerManager.SCREEN_DIM_WAKE_LOCK,
                context.getPackageName()+":light_screen");
        wl.acquire(500L);
        wl.release();
    }
}
